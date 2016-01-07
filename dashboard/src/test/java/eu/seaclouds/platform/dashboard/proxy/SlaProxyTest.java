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

package eu.seaclouds.platform.dashboard.proxy;

import com.squareup.okhttp.mockwebserver.MockResponse;
import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.seaclouds.platform.dashboard.util.ObjectMapperHelpers;
import eu.seaclouds.platform.dashboard.utils.TestFixtures;
import eu.seaclouds.platform.dashboard.utils.TestUtils;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class SlaProxyTest extends AbstractProxyTest<SlaProxy> {
    private final String RANDOM_STRING = UUID.randomUUID().toString();

     //TODO: Add JSON based tests

    @Override
    public SlaProxy getProxy() {
        return getSupport().getConfiguration().getSlaProxy();
    }

    @Test
    public void testAddAgreement() throws Exception {
        String xml = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_XML);

        getMockWebServer().enqueue(new MockResponse()
                        .setHeader("Accept", MediaType.APPLICATION_XML)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        assertNotNull(getProxy().addAgreement(ObjectMapperHelpers.XmlToObject(xml, Agreement.class)));
    }

    @Test
    public void testRemoveAgreement() throws Exception {
        getMockWebServer().enqueue(new MockResponse()
                        .setHeader("Accept", MediaType.APPLICATION_XML)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        assertNotNull(getProxy().removeAgreement(RANDOM_STRING));
    }

    @Test
    public void testNotifyRulesReady() throws Exception {
        String xml = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_XML);

        getMockWebServer().enqueue(new MockResponse()
                        .setHeader("Accept", MediaType.APPLICATION_XML)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        assertNotNull(getProxy().notifyRulesReady(ObjectMapperHelpers.XmlToObject(xml, Agreement.class)));
    }

    @Test
    public void testGetAgreement() throws Exception {
        String xml = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_XML);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(xml)
                        .setHeader("Accept", MediaType.APPLICATION_XML)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        Agreement response = getProxy().getAgreement(RANDOM_STRING);

        // Agreement doesn't implement equals(), so we are going to check the IDs
        Agreement fixture = ObjectMapperHelpers.XmlToObject(xml, Agreement.class);
        assertEquals(response.getAgreementId(), fixture.getAgreementId());
    }

    @Test
    public void testGetAgreementByTemplateId() throws Exception {
        String xml = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_XML);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(xml)
                        .setHeader("Accept", MediaType.APPLICATION_XML)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        Agreement response = getProxy().getAgreementByTemplateId(RANDOM_STRING);

        // Agreement doesn't implement equals(), so we are going to check the IDs
        Agreement fixture = ObjectMapperHelpers.XmlToObject(xml, Agreement.class);
        assertEquals(response.getAgreementId(), fixture.getAgreementId());
    }

    @Test
    public void testGetAgreementStatus() throws Exception {
        String termStatusXml = TestUtils.getStringFromPath(TestFixtures.GUARRANTEE_TERMS_STATUS_PATH_XML);
        String agreementXml = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_XML);

        Agreement agreement = ObjectMapperHelpers.XmlToObject(agreementXml, Agreement.class);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(termStatusXml)
                        .setHeader("Accept", MediaType.APPLICATION_XML)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        // GuaranteeTermsStatus doesn't implement equals(), so we are going to check the IDs
        GuaranteeTermsStatus response = getProxy().getAgreementStatus(agreement);
        GuaranteeTermsStatus fixture = ObjectMapperHelpers.XmlToObject(termStatusXml, GuaranteeTermsStatus.class);
        assertEquals(response.getAgreementId(), fixture.getAgreementId());
    }

    @Test
    public void testGetAgreementViolations() throws Exception {
        String termStatusXml =  TestUtils.getStringFromPath(TestFixtures.VIOLATIONS_XML_PATH);

        String agreementXml = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_XML);
        Agreement agreement = ObjectMapperHelpers.XmlToObject(agreementXml, Agreement.class);

        for(int i = 0; i < agreement.getTerms().getAllTerms().getGuaranteeTerms().size(); i++){
            getMockWebServer().enqueue(new MockResponse()
                            .setBody(termStatusXml)
                            .setHeader("Accept", MediaType.APPLICATION_XML)
                            .setHeader("Content-Type", MediaType.APPLICATION_XML)
            );
        }


        // GuaranteeTerm doesn't implement equals(), so we are going to check not null
        for(GuaranteeTerm term : agreement.getTerms().getAllTerms().getGuaranteeTerms()){
            assertNotNull(getProxy().getGuaranteeTermViolations(agreement, term));
        }

    }
}
