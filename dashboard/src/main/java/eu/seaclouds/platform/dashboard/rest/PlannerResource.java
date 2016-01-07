/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.rest;


import com.codahale.metrics.annotation.Timed;
import eu.seaclouds.platform.dashboard.proxy.PlannerProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/planner")
public class PlannerResource implements Resource{
    private static final Logger LOG = LoggerFactory.getLogger(PlannerResource.class);

    private final PlannerProxy planner;

    public PlannerResource(PlannerProxy planner) {
        this.planner = planner;
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_XML)
    @Path("monitoringrules/{templateId}")
    public Response getMonitoringRulesById(@PathParam("templateId") String templateId) {
        if (templateId == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            return Response.ok(planner.getMonitoringRulesByTemplateId(templateId)).build();
        }
    }


    @POST
    @Timed
    @Produces("application/x-yaml")
    @Path("adps")
    public Response getAdps(String aam) {
        if (aam == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            return Response.ok(planner.getAdps(aam)).build();
        }
    }

    @POST
    @Timed
    @Produces("application/x-yaml")
    @Path("dam")
    public Response getDam(String adp) {
        if (adp == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            return Response.ok(planner.getDam(adp)).build();
        }
    }
}
