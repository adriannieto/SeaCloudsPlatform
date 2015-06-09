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

angular.module('seacloudsDashboard.projects', ['ngRoute', 'ngFitText',
    'seacloudsDashboard.projects.project'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/projects', {
            templateUrl: 'projects/projects.html'
        })
    }])
    .controller('ProjectsCtrl', function ($scope) {
        $scope.projects = $scope.Projects.getProjects();
        ;
        $scope.Page.setTitle('SeaClouds Dashboard - Projects overview');

        var slaOK = "The application is satisfying the SLA's";
        var slaFAILED = "There are some violations in SLA's";

        $scope.getSLATooltip = function (project) {
            if(project.slaStatus == "OK") {
                return slaOK;
            }else{
                return slaFAILED;
            }
        }

        var statusOK = "The application is running";
        var statusSTARTING = "The application is starting";
        var statusRECONFIGURING = "The application needs to be reconfigured";
        var statusFAILURE = "The application failed during the execution";

        $scope.getStatusTooltip = function (project) {
            if (project.status == "OK") {
                return statusOK;
            }else if (project.status == "STARTING"){
                return statusSTARTING;
            }else if (project.status == "RECONFIGURE"){
                return statusRECONFIGURING;
            }else{
                return statusFAILURE;
            }

        }
    });