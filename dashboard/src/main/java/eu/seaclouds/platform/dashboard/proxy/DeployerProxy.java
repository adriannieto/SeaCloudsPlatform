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

package eu.seaclouds.platform.dashboard.proxy;

import eu.seaclouds.platform.dashboard.util.ObjectMapperHelpers;
import org.apache.brooklyn.rest.domain.ApplicationSummary;

import org.apache.brooklyn.rest.domain.EntitySummary;
import org.apache.brooklyn.rest.domain.SensorSummary;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;


public class DeployerProxy extends AbstractProxy {

    /**
     * Creates proxied HTTP GET request to Apache Brooklyn which returns the details about a particular hosted application
     *
     * @param brooklynId of the desired application to fetch. This ID may differ from SeaClouds Application ID
     * @return the request
     */
    public ApplicationSummary getApplication(String brooklynId) throws IOException {
        Invocation invocation = this.getJerseyClient().target(this.getEndpoint() + "/v1/applications/" + brooklynId).request().buildGet();

        if (this.getUser() != null && this.getPassword() != null) {
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, this.getUser());
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, this.getPassword());
        }

        // Dropwizard Jackson(com.fasterxml.jackson) cannot parse this entity, we will use Brooklyn one instead (org.codehaus.jackson.map.ObjectMapper)
        return ObjectMapperHelpers.JsonToObject(invocation.invoke().readEntity(String.class), ApplicationSummary.class);
    }

    /**
     * Creates a proxied HTTP DELETE request to Apache Brooklyn to remove an application
     *
     * @param brooklynId of the desired application to remove. This ID may differ from SeaClouds Application ID
     * @return the request
     */
    public TaskSummary removeApplication(String brooklynId) throws IOException {
        Invocation invocation = this.getJerseyClient().target(this.getEndpoint() + "/v1/applications/" + brooklynId).request().buildDelete();

        if (this.getUser() != null && this.getPassword() != null) {
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, this.getUser());
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, this.getPassword());
        }

        // Dropwizard Jackson(com.fasterxml.jackson) cannot parse this entity, we will use Brooklyn one instead (org.codehaus.jackson.map.ObjectMapper)
        return ObjectMapperHelpers.JsonToObject(invocation.invoke().readEntity(String.class), TaskSummary.class);

    }

    /**
     * Creates a proxied HTTP POST request to Apache Brooklyn to deploy a new application
     *
     * @param tosca file compliant with the Apache Brooklyn TOSCA specification
     * @return a task
     */
    public TaskSummary deployApplication(String tosca) throws IOException {

        Entity content = Entity.entity(tosca, MediaType.TEXT_PLAIN);
        Invocation invocation = this.getJerseyClient().target(this.getEndpoint() + "/v1/applications").request().buildPost(content);

        if (this.getUser() != null && this.getPassword() != null) {
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, this.getUser());
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, this.getPassword());
        }

        // Dropwizard Jackson(com.fasterxml.jackson) cannot parse this entity, we will use Brooklyn one instead (org.codehaus.jackson.map.ObjectMapper)
        return ObjectMapperHelpers.JsonToObject(invocation.invoke().readEntity(String.class), TaskSummary.class);
    }

    public List<EntitySummary> getEntitiesFromApplication(String brooklynId) throws IOException {
        Invocation invocation = this.getJerseyClient()
                .target(this.getEndpoint() + "/v1/applications/" + brooklynId + "/entities")
                .request().buildGet();

        if (this.getUser() != null && this.getPassword() != null) {
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, this.getUser());
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, this.getPassword());
        }

        // Dropwizard Jackson(com.fasterxml.jackson) cannot parse this entity, we will use Brooklyn one instead (org.codehaus.jackson.map.ObjectMapper)
        return ObjectMapperHelpers.JsonToObjectCollection(invocation.invoke().readEntity(String.class), EntitySummary.class);
    }

    public List<SensorSummary> getEntitySensors(String brooklynId, String brooklynEntityId) throws IOException {
        Invocation invocation = this.getJerseyClient()
                .target(this.getEndpoint() + "/v1/applications/" + brooklynId + "/entities/" + brooklynEntityId + "/sensors")
                .request().buildGet();

        if (this.getUser() != null && this.getPassword() != null) {
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, this.getUser());
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, this.getPassword());
        }

        // Dropwizard Jackson(com.fasterxml.jackson) cannot parse this entity, we will use Brooklyn one instead (org.codehaus.jackson.map.ObjectMapper)
        return ObjectMapperHelpers.JsonToObjectCollection(invocation.invoke().readEntity(String.class), SensorSummary.class);
    }

    public String getEntitySensorsValue(String brooklynId, String brooklynEntityId, String sensorId) throws IOException {
        Invocation invocation = this.getJerseyClient().target(
                this.getEndpoint() + "/v1/applications/" + brooklynId + "/entities/" + brooklynEntityId + "/sensors/" + sensorId + "?raw=true")
                .request().buildGet();

        if (this.getUser() != null && this.getPassword() != null) {
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, this.getUser());
            invocation = invocation.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, this.getPassword());
        }

        // Dropwizard Jackson(com.fasterxml.jackson) cannot parse this entity, we will use Brooklyn one instead (org.codehaus.jackson.map.ObjectMapper)
        return invocation.invoke().readEntity(String.class);
    }
}
