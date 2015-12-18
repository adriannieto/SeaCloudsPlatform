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


import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.parser.data.Violation;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class SlaProxy extends AbstractProxy {
    private static final Logger LOG = LoggerFactory.getLogger(SlaProxy.class);

    /**
     * Creates proxied HTTP POST request to SeaClouds SLA core which installs a set of SLA Agreements
     * paired with the corresponding Monitoring Rules Monitoring Rules
     *
     * @param slaAgreement SLA Agreements to install specified in SeaClouds SLA syntax.
     * @return the request
     */
    public String addAgreement(Agreement slaAgreement) {
        Entity content = Entity.entity(slaAgreement, MediaType.APPLICATION_XML);

        Invocation invocation = this.getJerseyClient().target(this.getEndpoint() + "/seaclouds/agreements").request()
                .header("Accept", MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_XML)
                .buildPost(content);

        //SLA Core returns a text message if the response was succesfully not the object, this is not the best behaviour
        return invocation.invoke().readEntity(String.class);
    }


    /**
     * Creates proxied HTTP DELETE request to SeaClouds SLA core which removes the SLA from the SLA Core
     *
     * @param agreementId of the SLA Agreement to be removed. This ID may differ from SeaClouds Application ID
     * @return the request
     */
    public String removeAgreement(String agreementId) {
        Invocation invocation = this.getJerseyClient().target(this.getEndpoint() + "/agreements/" + agreementId).request()
                .header("Accept", MediaType.APPLICATION_XML)
                .header("Content-Type", MediaType.APPLICATION_XML)
                .buildDelete();

        //SLA Core returns a text message if the response was succesfully not the object, this is not the best behaviour
        return invocation.invoke().readEntity(String.class);
    }

    /**
     * Creates proxied HTTP POST request to SeaClouds SLA core which notifies that the Monitoring Rules were installed
     * in Tower4Clouds. @see Issue #56
     *
     * @return the request
     */
    public String notifyRulesReady(Agreement slaAgreement) {
        Entity content = Entity.entity("", MediaType.TEXT_PLAIN);
        Invocation invocation = this.getJerseyClient().target(this.getEndpoint() + "/seaclouds/commands/rulesready?agreementId=" + slaAgreement.getAgreementId()).request()
                .header("Accept", MediaType.APPLICATION_XML)
                .header("Content-Type", MediaType.APPLICATION_XML)
                .buildPost(content);

        //SLA Core returns a text message if the response was succesfully not the object, this is not the best behaviour
        return invocation.invoke().readEntity(String.class);
    }

    /**
     * Creates proxied HTTP GET request to SeaClouds SLA core which retrieves the Agreement details
     *
     * @param agreementId of the desired agreement. This ID may differ from SeaClouds Application ID
     * @return the request
     */
    public Agreement getAgreement(String agreementId) {
        return this.getJerseyClient().target(this.getEndpoint() + "/agreements/" + agreementId).request()
                .header("Accept", MediaType.APPLICATION_XML)
                .header("Content-Type", MediaType.APPLICATION_XML)
                .buildGet().invoke().readEntity(Agreement.class);
    }


    /**
     * Creates proxied HTTP GET request to SeaClouds SLA which returns the Agreement according to the template id
     *
     * @param slaAgreementTemplateId
     * @return the request
     */
    public Agreement getAgreementByTemplateId(String slaAgreementTemplateId) {
        return this.getJerseyClient().target(this.getEndpoint() + "/seaclouds/commands/fromtemplate?templateId=" + slaAgreementTemplateId).request()
                .header("Accept", MediaType.APPLICATION_XML)
                .header("Content-Type", MediaType.APPLICATION_XML)
                .buildGet().invoke().readEntity(Agreement.class);
    }


    /**
     * Creates proxied HTTP GET request to SeaClouds SLA core which retrieves the Agreement Status
     *
     * @param agreementId of the desired agreement This ID may differ from SeaClouds Application ID
     * @return the request
     */
    public GuaranteeTermsStatus getAgreementStatus(Agreement agreement) {
        return this.getJerseyClient().target(this.getEndpoint() + "/agreements/" + agreement.getAgreementId() + "/guaranteestatus").request()
                .header("Accept", MediaType.APPLICATION_XML)
                .header("Content-Type", MediaType.APPLICATION_XML)
                .buildGet().invoke().readEntity(GuaranteeTermsStatus.class);
    }


    /**
     * Creates proxied HTTP GET request to SeaClouds SLA core which retrieves the Agreement Term Violations
     *
     * @param agreementId       of the desired agreement This ID may differ from SeaClouds Application ID
     * @param guaranteeTermName term name of the corresponding agreement to retrieve the violations
     * @return the request
     */
    public List<Violation> getAgreementViolations(Agreement agreement, GuaranteeTerm guaranteeTerm) {
        return this.getJerseyClient().target(this.getEndpoint() + "/violations?agreementId=" + agreement.getAgreementId() + "&guaranteeTerm=" + guaranteeTerm.getName()).request()
                .header("Accept", MediaType.APPLICATION_XML)
                .header("Content-Type", MediaType.APPLICATION_XML)
                .buildGet().invoke().readEntity(new GenericType<List<Violation>>() {
                });
    }
}
