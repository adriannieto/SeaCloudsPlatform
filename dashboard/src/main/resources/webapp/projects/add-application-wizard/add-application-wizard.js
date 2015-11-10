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

'use strict';

angular.module('seacloudsDashboard.projects.addApplicationWizard', ['ngRoute', 'angularTopologyEditor', 'ui.codemirror', 'ngFileUpload'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/add-application-wizard', {
            templateUrl: 'projects/add-application-wizard/add-application-wizard.html'
        })
    }])
    .controller('AddApplicationWizardCtrl', function ($scope, $timeout, notificationService) {
        $scope.applicationWizardData = {
            name: "",
            id: undefined,
            application_requirements: {
                response_time: undefined,
                availability: undefined,
                cost: undefined,
                workload: undefined
            },
            aam: undefined,
            feasibleAdps: undefined,
            finalAdp: undefined,
            finalDam: undefined,
            finalMonitoringRules: undefined,
            finalSlaRules: undefined,
            wizardLog: "",
            topology: {
                "nodes": [],
                "links": []
            },
            brooklynApplication: undefined
        }


        $scope.deployApplication = function () {

            var damSuccessCb = function (futureEntity) {
                $scope.applicationWizardData.wizardLog += "Sending application to the Deployer...";
                $scope.applicationWizardData.wizardLog += "\t Done. \n";

                $scope.applicationWizardData.id = futureEntity.entityId;

                $scope.applicationWizardData.wizardLog += "Retrieving application deployment status...";
                $scope.applicationWizardData.wizardLog += "\t Done. \n";

                $scope.applicationWizardData.wizardLog += "Installing Monitoring Rules...";

            }

            var damFailCb = function () {
                $scope.applicationWizardData.wizardLog += "Sending application to the Deployer...";
                $scope.applicationWizardData.wizardLog += "\t ERROR. \n";
            }


            var rulesSuccessCb = function () {
                if($scope.applicationWizardData.finalMonitoringRules && $scope.applicationWizardData.finalSlaRules){
                    $scope.applicationWizardData.wizardLog += "\t Done. \n";
                }else{
                    $scope.applicationWizardData.wizardLog += "\t Skipped. \n";
                }

                $scope.applicationWizardData.wizardLog += "Installing Service Level Agreements...";
            }

            var rulesFailCb = function () {
                $scope.applicationWizardData.wizardLog += "\t ERROR. \n";
            }

            var agreementSuccessCb = function () {
                if($scope.applicationWizardData.finalSlaRules && $scope.applicationWizardData.finalMonitoringRules){
                    $scope.applicationWizardData.wizardLog += "\t Done. \n";
                }else{
                    $scope.applicationWizardData.wizardLog += "\t Skipped. \n";
                }
            }

            var agreementFailCb = function () {
                $scope.applicationWizardData.wizardLog += "\t ERROR. \n";
            }

            $scope.applicationWizardData.wizardLog += "** Please wait until the process finishes **\n\n";
            var dummyDAM = "name: WebChat [Demo]\r\n\r\nservices:\r\n\r\n- serviceType: org.apache.brooklyn.entity.software.base.VanillaSoftwareProcess\r\n  name: MySQL+DC\r\n  id: dc+mysql\r\n  location: aws-ec2:us-west-2\r\n  brooklyn.config:\r\n    launch.command: \"true\"\r\n    stop.command: \"true\"\r\n    checkRunning.command: \"true\"\r\n    children.startable.mode: background_late\r\n  brooklyn.children:\r\n  - serviceType: org.apache.brooklyn.entity.database.mysql.MySqlNode\r\n    id: db\r\n    name: MessageDatabase\r\n    brooklyn.config:\r\n      creationScriptUrl: https:\/\/bit.ly\/brooklyn-visitors-creation-script\r\n  - serviceType: org.apache.brooklyn.entity.software.base.VanillaSoftwareProcess\r\n    name: Data-collector\r\n    id: data-collector\r\n    provisioning.properties:\r\n      stopIptables: true\r\n    launch.command: |\r\n      sudo apt-get update\r\n      sudo apt-get install -y openjdk-7-jre unzip\r\n      export MODACLOUDS_TOWER4CLOUDS_MANAGER_IP=52.19.149.78\r\n      export MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT=8170\r\n      export MODACLOUDS_TOWER4CLOUDS_DC_SYNC_PERIOD=10\r\n      export MODACLOUDS_TOWER4CLOUDS_RESOURCES_KEEP_ALIVE_PERIOD=25\r\n      export MODACLOUDS_TOWER4CLOUDS_CLOUD_PROVIDER_ID=amazon\r\n      export MODACLOUDS_TOWER4CLOUDS_CLOUD_PROVIDER_TYPE=IaaS\r\n      export MODACLOUDS_TOWER4CLOUDS_VM_TYPE=WebChat_DBModule\r\n      export MODACLOUDS_TOWER4CLOUDS_VM_ID=WebChat_DBModule_Instance\r\n      wget -O data-collector-2.0.4.jar \"https:\/\/github.com\/imperial-modaclouds\/modaclouds-data-collectors\/releases\/download\/2.0.4\/data-collector-2.0.4.jar\"\r\n      wget -O hyperic-sigar-1.6.4.zip \"https:\/\/sourceforge.net\/projects\/sigar\/files\/sigar\/1.6\/hyperic-sigar-1.6.4.zip\/download?use_mirror=switch\"\r\n      unzip hyperic-sigar-1.6.4.zip\r\n      nohup java -Djava.library.path=.\/hyperic-sigar-1.6.4\/sigar-bin\/lib\/ -jar data-collector-2.0.4.jar tower4clouds > dc.out 2>&1 &\r\n      echo $! > $PID_FILE\r\n    install.latch: $brooklyn:component(\"db\").attributeWhenReady(\"service.isUp\")\r\n\r\n\r\n- type: org.apache.brooklyn.entity.cloudfoundry.webapp.java.JavaCloudFoundryPaasWebApp\r\n  name: WebChat-Module\r\n  id: webapp\r\n  location:\r\n    cloudfoundry:\r\n      user: mbarrientos@lcc.uma.es\r\n      password: scenic-UMA\r\n      org: seaclouds-org\r\n      endpoint: https:\/\/api.run.pivotal.io\r\n      space: development\r\n      address: run.pivotal.io\r\n  brooklyn.config:\r\n    application-name: webchat-demo-module\r\n    application-url: file:\/\/\/home\/ubuntu\/resources\/brooklyn-webapp-custom-env.war\r\n    env: \r\n      myDbName: visitors\r\n      myDbHostName: $brooklyn:component(\"db\").attributeWhenReady(\"host.address\")\r\n      myDbUser: brooklyn\r\n      myDbPassword: br00k11n\r\n      myDbPort: 3306\r\n";
            $scope.SeaCloudsApi.addProject(dummyDAM, damSuccessCb, damFailCb, undefined, rulesSuccessCb, rulesFailCb,
                undefined, agreementSuccessCb, agreementFailCb).
                success(function (data) {
                    $scope.applicationWizardData.wizardLog += "\n\n";
                    $scope.applicationWizardData.wizardLog += "The application deployment process was triggered successfully*. \n";
                    $scope.applicationWizardData.wizardLog += "* Please notice that although the wizard finished the application runtime" +
                        "failures could happen please go to the status view in order to verify " +
                        "that everything is running properly"
                }).
                error(function (data) {
                    $scope.applicationWizardData.wizardLog += "\n\n";
                    $scope.applicationWizardData.wizardLog += "Something wrong happened!\n";
                    $scope.applicationWizardData.wizardLog += "Please restart the process and try again\n";
                })
        }

        $scope.steps = ['Application properties', 'Design topology',
            'Optimize & Plan', 'Configuration summary', 'Process Summary & Deploy'];
        $scope.currentStep = 1;
        $scope.isSelected = function (step) {
            return $scope.currentStep == step;
        };
        $scope.getStepCount = function () {

            return $scope.steps.length;
        };
        $scope.range = function (n) {
            return new Array(n);
        };
        $scope.previousStep = function () {
            switch ($scope.currentStep) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    $scope.applicationWizardData.feasibleAdps = undefined;
                    $scope.applicationWizardData.finalAdp = undefined;
                    break;
                case 4:
                    $scope.applicationWizardData.finalDam = undefined;
                    $scope.applicationWizardData.finalMonitoringRules = undefined;
                    $scope.applicationWizardData.finalSlaRules = undefined;
                    $scope.damGenerated = false;
                    break;
                case 5:
                default:
                    break;
            }
            $scope.currentStep--;
        };

        $scope.nextStep = function () {
            switch ($scope.currentStep) {
                case 1:
                    $scope.currentStep++;
                    break;
                case 2:
                    $scope.currentStep++;
                    $timeout(function(){
                        $scope.applicationWizardData.feasibleAdps = [
                            "tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03\r\ndescription: WebChat application\r\nimports:\r\n- tosca-normative-types:1.0.0.wd03-SNAPSHOT\r\ntopology_template:\r\n  node_templates:\r\n    WebChat-Module:\r\n      type: seaclouds.nodes.webapp.tomcat.TomcatServer\r\n      artifacts:\r\n      - war: \"http:\/\/search.maven.org\/remotecontent?filepath=io\/brooklyn\/example\/brooklyn-example-hello-world-sql-webapp\/0.6.0\/brooklyn-example-hello-world-sql-webapp-0.6.0.war\"\r\n        type: tosca.artifacts.File\r\n      requirements:\r\n      - endpoint: MessageDatabase\r\n      - host: webservice_offering\r\n\r\n\r\n    MessageDatabase:\r\n      type: seaclouds.nodes.database.mysql.MySqlNode\r\n      artifacts:\r\n      - db_create: https:\/\/bit.ly\/brooklyn-visitors-creation-script\r\n        type: tosca.artifacts.File\r\n      properties:\r\n        mysql_version:\r\n          constraints:\r\n          - greater_or_equal: \'5.0\'\r\n          - less_or_equal: \'5.0\'\r\n      requirements:\r\n      - host: My-DB-Compute\r\n\r\n    MessageDatabase-Compute:\r\n      type: seaclouds.nodes.Platform.Pivotal\r\n      properties:\r\n        resource_type: platform\r\n        go_support: true\r\n        java_support: true\r\n        node_support: true\r\n        php_support: true\r\n        python_support: true\r\n        ruby_support: true\r\n        mysql_support: true\r\n        postgresql_support: true\r\n        mongoDB_support: true\r\n        redis_support: true\r\n        riak_support: true\r\n        dataStax_support: true\r\n        neo4j_support: true\r\n        pivotalHD_support: true\r\n        cost: 0.03 USD_perGB_per_h\r\n        java_version: 7\r\n\r\n    webservice_offering:\r\n      type: seaclouds.nodes.Platform.Pivotal\r\n      properties:\r\n        resource_type: platform\r\n        go_support: true\r\n        java_support: true\r\n        node_support: true\r\n        php_support: true\r\n        python_support: true\r\n        ruby_support: true\r\n        mysql_support: true\r\n        postgresql_support: true\r\n        mongoDB_support: true\r\n        redis_support: true\r\n        riak_support: true\r\n        dataStax_support: true\r\n        neo4j_support: true\r\n        pivotalHD_support: true\r\n        cost: 0.03 USD_perGB_per_h\r\n        java_version: 7\r\n\r\n\r\ngroups:\r\n  operation_Chat:\r\n    members:\r\n    - WebChat-Module\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 50 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies:\r\n        operation_MessageDatabase: \'2\'\r\n    - QoSRequirements:\r\n        response_time:\r\n          less_than: 2000.0 ms\r\n        availability:\r\n          greater_than: 0.998\r\n        cost:\r\n          less_or_equal: 200.0 euros_per_month\r\n        workload:\r\n          less_or_equal: 50.0 req_per_min\r\n      - ExpectedQuality: {expectedWorkload: 50.0, expectedAvailability: 0.9994997551225, expectedCost: 190.8, expectedExecutionTime: 0.10079721294968995}\r\n\r\n  operation_MessageDatabase:\r\n    members:\r\n    - MessageDatabase\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 30 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies: {}\r\n",
                            "tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03\r\ndescription: WebChat application\r\nimports:\r\n- tosca-normative-types:1.0.0.wd03-SNAPSHOT\r\ntopology_template:\r\n  node_templates:\r\n    WebChat-Module:\r\n      type: seaclouds.nodes.webapp.tomcat.TomcatServer\r\n      artifacts:\r\n      - war: \"http:\/\/search.maven.org\/remotecontent?filepath=io\/brooklyn\/example\/brooklyn-example-hello-world-sql-webapp\/0.6.0\/brooklyn-example-hello-world-sql-webapp-0.6.0.war\"\r\n        type: tosca.artifacts.File\r\n      requirements:\r\n      - endpoint: MessageDatabase\r\n      - host: webservice_offering\r\n\r\n\r\n    MessageDatabase:\r\n      type: seaclouds.nodes.database.mysql.MySqlNode\r\n      artifacts:\r\n      - db_create: https:\/\/bit.ly\/brooklyn-visitors-creation-script\r\n        type: tosca.artifacts.File\r\n      properties:\r\n        mysql_version:\r\n          constraints:\r\n          - greater_or_equal: \'5.0\'\r\n          - less_or_equal: \'5.0\'\r\n      requirements:\r\n      - host: My-DB-Compute\r\n\r\n    MessageDatabase-Compute:\r\n      type: seaclouds.Nodes.Compute\r\n      properties:\r\n        resource_type: compute\r\n        hardwareId: i2.xlarge\r\n        location: \"aws:ec2\"\r\n        region: \"eu-west-1\"\r\n        performance: 90\r\n        availability: 0.99950\r\n        country: Ireland\r\n        city: Dublig\r\n        cost: 1.001 USD\/hour\r\n        disk_size: 800\r\n        num_disks: 1\r\n        num_cpus: 4\r\n        ram: 30.5\r\n        disk_type: ssd\r\n\r\n    webservice_offering:\r\n      type: seaclouds.Nodes.Compute\r\n      properties:\r\n        resource_type: compute\r\n        location: \"hpcloud-compute\"\r\n        region: \"region-b.geo-1\"\r\n        performance: 80\r\n        availability: 0.99550\r\n        country: EEUU\r\n        city: Oregon\r\n        cost: 1.101 USD\/hour\r\n        disk_size: 700\r\n        num_disks: 2\r\n        num_cpus: 4\r\n        ram: 16.5\r\n        disk_type: ssd\r\n\r\n\r\ngroups:\r\n  operation_Chat:\r\n    members:\r\n    - WebChat-Module\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 50 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies:\r\n        operation_MessageDatabase: \'2\'\r\n    - QoSRequirements:\r\n        response_time:\r\n          less_than: 2000.0 ms\r\n        availability:\r\n          greater_than: 0.998\r\n        cost:\r\n          less_or_equal: 200.0 euros_per_month\r\n        workload:\r\n          less_or_equal: 50.0 req_per_min\r\n      - ExpectedQuality: {expectedWorkload: 50.0, expectedAvailability: 0.9994997551225, expectedCost: 190.8, expectedExecutionTime: 0.10079721294968995}\r\n\r\n  operation_MessageDatabase:\r\n    members:\r\n    - MessageDatabase\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 30 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies: {}\r\n\r\n",
                            "tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03\r\ndescription: WebChat application\r\nimports:\r\n- tosca-normative-types:1.0.0.wd03-SNAPSHOT\r\ntopology_template:\r\n  node_templates:\r\n    WebChat-Module:\r\n      type: seaclouds.nodes.webapp.tomcat.TomcatServer\r\n      artifacts:\r\n      - war: \"http:\/\/search.maven.org\/remotecontent?filepath=io\/brooklyn\/example\/brooklyn-example-hello-world-sql-webapp\/0.6.0\/brooklyn-example-hello-world-sql-webapp-0.6.0.war\"\r\n        type: tosca.artifacts.File\r\n      requirements:\r\n      - endpoint: MessageDatabase\r\n      - host: pivotal\r\n\r\n\r\n    MessageDatabase:\r\n      type: seaclouds.nodes.database.mysql.MySqlNode\r\n      artifacts:\r\n      - db_create: https:\/\/bit.ly\/brooklyn-visitors-creation-script\r\n        type: tosca.artifacts.File\r\n      properties:\r\n        mysql_version:\r\n          constraints:\r\n          - greater_or_equal: \'5.0\'\r\n          - less_or_equal: \'5.0\'\r\n      requirements:\r\n      - host: My-DB-Compute\r\n\r\n    MessageDatabase-Compute:\r\n      type: seaclouds.Nodes.Compute\r\n      properties:\r\n        resource_type: compute\r\n        hardwareId: i2.xlarge\r\n        location: \"aws:ec2\"\r\n        region: \"eu-west-1\"\r\n        performance: 90\r\n        availability: 0.99950\r\n        country: Ireland\r\n        city: Dublig\r\n        cost: 1.001 USD\/hour\r\n        disk_size: 800\r\n        num_disks: 1\r\n        num_cpus: 4\r\n        ram: 30.5\r\n        disk_type: ssd\r\n\r\n    pivotal:\r\n      type: seaclouds.nodes.Platform.Pivotal\r\n      properties:\r\n        resource_type: platform\r\n        go_support: true\r\n        java_support: true\r\n        node_support: true\r\n        php_support: true\r\n        python_support: true\r\n        ruby_support: true\r\n        mysql_support: true\r\n        postgresql_support: true\r\n        mongoDB_support: true\r\n        redis_support: true\r\n        riak_support: true\r\n        dataStax_support: true\r\n        neo4j_support: true\r\n        pivotalHD_support: true\r\n        cost: 0.03 USD_perGB_per_h\r\n        java_version: 7\r\n\r\n\r\ngroups:\r\n  operation_Chat:\r\n    members:\r\n    - WebChat-Module\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 50 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies:\r\n        operation_MessageDatabase: \'2\'\r\n    - QoSRequirements:\r\n        response_time:\r\n          less_than: 2000.0 ms\r\n        availability:\r\n          greater_than: 0.998\r\n        cost:\r\n          less_or_equal: 200.0 euros_per_month\r\n        workload:\r\n          less_or_equal: 50.0 req_per_min\r\n      - ExpectedQuality: {expectedWorkload: 50.0, expectedAvailability: 0.9994997551225, expectedCost: 190.8, expectedExecutionTime: 0.10079721294968995}\r\n\r\n  operation_MessageDatabase:\r\n    members:\r\n    - MessageDatabase\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 30 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies: {}\r\n\r\n",
                            "tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03\r\ndescription: WebChat application\r\nimports:\r\n- tosca-normative-types:1.0.0.wd03-SNAPSHOT\r\ntopology_template:\r\n  node_templates:\r\n    WebChat-Module:\r\n      type: seaclouds.nodes.webapp.tomcat.TomcatServer\r\n      artifacts:\r\n      - war: \"http:\/\/search.maven.org\/remotecontent?filepath=io\/brooklyn\/example\/brooklyn-example-hello-world-sql-webapp\/0.6.0\/brooklyn-example-hello-world-sql-webapp-0.6.0.war\"\r\n        type: tosca.artifacts.File\r\n      requirements:\r\n      - endpoint: MessageDatabase\r\n      - host: webservice_offering\r\n\r\n\r\n    MessageDatabase:\r\n      type: seaclouds.nodes.database.mysql.MySqlNode\r\n      artifacts:\r\n      - db_create: https:\/\/bit.ly\/brooklyn-visitors-creation-script\r\n        type: tosca.artifacts.File\r\n      properties:\r\n        mysql_version:\r\n          constraints:\r\n          - greater_or_equal: \'5.0\'\r\n          - less_or_equal: \'5.0\'\r\n      requirements:\r\n      - host: My-DB-Compute\r\n\r\n    MessageDatabase-Compute:\r\n      type: seaclouds.Nodes.Compute\r\n      properties:\r\n        resource_type: compute\r\n        location: \"softlayer\"\r\n        region: \"18171\"\r\n        performance: 90\r\n        availability: 0.99950\r\n        country: EEUU\r\n        city: Seattle\r\n        cost: 1.101 USD\/hour\r\n        disk_size: 600\r\n        num_disks: 1\r\n        num_cpus: 2\r\n        ram: 8\r\n        disk_type: ssd\r\n\r\n    webservice_offering:\r\n      type: seaclouds.Nodes.Compute\r\n      properties:\r\n        resource_type: compute\r\n        hardwareId: i2.xlarge\r\n        location: \"aws:ec2\"\r\n        region: \"eu-west-1\"\r\n        performance: 90\r\n        availability: 0.99950\r\n        country: Ireland\r\n        city: Dublig\r\n        cost: 1.001 USD\/hour\r\n        disk_size: 800\r\n        num_disks: 1\r\n        num_cpus: 4\r\n        ram: 30.5\r\n        disk_type: ssd\r\n\r\ngroups:\r\n  operation_Chat:\r\n    members:\r\n    - WebChat-Module\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 50 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies:\r\n        operation_MessageDatabase: \'2\'\r\n    - QoSRequirements:\r\n        response_time:\r\n          less_than: 2000.0 ms\r\n        availability:\r\n          greater_than: 0.998\r\n        cost:\r\n          less_or_equal: 200.0 euros_per_month\r\n        workload:\r\n          less_or_equal: 50.0 req_per_min\r\n      - ExpectedQuality: {expectedWorkload: 50.0, expectedAvailability: 0.9994997551225, expectedCost: 190.8, expectedExecutionTime: 0.10079721294968995}\r\n\r\n  operation_MessageDatabase:\r\n    members:\r\n    - MessageDatabase\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 30 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies: {}\r\n\r\n",
                            "tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03\r\ndescription: WebChat application\r\nimports:\r\n- tosca-normative-types:1.0.0.wd03-SNAPSHOT\r\ntopology_template:\r\n  node_templates:\r\n    WebChat-Module:\r\n      type: seaclouds.nodes.webapp.tomcat.TomcatServer\r\n      artifacts:\r\n      - war: \"http:\/\/search.maven.org\/remotecontent?filepath=io\/brooklyn\/example\/brooklyn-example-hello-world-sql-webapp\/0.6.0\/brooklyn-example-hello-world-sql-webapp-0.6.0.war\"\r\n        type: tosca.artifacts.File\r\n      requirements:\r\n      - endpoint: MessageDatabase\r\n      - host: webservice_offering\r\n\r\n\r\n    MessageDatabase:\r\n      type: seaclouds.nodes.database.mysql.MySqlNode\r\n      artifacts:\r\n      - db_create: https:\/\/bit.ly\/brooklyn-visitors-creation-script\r\n        type: tosca.artifacts.File\r\n      properties:\r\n        mysql_version:\r\n          constraints:\r\n          - greater_or_equal: \'5.0\'\r\n          - less_or_equal: \'5.0\'\r\n      requirements:\r\n      - host: My-DB-Compute\r\n\r\n    MessageDatabase-Compute:\r\n      type: seaclouds.Nodes.Compute\r\n      properties:\r\n        resource_type: compute\r\n        hardwareId: i2.xlarge\r\n        location: \"aws:ec2\"\r\n        region: \"eu-west-1\"\r\n        performance: 90\r\n        availability: 0.99950\r\n        country: Ireland\r\n        city: Dublig\r\n        cost: 1.001 USD\/hour\r\n        disk_size: 800\r\n        num_disks: 1\r\n        num_cpus: 4\r\n        ram: 30.5\r\n        disk_type: ssd\r\n\r\n    webservice_offering:\r\n      type: seaclouds.Nodes.Compute\r\n      properties:\r\n        resource_type: compute\r\n        hardwareId: i2.xlarge\r\n        location: \"aws:ec2\"\r\n        region: \"eu-west-1\"\r\n        performance: 90\r\n        availability: 0.99950\r\n        country: Ireland\r\n        city: Dublig\r\n        cost: 1.001 USD\/hour\r\n        disk_size: 800\r\n        num_disks: 1\r\n        num_cpus: 4\r\n        ram: 30.5\r\n        disk_type: ssd\r\n\r\ngroups:\r\n  operation_Chat:\r\n    members:\r\n    - WebChat-Module\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 50 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies:\r\n        operation_MessageDatabase: \'2\'\r\n    - QoSRequirements:\r\n        response_time:\r\n          less_than: 2000.0 ms\r\n        availability:\r\n          greater_than: 0.998\r\n        cost:\r\n          less_or_equal: 200.0 euros_per_month\r\n        workload:\r\n          less_or_equal: 50.0 req_per_min\r\n      - ExpectedQuality: {expectedWorkload: 50.0, expectedAvailability: 0.9994997551225, expectedCost: 190.8, expectedExecutionTime: 0.10079721294968995}\r\n\r\n  operation_MessageDatabase:\r\n    members:\r\n    - MessageDatabase\r\n    policies:\r\n    - QoSInfo:\r\n        execution_time: 30 ms\r\n        benchmark_platform: hp_cloud_services.2xl\r\n    - dependencies: {}"
                        ]
                    }, 5000);
                    break;
                case 3:
                    $scope.applicationWizardData.finalDam = "tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03\r\n\r\nimports:\r\n  - tosca-normative-types:1.0.0.wd03-SNAPSHOT\r\n\r\ntemplate_name: brooklyn.a4c.simple.chatApplication\r\ntemplate_version: 1.0.0-SNAPSHOT\r\n\r\nnode_types:\r\n\r\n  seaclouds.nodes.Datacollector:\r\n    derived_from: tosca.nodes.Root\r\n    description: >\r\n      A simple Tomcat server\r\n    properties:\r\n      install_latch:\r\n        type: string\r\n        required: false\r\n    requirements:\r\n      - host: tosca.nodes.Compute\r\n        type: tosca.relationships.HostedOn\r\n\r\n  seaclouds.nodes.Compute:\r\n    derived_from: tosca.nodes.Compute\r\n    description: >\r\n      Adding hardareId.\r\n    properties:\r\n      hardwareId:\r\n        type: string\r\n        description: The host Operating System (OS) type.\r\n    capabilities:\r\n      host: tosca.capabilities.Container\r\n\r\n  org.apache.brooklyn.entity.webapp.tomcat.TomcatServer:\r\n    derived_from: tosca.nodes.Root\r\n    description: >\r\n      A simple Tomcat server\r\n    properties:\r\n      http.port:\r\n        type: list\r\n        required: false\r\n        entry_schema:\r\n          type: string\r\n      java.sysprops:\r\n        type: map\r\n        required: false\r\n        entry_schema:\r\n          type: string\r\n    requirements:\r\n      - host: tosca.nodes.Compute\r\n        type: tosca.relationships.HostedOn\r\n    artifacts:\r\n      - wars.root:\r\n        type: tosca.artifacts.File\r\n\r\n  org.apache.brooklyn.entity.database.mysql.MySqlNode:\r\n    derived_from: tosca.nodes.Root\r\n    description: >\r\n      A MySQL server\r\n    requirements:\r\n      - host: tosca.nodes.Compute\r\n        type: tosca.relationships.HostedOn\r\n    artifacts:\r\n      - datastore.creation.script.url:\r\n        type: tosca.artifacts.File\r\n\r\ntopology_template:\r\n  node_templates:\r\n    tomcat_server:\r\n      type: org.apache.brooklyn.entity.webapp.tomcat.TomcatServer\r\n      properties:\r\n        http.port: \"8080+\"\r\n        java.sysprops:\r\n          brooklyn.example.db.url: $brooklyn:formatString(\"jdbc:%s%s?user=%s\\\\&password=%s\", component(\"MessageDatabase\").attributeWhenReady(\"datastore.url\"), \"visitors\", \"brooklyn\", \"br00k11n\")\r\n      requirements:\r\n        - host: webservice_offering\r\n      artifacts:\r\n        wars.root:\r\n          implementation: \"http:\/\/search.maven.org\/remotecontent?filepath=io\/brooklyn\/example\/brooklyn-example-hello-world-sql-webapp\/0.6.0\/brooklyn-example-hello-world-sql-webapp-0.6.0.war\"\r\n          type: tosca.artifacts.File\r\n\r\n    MessageDatabase:\r\n      type: org.apache.brooklyn.entity.database.mysql.MySqlNode\r\n      requirements:\r\n        - host: MessageDatabase-Compute\r\n      artifacts:\r\n        datastore.creation.script.url:\r\n          implementation: \"https:\/\/github.com\/apache\/incubator-brooklyn\/raw\/master\/usage\/launcher\/src\/test\/resources\/visitors-creation-script.sql\"\r\n          type: tosca.artifacts.File\r\n\r\n    Data-collector:\r\n      type: seaclouds.nodes.Datacollector\r\n      properties:\r\n        install_latch: $brooklyn:component(\"MessageDatabase\").attributeWhenReady(\"service.isUp\")\r\n      requirements:\r\n        - host: MessageDatabase-Compute\r\n      interfaces:\r\n        Standard:\r\n           start: https:\/\/gist.githubusercontent.com\/kiuby88\/a6bb19c117353ae2edbb\/raw\/8943600efc0f0fdfdedc8209525c400035c36084\/dc-start.sh\r\n\r\n    webservice_offering:\r\n      type: seaclouds.nodes.PaaS\r\n      properties:\r\n        go_support: true\r\n        java_support: true\r\n        node_support: true\r\n        php_support: true\r\n        python_support: true\r\n        ruby_support: true\r\n        mysql_support: true\r\n        postgresql_support: true\r\n        mongoDB_support: true\r\n        redis_support: true\r\n        riak_support: true\r\n        dataStax_support: true\r\n        neo4j_support: true\r\n        pivotalHD_support: true\r\n        java_version: 7\r\n\r\n    MessageDatabase-Compute:\r\n      type: seaclouds.nodes.Compute\r\n      properties:\r\n        num_cpus: 1\r\n        disk_size: 10 GB\r\n        mem_size: 4 GB\r\n        hardwareId: m1.medium\r\n        os_distribution: ubuntu\r\n        os_version: 12.05\r\n        resource_type: compute\r\n        mysql_support: true\r\n        mysql_version: 5.4\r\n        availability: 0.99\r\n        performance: 62\r\n\r\n\r\n  groups:\r\n    add_brooklyn_location:\r\n      members: [ webservice_offering ]\r\n      policies:\r\n      - brooklyn.location: cloudfoundry-instance\r\n    add_brooklyn_location2:\r\n      members: [ MessageDatabase-Compute ]\r\n      policies:\r\n      - brooklyn.location: aws-ec2:us-west-2\r\n\r\n    operation_Chat:\r\n      members: [tomcat_server]\r\n      policies:\r\n      - QoSInfo: {execution_time: 50 ms, benchmark_platform: hp_cloud_services.2xl}\r\n      - dependencies: {operation_MessageDatabase: \'2\'}\r\n      - QoSRequirements:\r\n          response_time: {less_than: 2000.0 ms}\r\n          availability: {greater_than: 0.998}\r\n          cost: {less_or_equal: 200.0 euros_per_month}\r\n          workload: {less_or_equal: 50.0 req_per_min}\r\n    operation_MessageDatabase:\r\n      members: [MessageDatabase]\r\n      policies:\r\n      - QoSInfo: {execution_time: 30 ms, benchmark_platform: hp_cloud_services.2xl}\r\n      - dependencies: {}";
                    $scope.applicationWizardData.finalMonitoringRules = "<monitoringRules>\r\n\t<monitoringRule id=\"respTimeRule_Chat\" timeStep=\"10\" timeWindow=\"10\">\r\n    \t\t<monitoredTargets>\r\n        \t\t\t<monitoredTarget type=\"Chat\" class=\"InternalComponent\"\/>\r\n    \t\t<\/monitoredTargets>\r\n    \t\t<collectedMetric metricName=\"AvarageResponseTimeInternalComponent\"\/>\r\n    \t\t<actions>\r\n        \t\t\t<action name=\"OutputMetric\">\r\n            \t\t\t\t<parameter name=\"metric\">AvarageResponseTime_Chat<\/parameter>\r\n            \t\t\t\t<parameter name=\"value\">METRIC<\/parameter>\r\n            \t\t\t\t<parameter name=\"resourceId\">ID<\/parameter>\r\n        \t\t\t<\/action>\r\n    \t\t<\/actions>\r\n\t<\/monitoringRule>\r\n\r\n\t<monitoringRule id=\"respTimeSLARule_Chat\" timeStep=\"10\" timeWindow=\"10\">\r\n    \t\t<monitoredTargets>\r\n        \t\t\t<monitoredTarget type=\"Chat\" class=\"InternalComponent\"\/>\r\n    \t\t<\/monitoredTargets>\r\n    \t\t<collectedMetric metricName=\"AvarageResponseTimeInternalComponent\"\/>\r\n    \t\t<condition>METRIC &gt; 2000.0<\/condition>\r\n    \t\t<actions>\r\n        \t\t\t<action name=\"OutputMetric\">\r\n            \t\t\t\t<parametername=\"metric\">\r\n\t\t\t\t\t\tAvarageResponseTime_Chat_Violation\r\n\t\t\t\t\t<\/parameter>\r\n            \t\t\t\t<parameter name=\"value\">METRIC<\/parameter>\r\n            \t\t\t\t<parameter name=\"resourceId\">ID<\/parameter>\r\n        \t\t\t<\/action>\r\n    \t\t<\/actions>\r\n\t<\/monitoringRule>\r\n\r\n\t<monitoringRule id=\"vmAvailableSLARule_Chat\" timeStep=\"2\" timeWindow=\"2\">\r\n    \t\t<monitoredTargets>\r\n        \t\t\t<monitoredTarget type=\"Chat\" class=\"InternalComponent\"\/>\r\n    \t\t<\/monitoredTargets>\r\n    \t\t<collectedMetric metricName=\"AppAvailable\"\/>\r\n    \t\t<metricAggregation groupingClass=\"InternalComponent\" aggregateFunction=\"Average\"\/>\r\n    \t\t<condition>METRIC &lt; 0.998<\/condition>\r\n    \t\t<actions>\r\n        \t\t\t<action name=\"OutputMetric\">\r\n            \t\t\t\t<parameter name=\"metric\">\r\n\t\t\t\t\t\tAvarageAppAvailability_Chat_Violation\r\n\t\t\t\t\t<\/parameter>\r\n            \t\t\t\t<parameter name=\"value\">METRIC<\/parameter>\r\n            \t\t\t\t<parameter name=\"resourceId\">ID<\/parameter>\r\n        \t\t\t<\/action>\r\n    \t\t<\/actions>\r\n\t<\/monitoringRule>\r\n<\/monitoringRules>";
                    $scope.applicationWizardData.finalSlaRules = "<wsag:Agreement xmlns:wsag=\"http:\/\/www.ggf.org\/namespaces\/ws-agreement\">\r\n  <wsag:Name>AAM for SeaClouds Demo V2.0<\/wsag:Name>\r\n  <wsag:Context>\r\n    <wsag:AgreementInitiator>client<\/wsag:AgreementInitiator>\r\n    <wsag:AgreementResponder>seaclouds<\/wsag:AgreementResponder>\r\n    <wsag:ServiceProvider>AgreementResponder<\/wsag:ServiceProvider>\r\n    <sla:Service xmlns:sla=\"http:\/\/sla.atos.eu\">service<\/sla:Service>\r\n  <\/wsag:Context>\r\n  <wsag:Terms>\r\n    <wsag:All>\r\n      <wsag:ServiceProperties wsag:Name=\"NonFunctional\" wsag:ServiceName=\"default\">\r\n        <wsag:VariableSet>\r\n          <wsag:Variable wsag:Name=\"AvarageResponseTimeInternalComponent\" wsag:Metric=\"xs:double\"> \r\n            <wsag:Location><\/wsag:Location>\r\n          <\/wsag:Variable>\r\n          <wsag:Variable wsag:Name=\"AppAvailable\" wsag:Metric=\"xs:double\">\r\n            <wsag:Location><\/wsag:Location>\r\n          <\/wsag:Variable>\r\n        <\/wsag:VariableSet>\r\n      <\/wsag:ServiceProperties>\r\n      <wsag:GuaranteeTerm wsag:Name=\"ApplicationResponseTime\">\r\n        <wsag:ServiceLevelObjective>\r\n          <wsag:KPITarget>\r\n            <wsag:KPIName>ResponseTime<\/wsag:KPIName>\r\n            <wsag:CustomServiceLevel>{\"constraint\": \"AvarageResponseTime_Chat_Violation\", \"qos\" : \"AvarageResponseTimeInternalComponent LE 2000\"}<\/wsag:CustomServiceLevel>\r\n          <\/wsag:KPITarget>\r\n        <\/wsag:ServiceLevelObjective>\r\n      <\/wsag:GuaranteeTerm>\r\n      <wsag:GuaranteeTerm wsag:Name=\"ApplicationAvailability\">\r\n        <wsag:ServiceLevelObjective>\r\n          <wsag:KPITarget>\r\n            <wsag:KPIName>AppAvailable<\/wsag:KPIName>\r\n            <wsag:CustomServiceLevel>{\"constraint\": \"AvarageAppAvailability_Chat_Violation\", \"qos\" : \"AppAvailable GE 0.998\"}<\/wsag:CustomServiceLevel>\r\n          <\/wsag:KPITarget>\r\n        <\/wsag:ServiceLevelObjective>\r\n      <\/wsag:GuaranteeTerm>\r\n    <\/wsag:All>\r\n  <\/wsag:Terms>\r\n<\/wsag:Agreement>";
                    $scope.damGenerated = true;
                    $scope.currentStep++;
                    break;
                case 4:
                    $scope.deployApplication();
                    $scope.currentStep++;
                    break;
                case 5:
                default:
                    break;
            }
        };

        $scope.wizardCanRollback = function () {
            switch ($scope.currentStep) {
                case 1:
                    return false;
                case 2:
                case 3:
                case 4:
                    return true;
                case 5:
                    return false;
            }
        }

        $scope.wizardCanContinue = function () {
            switch ($scope.currentStep) {
                case 1:
                    return $scope.applicationWizardData.name && $scope.applicationWizardData.application_requirements.availability &&
                        $scope.applicationWizardData.application_requirements.cost && $scope.applicationWizardData.application_requirements.response_time &&
                        $scope.applicationWizardData.application_requirements.workload;
                case 2:
                    return $scope.applicationWizardData.topology.nodes.length;
                case 3:
                    return $scope.applicationWizardData.finalAdp;
                case 4:
                    return $scope.applicationWizardData.finalDam;
                case 5:
                    return true;
            }
        }

        $scope.codemirrorFeasibleDamOptions = {
            mode: 'yaml',
            readOnly: true,
            cursorBlinkRate: -1
        };


        $scope.codemirrorDamOptions = {
            mode: 'yaml',
            lineNumbers: true,
        };


        $scope.codemirrorSlaRulesOptions = {
            mode: 'xml',
            lineNumbers: true,
        };

        $scope.codeMirrorMonitoringRulesOptions = {
            mode: 'xml',
            lineNumbers: true,
        };
    })
    .directive('addApplicationWizard', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/add-application-wizard.html',
            controller: 'AddApplicationWizardCtrl'
        };
    })
    .directive('wizardStep1', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-1.html',
            scope: true
            //controller: 'AddApplicationWizardCtrl'
        };
    })
    .directive('wizardStep2', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-2.html',
            scope: true,
        };
    })
    .directive('wizardStep3', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-3.html',
            scope: true,
            controller: function ($scope, $element) {
                var MAX_ITEM_PER_PAGE = 3
                var currentPage = 0;
                $scope.getCurrentlyVisibleFeasibleAdps = function () {
                    $scope.MAX_PAGES = Math.floor($scope.applicationWizardData.feasibleAdps.length / MAX_ITEM_PER_PAGE);
                    return $scope.applicationWizardData.feasibleAdps.slice(currentPage * MAX_ITEM_PER_PAGE,
                        currentPage * MAX_ITEM_PER_PAGE + MAX_ITEM_PER_PAGE);
                }

                $scope.getCurrentPage = function () {
                    return currentPage;
                }

                $scope.nextPage = function () {
                    currentPage++;
                }

                $scope.previousPage = function () {
                    currentPage--;
                }

                $scope.setFinalAdp = function (finalAdp) {
                    $scope.applicationWizardData.finalAdp = finalAdp;
                }
            }
        };
    })
    .directive('wizardStep4', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-4.html',
            scope: true
        };
    })
    .directive('wizardStep5', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-5.html',
            scope: true,
            controller: function ($scope, $interval, notificationService) {
                $scope.applicationWizardData.brooklynAppTopology = {
                    "nodes": [],
                    "links": []
                },

                    $scope.$watch('applicationWizardData.id', function (newValue) {
                        if (newValue) {
                            $scope.updateFunction = $interval(function () {
                                $scope.SeaCloudsApi.getProject($scope.applicationWizardData.id).
                                    success(function (project) {
                                        $scope.applicationWizardData.brooklynAppTopology = TopologyEditorUtils.getTopologyFromEntities(project);
                                    }).error(function () {
                                        //TODO: Handle the error better than showing a notification
                                        notificationService.error("Unable to retrieve the projects");
                                    })
                            }, 5000);
                        }
                    });

                $scope.$on('$destroy', function () {
                    if ($scope.updateFunction) {
                        $interval.cancel($scope.updateFunction);
                    }
                });
            }
        }
    })
