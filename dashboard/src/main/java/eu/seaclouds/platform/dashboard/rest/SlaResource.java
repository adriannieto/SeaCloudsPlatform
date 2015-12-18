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
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationData;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationDataStorage;
import eu.seaclouds.platform.dashboard.proxy.SlaProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/sla")
public class SlaResource implements Resource{
    private static final Logger LOG = LoggerFactory.getLogger(SlaResource.class);

    private final SlaProxy sla;
    private final SeaCloudsApplicationDataStorage dataStore;

    public SlaResource(SlaProxy slaProxy) {
        sla = slaProxy;
        dataStore = SeaCloudsApplicationDataStorage.getInstance();
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("agreements/{seaCloudsId}")
    public Response getAgreement(@PathParam("seaCloudsId") String seaCloudsId) {
        if (seaCloudsId == null) {
            SlaResource.LOG.error("Missing input parameters");
            return Response.status(Status.NOT_ACCEPTABLE).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = this.dataStore.getSeaCloudsApplicationDataById(seaCloudsId);
            if (seaCloudsApplicationData == null) {
                return Response.status(Status.BAD_REQUEST).build();
            }

            return Response.ok(this.sla.getAgreement(seaCloudsApplicationData.getAgreementId())).build();
        }
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("agreements/{seaCloudsId}/terms/{termName}/violations")
    public Response getViolations(@PathParam("seaCloudsId") String seaCloudsId, @PathParam("termName") String termName) {
        if (seaCloudsId == null || termName == null) {
            SlaResource.LOG.error("Missing input parameters");
            return Response.status(Status.NOT_ACCEPTABLE).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = this.dataStore.getSeaCloudsApplicationDataById(seaCloudsId);

            if (seaCloudsApplicationData == null) {
                SlaResource.LOG.error("Application " + seaCloudsId + " not found");
                return Response.status(Status.BAD_REQUEST).build();
            }

            Agreement agreement = this.sla.getAgreement(seaCloudsApplicationData.getAgreementId());
            GuaranteeTerm term = null;
            for (GuaranteeTerm termItem : agreement.getTerms().getAllTerms().getGuaranteeTerms()) {
                if(termItem.getName().equalsIgnoreCase(termName)){
                    term = termItem;
                    break;
                }
            }

            return Response.ok(this.sla.getAgreementViolations(agreement, term)).build();
        }
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("agreements/{seaCloudsId}/status")
    public Response getAgreementStatus(@PathParam("seaCloudsId") String seaCloudsId) {
        if (seaCloudsId == null) {
            SlaResource.LOG.error("Missing input parameters");
            return Response.status(Status.NOT_ACCEPTABLE).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = this.dataStore.getSeaCloudsApplicationDataById(seaCloudsId);
            if (seaCloudsApplicationData == null) {
                SlaResource.LOG.error("Application " + seaCloudsId + " not found");
                return Response.status(Status.BAD_REQUEST).build();
            }

            Agreement agreement = this.sla.getAgreement(seaCloudsApplicationData.getAgreementId());
            return Response.ok(this.sla.getAgreementStatus(agreement)).build();
        }
    }
}