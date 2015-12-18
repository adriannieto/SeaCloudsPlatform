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

import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.parser.data.Violation;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationDataStorage;
import eu.seaclouds.platform.dashboard.proxy.DeployerProxy;
import eu.seaclouds.platform.dashboard.proxy.MonitorProxy;
import eu.seaclouds.platform.dashboard.proxy.PlannerProxy;
import eu.seaclouds.platform.dashboard.proxy.SlaProxy;
import eu.seaclouds.platform.dashboard.util.ObjectMapperHelpers;
import eu.seaclouds.platform.dashboard.utils.TestFixtures;
import eu.seaclouds.platform.dashboard.utils.TestUtils;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import org.apache.brooklyn.rest.domain.EntitySummary;
import org.apache.brooklyn.rest.domain.SensorSummary;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.mockito.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractResourceTest<T extends Resource> {
    protected static final String RANDOM_STRING = UUID.randomUUID().toString();
    private static final String DEPLOYER_ENDPOINT = "http://localhost:8081";
    private static final String MONITOR_ENDPOINT = "http://localhost:8170";
    private static final String PLANNER_ENDPOINT = "http://localhost:1234";
    private static final String SLA_ENDPOINT = "http://localhost:8080";

    private ApplicationSummary applicationSummary;
    private TaskSummary taskSummaryDeploy;
    private TaskSummary taskSummaryDelete;
    private List<SensorSummary> sensorSummaries;
    private List<EntitySummary> entitySummaries;
    private Agreement agreement;
    private GuaranteeTermsStatus agreementStatus;
    private List<Violation> agreementTermViolations;
    private MonitoringRules monitoringRules;
    private String topology;
    private String adp;
    private String adps;
    private String aam;
    private String dam;

    private final DeployerProxy deployerProxy = mock(DeployerProxy.class);
    private final MonitorProxy monitorProxy = mock(MonitorProxy.class);
    private final PlannerProxy plannerProxy = mock(PlannerProxy.class);
    private final SlaProxy slaProxy = mock(SlaProxy.class);

    private void initObjects() throws IOException, JAXBException {
        this.applicationSummary = ObjectMapperHelpers.JsonToObject(
                TestUtils.getStringFromPath(TestFixtures.APPLICATION_PATH), ApplicationSummary.class);
        this.taskSummaryDeploy = ObjectMapperHelpers.JsonToObject(
                TestUtils.getStringFromPath(TestFixtures.TASK_SUMMARY_DEPLOY_PATH), TaskSummary.class);
        this.taskSummaryDelete = ObjectMapperHelpers.JsonToObject(
                TestUtils.getStringFromPath(TestFixtures.TASK_SUMMARY_DEPLOY_PATH), TaskSummary.class);
        this.sensorSummaries = ObjectMapperHelpers.JsonToObjectCollection(
                TestUtils.getStringFromPath(TestFixtures.SENSORS_SUMMARIES_PATH), SensorSummary.class);
        this.entitySummaries = ObjectMapperHelpers.JsonToObjectCollection(
                TestUtils.getStringFromPath(TestFixtures.ENTITIES_PATH), EntitySummary.class);


        this.agreement = ObjectMapperHelpers.XmlToObject(
                TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_XML), Agreement.class);
        this.agreementStatus = ObjectMapperHelpers.XmlToObject(
                TestUtils.getStringFromPath(TestFixtures.GUARRANTEE_TERMS_STATUS_PATH_XML), GuaranteeTermsStatus.class);
        this.agreementTermViolations = ObjectMapperHelpers.XmlToObjectCollection(TestUtils.getStringFromPath(TestFixtures.VIOLATIONS_XML_PATH), Violation.class);

        this.monitoringRules = ObjectMapperHelpers.XmlToObject(TestUtils.getStringFromPath(TestFixtures.MONITORING_RULES_PATH), MonitoringRules.class);
        this.topology = TestUtils.getStringFromPath(TestFixtures.DESIGNER_TOPOLOGY);

        this.adp = TestUtils.getStringFromPath(TestFixtures.ADP_PATH);
        this.adps = TestUtils.getStringFromPath(TestFixtures.ADPS_PATH);
        this.aam = TestUtils.getStringFromPath(TestFixtures.AAM_PATH);
        this.dam = TestUtils.getStringFromPath(TestFixtures.TOSCA_DAM_PATH);
    }

    private void initMocks() throws IOException {

        when(this.deployerProxy.getEndpoint()).thenReturn(AbstractResourceTest.DEPLOYER_ENDPOINT);
        when(this.monitorProxy.getEndpoint()).thenReturn(AbstractResourceTest.MONITOR_ENDPOINT);
        when(this.plannerProxy.getEndpoint()).thenReturn(AbstractResourceTest.PLANNER_ENDPOINT);
        when(this.slaProxy.getEndpoint()).thenReturn(AbstractResourceTest.SLA_ENDPOINT);

        when(this.deployerProxy.getApplication(anyString())).thenReturn(this.applicationSummary);
        when(this.deployerProxy.deployApplication(anyString())).thenReturn(this.taskSummaryDeploy);
        when(this.deployerProxy.removeApplication(anyString())).thenReturn(this.taskSummaryDelete);
        when(this.deployerProxy.getEntitiesFromApplication(anyString())).thenReturn(this.entitySummaries);
        when(this.deployerProxy.getEntitySensors(anyString(), anyString())).thenReturn(this.sensorSummaries);
        when(this.deployerProxy.getEntitySensorsValue(anyString(), anyString(), anyString())).thenReturn("0.7");


        when(this.monitorProxy.addMonitoringRules(any(MonitoringRules.class))).thenReturn(AbstractResourceTest.RANDOM_STRING);
        when(this.monitorProxy.listMonitoringRules()).thenReturn(this.monitoringRules);
        when(this.monitorProxy.removeMonitoringRule(anyString())).thenReturn(AbstractResourceTest.RANDOM_STRING);

        when(this.plannerProxy.getMonitoringRulesByTemplateId(anyString())).thenReturn(this.monitoringRules);
        when(this.plannerProxy.getAdps(anyString())).thenReturn(this.adps);
        when(this.plannerProxy.getDam(anyString())).thenReturn(this.dam);

        when(this.slaProxy.addAgreement(Matchers.<Agreement>any())).thenReturn(AbstractResourceTest.RANDOM_STRING);
        when(this.slaProxy.getAgreement(anyString())).thenReturn(this.agreement);
        when(this.slaProxy.getAgreementByTemplateId(anyString())).thenReturn(this.agreement);
        when(this.slaProxy.getAgreementStatus(Matchers.<Agreement>any())).thenReturn(this.agreementStatus);
        when(this.slaProxy.getAgreementViolations(Matchers.<Agreement>any(), Matchers.<GuaranteeTerm>any())).thenReturn(this.agreementTermViolations);
        when(this.slaProxy.notifyRulesReady(Matchers.<Agreement>any())).thenReturn(AbstractResourceTest.RANDOM_STRING);
        when(this.slaProxy.removeAgreement(anyString())).thenReturn(AbstractResourceTest.RANDOM_STRING);
    }

    @BeforeClass
    public void setupClass() throws Exception {
        this.initObjects();
        this.initMocks();
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        SeaCloudsApplicationDataStorage.getInstance().clearDataStore();
    }

    public ApplicationSummary getApplicationSummary() {
        return this.applicationSummary;
    }

    public TaskSummary getTaskSummaryDeploy() {
        return this.taskSummaryDeploy;
    }

    public TaskSummary getTaskSummaryDelete() {
        return this.taskSummaryDelete;
    }

    public Agreement getAgreement() {
        return this.agreement;
    }

    public GuaranteeTermsStatus getAgreementStatus() {
        return this.agreementStatus;
    }

    public List<Violation> getAgreementTermViolations() {
        return this.agreementTermViolations;
    }

    public MonitoringRules getMonitoringRules() {
        return this.monitoringRules;
    }

    public String getTopology() {
        return this.topology;
    }

    public String getAdp() {
        return this.adp;
    }

    public String getAdps() {
        return this.adps;
    }

    public String getAam() {
        return this.aam;
    }

    public String getDam() {
        return this.dam;
    }

    public DeployerProxy getDeployerProxy() {
        return this.deployerProxy;
    }

    public MonitorProxy getMonitorProxy() {
        return this.monitorProxy;
    }

    public PlannerProxy getPlannerProxy() {
        return this.plannerProxy;
    }

    public SlaProxy getSlaProxy() {
        return this.slaProxy;
    }


    public List<SensorSummary> getSensorSummaries() {
        return this.sensorSummaries;
    }

    public List<EntitySummary> getEntitySummaries() {
        return this.entitySummaries;
    }
}
