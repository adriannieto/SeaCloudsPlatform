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

package eu.seaclouds.platform.dashboard.model;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.net.URL;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class SeaCloudsApplicationDataTest {
    private static final String TOSCA_DAM_FILE_PATH = "fixtures/tosca-dam.yml";
    private static final String DESCRIPTION = "Sample 3-tier application";
    private static final String MONITORING_RULES_TEMPLATE_ID = "3e63723c-9715-457a-9aeb-2ae1b274e8b1";
    private static final String AGREEMENT_TEMPLATE_ID = "3e63723c-9715-457a-9aeb-2ae1b274e8b2";

    private Map toscaDamMap;
    private SeaCloudsApplicationData applicationData;

    //TODO: Modify this class when we will take Objects as and input for the setters instead of strings.

    @BeforeMethod
    public void setUp() throws Exception {
        Yaml yamlParser = new Yaml();
        URL resource = Resources.getResource(SeaCloudsApplicationDataTest.TOSCA_DAM_FILE_PATH);
        this.toscaDamMap = (Map) yamlParser.load(FileUtils.openInputStream(new File(resource.getFile())));
    }

    @Test
    public void testExtractName() {
        String toscaDamName = SeaCloudsApplicationData.extractName(this.toscaDamMap);
        assertEquals(toscaDamName, SeaCloudsApplicationDataTest.DESCRIPTION);
    }

    @Test
    public void testExtractAgreementTemplateId() {
        String toscaAgreementTemplateId = SeaCloudsApplicationData.extractAgreementTemplateId(this.toscaDamMap);
        assertEquals(toscaAgreementTemplateId, SeaCloudsApplicationDataTest.AGREEMENT_TEMPLATE_ID);
    }

    @Test
    public void testExtractMonitoringRulesemplateId() {
        String monitoringRulesId = SeaCloudsApplicationData.extractMonitoringRulesemplateId(this.toscaDamMap);
        assertEquals(monitoringRulesId, SeaCloudsApplicationDataTest.MONITORING_RULES_TEMPLATE_ID);
    }

    @Test
    public void testCreateSeaCloudsApplicationData(){
        SeaCloudsApplicationData application = new SeaCloudsApplicationData(this.toscaDamMap);
        assertEquals(application.getName(), SeaCloudsApplicationDataTest.DESCRIPTION);
        assertEquals(application.getAgreementTemplateId(), SeaCloudsApplicationDataTest.AGREEMENT_TEMPLATE_ID);
        assertEquals(application.getMonitoringRulesTemplateId(), SeaCloudsApplicationDataTest.MONITORING_RULES_TEMPLATE_ID);
    }

}