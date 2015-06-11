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

package eu.seaclouds.platform.dashboard.resources;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.seaclouds.platform.dashboard.ConfigParameters;
import eu.seaclouds.platform.dashboard.http.HttpGetRequestBuilder;
import eu.seaclouds.platform.dashboard.http.HttpPostRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

@Path("/sla")
@Produces(MediaType.APPLICATION_JSON)
public class SlaResource {
    static Logger log = LoggerFactory.getLogger(SlaResource.class);

    @POST
    @Path("agreements")
    public Response addAgreements(String json) {
        JsonObject input = new JsonParser().parse(json).getAsJsonObject();


        String rules = input.get("rules").getAsJsonPrimitive().getAsString();
        String agreements = input.get("agreements").getAsJsonPrimitive().getAsString();

        if (agreements != null && rules != null) {
            try {

                String slaResponse = new HttpPostRequestBuilder()
                        .multipartPostRequest(true)
                        .addParam("sla", agreements)
                        .addParam("rules", rules)
                        .host(ConfigParameters.SLA_ENDPOINT)
                        .path("/seaclouds/agreements")
                        .addHeader("Accept", "application/json")
                        .build();
                                // Change to JSON if necessary
                                // .addHeader("Content-Type", "application/json")
                                // .addHeader("Accept", "application/json")


                // Notify the SLA when the rules are ready (Issue #56)
                new HttpPostRequestBuilder()
                        .host(ConfigParameters.SLA_ENDPOINT)
                        .path("/seaclouds/commands/rulesready")
                        .addHeader("Accept", "application/json")
                        .build();

                return Response.ok(slaResponse.toString()).build();
            } catch (URISyntaxException | IOException e) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("agreements")
    public Response listAgreements(@QueryParam("provider") String provider, @QueryParam("status") String status) {
        try {
            String calculatedPath = "/agreements";
            if (provider != null) {
                calculatedPath += "?provider=" + provider;

            }

            if (provider == null && status != null) {
                calculatedPath += "?";
            } else if (provider != null && status != null) {
                calculatedPath += "&";
            }

            if (status != null) {
                calculatedPath += "status=" + status;
            }

            String slaResponse = new HttpGetRequestBuilder()
                    .host(ConfigParameters.SLA_ENDPOINT)
                    .path(calculatedPath)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            return Response.ok(slaResponse.toString()).build();
        } catch (IOException | URISyntaxException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }
}