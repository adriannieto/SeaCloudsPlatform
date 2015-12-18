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

import eu.atos.sla.parser.data.wsag.Agreement;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.yaml.snakeyaml.Yaml;

import java.io.Serializable;
import java.util.*;

public class SeaCloudsApplicationData implements Serializable {
    private static final String YAML_DESCRIPTION_TAG = "description";
    private static final String YAML_TOPOLOGY_TEMPLATE_TAG = "topology_template";
    private static final String YAML_GROUPS_TEMPLATE_TAG = "groups";
    private static final String YAML_POLICIES_TAG = "policies";
    private static final String YAML_MONITORING_INFORMATION_TAG = "monitoringInformation";
    private static final String YAML_AGREEMENT_TAG = "sla_gen_info";

    private static final String YAML_ID_TAG = "id";


    private final String seaCloudsApplicationId;
    private final String name;
    private final Map toscaDamMap;
    private final String monitoringRulesTemplateId;
    private final String agreementTemplateId;
    private String deployerApplicationId;
    private Set<String> monitoringRulesIds;
    private String agreementId;

    public SeaCloudsApplicationData(String toscaDam) {
        seaCloudsApplicationId = UUID.randomUUID().toString();
        Yaml yamlParser = new Yaml();
        toscaDamMap = (Map) yamlParser.load(toscaDam);
        name = SeaCloudsApplicationData.extractName(toscaDamMap);
        monitoringRulesTemplateId = SeaCloudsApplicationData.extractMonitoringRulesemplateId(toscaDamMap);
        agreementTemplateId = SeaCloudsApplicationData.extractAgreementTemplateId(toscaDamMap);

    }

    SeaCloudsApplicationData(Map toscaDamMap) {
        seaCloudsApplicationId = UUID.randomUUID().toString();
        this.toscaDamMap = toscaDamMap;
        name = SeaCloudsApplicationData.extractName(this.toscaDamMap);
        monitoringRulesTemplateId = SeaCloudsApplicationData.extractMonitoringRulesemplateId(this.toscaDamMap);
        agreementTemplateId = SeaCloudsApplicationData.extractAgreementTemplateId(this.toscaDamMap);

    }

    static String extractName(Map toscaDamMap) {
        return (String) toscaDamMap.get(SeaCloudsApplicationData.YAML_DESCRIPTION_TAG);
    }

    static String extractAgreementTemplateId(Map toscaDamMap) {
        Map topologyTemplate = (Map) toscaDamMap.get(SeaCloudsApplicationData.YAML_TOPOLOGY_TEMPLATE_TAG);
        Map groups = (Map) topologyTemplate.get(SeaCloudsApplicationData.YAML_GROUPS_TEMPLATE_TAG);
        Map monitoringInformation = (Map) groups.get(SeaCloudsApplicationData.YAML_AGREEMENT_TAG);
        Map policies = (Map)((List)  monitoringInformation.get(SeaCloudsApplicationData.YAML_POLICIES_TAG)).get(0);
        return (String) policies.get(SeaCloudsApplicationData.YAML_ID_TAG);
    }

    static String extractMonitoringRulesemplateId(Map toscaDamMap) {
        Map topologyTemplate = (Map) toscaDamMap.get(SeaCloudsApplicationData.YAML_TOPOLOGY_TEMPLATE_TAG);
        Map groups = (Map) topologyTemplate.get(SeaCloudsApplicationData.YAML_GROUPS_TEMPLATE_TAG);
        Map slaGenInfo = (Map) groups.get(SeaCloudsApplicationData.YAML_MONITORING_INFORMATION_TAG);
        Map policies = (Map) ((List) slaGenInfo.get(SeaCloudsApplicationData.YAML_POLICIES_TAG)).get(0);
        return (String) policies.get(SeaCloudsApplicationData.YAML_ID_TAG);
    }



    public void setDeployerApplicationId(ApplicationSummary application) {
        deployerApplicationId = application.getId();
    }

    public void setDeployerApplicationId(TaskSummary applicationTask) {
        deployerApplicationId = applicationTask.getEntityId();
    }

    public void setMonitoringRulesIds(MonitoringRules monitoringRules) {
        Set<String> rulesIdSet = new HashSet<>();
        for(MonitoringRule rule : monitoringRules.getMonitoringRules()){
            rulesIdSet.add(rule.getId());
        }

        monitoringRulesIds = rulesIdSet;
    }

    public void setAgreementId(Agreement agreement) {
        agreementId = agreement.getAgreementId();
    }

    public String getSeaCloudsApplicationId() {
        return this.seaCloudsApplicationId;
    }

    public String getName() {
        return this.name;
    }

    public String getMonitoringRulesTemplateId() {
        return this.monitoringRulesTemplateId;
    }

    public String getAgreementTemplateId() {
        return this.agreementTemplateId;
    }

    public Set<String> getMonitoringRulesIds() {
        return this.monitoringRulesIds;
    }

    public String getAgreementId() {
        return this.agreementId;
    }

    public String getToscaDam() {
        Yaml yamlParser = new Yaml();
        return yamlParser.dump(this.toscaDamMap);
    }

    public String getDeployerApplicationId() {
        return this.deployerApplicationId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        SeaCloudsApplicationData that = (SeaCloudsApplicationData) o;

        return this.seaCloudsApplicationId.equals(that.seaCloudsApplicationId);

    }

    @Override
    public int hashCode() {
        return this.seaCloudsApplicationId.hashCode();
    }

}
