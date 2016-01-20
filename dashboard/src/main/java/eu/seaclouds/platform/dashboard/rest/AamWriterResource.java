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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.aamwriter.AamWriter;
import eu.seaclouds.platform.planner.aamwriter.AamWriterException;

@Path("/aamwriter")
@Produces(MediaType.TEXT_PLAIN)
public class AamWriterResource implements Resource {
    private static final Logger LOG = LoggerFactory.getLogger(AamWriterResource.class);
    private static final AamWriter AAM_WRITER = new AamWriter();

    @POST
    @Path("translate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/x-yaml")
    public String translateDesignerModel(String topology) {
        return AAM_WRITER.writeAam(topology);
    }

}
