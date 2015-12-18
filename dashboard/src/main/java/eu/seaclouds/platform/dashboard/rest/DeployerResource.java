/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.seaclouds.platform.dashboard.rest;


import com.codahale.metrics.annotation.Timed;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationData;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationDataStorage;
import eu.seaclouds.platform.dashboard.proxy.DeployerProxy;
import eu.seaclouds.platform.dashboard.proxy.MonitorProxy;
import eu.seaclouds.platform.dashboard.proxy.PlannerProxy;
import eu.seaclouds.platform.dashboard.proxy.SlaProxy;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("/deployer")
public class DeployerResource implements Resource{
    private static final Logger LOG = LoggerFactory.getLogger(DeployerResource.class);

    private final DeployerProxy deployer;
    private final MonitorProxy monitor;
    private final SlaProxy sla;
    private final PlannerProxy planner;
    private final SeaCloudsApplicationDataStorage dataStore;

    public DeployerResource(DeployerProxy deployerProxy, MonitorProxy monitorProxy, SlaProxy slaProxy, PlannerProxy planner) {
        deployer = deployerProxy;
        monitor = monitorProxy;
        sla = slaProxy;
        this.planner = planner;
        dataStore = SeaCloudsApplicationDataStorage.getInstance();
    }

    private void cleanUpApplicationDependencies(SeaCloudsApplicationData seaCloudsApplicationData) {

        // TODO: Undo observers (Maybe they are removed when MR are removed)


        // TODO: Undo grafana


        try {
            for (String ruleId : seaCloudsApplicationData.getMonitoringRulesIds()) {
                this.monitor.removeMonitoringRule(ruleId);
            }
        } catch (Exception e) {
            DeployerResource.LOG.debug("Something went wrong during the cleanup of the monitoring rules");
            // This is perfectly fine, it will happen if this phase was not reached before the error.
        }

        try {
            this.sla.removeAgreement(seaCloudsApplicationData.getAgreementId());
        } catch (Exception e) {
            DeployerResource.LOG.debug("Something went wrong during the cleanup of the agreement");
            // This is perfectly fine, it will happen if this phase was not reached before the error.
        }

        try {
            this.deployer.removeApplication(seaCloudsApplicationData.getDeployerApplicationId());
        } catch (Exception e) {
            DeployerResource.LOG.debug("Something went wrong during the cleanup of the application");
            // This is perfectly fine, it will happen if this phase was not reached before the error.
        }

    }

    @POST
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications")
    public Response addApplication(String dam) {
        SeaCloudsApplicationData seaCloudsApplication = null;

        if (dam == null) {
            DeployerResource.LOG.error("Missing input parameters");
            return Response.status(Status.NOT_ACCEPTABLE).build();
        } else {
            try {
                DeployerResource.LOG.debug("Deploy new application process started");

                seaCloudsApplication = new SeaCloudsApplicationData(dam);

                DeployerResource.LOG.debug("STEP 1: Start deployment of the application");
                TaskSummary taskSummary = this.deployer.deployApplication(dam);
                seaCloudsApplication.setDeployerApplicationId(taskSummary);

                DeployerResource.LOG.debug("STEP 2: Retrieve Monitoring Rules from TOSCA");
                MonitoringRules monitoringRules = this.planner.getMonitoringRulesByTemplateId(seaCloudsApplication.getMonitoringRulesTemplateId());

                DeployerResource.LOG.debug("STEP 3: Install Monitoring Rules");
                this.monitor.addMonitoringRules(monitoringRules);
                seaCloudsApplication.setMonitoringRulesIds(monitoringRules);

                DeployerResource.LOG.debug("STEP 4: Retrieve SLA Agreements from TOSCA");
                Agreement agreement = this.sla.getAgreementByTemplateId(seaCloudsApplication.getAgreementTemplateId());

                DeployerResource.LOG.debug("STEP 5: Install SLA Agreements");
                this.sla.addAgreement(agreement);
                seaCloudsApplication.setAgreementId(agreement);

                DeployerResource.LOG.debug("STEP 6: Notify Rules Ready (Issue #56)");
                this.sla.notifyRulesReady(agreement);

                DeployerResource.LOG.debug("Application deployment process finished");
                this.dataStore.addSeaCloudsApplicationData(seaCloudsApplication);
                return Response.ok(seaCloudsApplication).build();
            } catch (Exception e) {
                this.cleanUpApplicationDependencies(seaCloudsApplication);
                DeployerResource.LOG.error(e.getMessage());
                return Response.status(Status.BAD_REQUEST).build();
            }
        }
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications")
    public Response listApplications() {
        return Response.ok(this.dataStore.listSeaCloudsApplicationData()).build();
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications/{seaCloudsId}")
    public Response getApplication(@PathParam("seaCloudsId") String seaCloudsId) throws IOException {
        if (seaCloudsId == null) {
            DeployerResource.LOG.error("Missing input parameters");
            return Response.status(Status.NOT_ACCEPTABLE).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = this.dataStore.getSeaCloudsApplicationDataById(seaCloudsId);

            if (seaCloudsApplicationData == null) {
                return Response.status(Status.BAD_REQUEST).build();
            }

            return Response.ok(this.deployer.getApplication(seaCloudsApplicationData.getDeployerApplicationId())).build();
        }
    }

    @DELETE
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications/{seaCloudsId}")
    public Response removeApplication(@PathParam("seaCloudsId") String seaCloudsId) {
        if (seaCloudsId == null) {
            DeployerResource.LOG.error("Missing input parameters");
            return Response.status(Status.NOT_ACCEPTABLE).build();
        }

        SeaCloudsApplicationData seaCloudsApplicationData = this.dataStore.getSeaCloudsApplicationDataById(seaCloudsId);

        if (seaCloudsApplicationData == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        this.cleanUpApplicationDependencies(seaCloudsApplicationData);
        this.dataStore.removeSeaCloudsApplicationDataById(seaCloudsApplicationData.getSeaCloudsApplicationId());

        return Response.ok().build();
    }


}
