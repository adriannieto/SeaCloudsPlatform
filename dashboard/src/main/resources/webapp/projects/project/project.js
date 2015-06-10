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

angular.module('seacloudsDashboard.projects.project', ['ngRoute', 'seacloudsDashboard.projects', 'seacloudsDashboard.projects.project.status',
    'seacloudsDashboard.projects.project.monitor', 'seacloudsDashboard.projects.project.sla'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/projects/:project', {
            templateUrl: 'projects/project/project.html'
        })
    }])
    .controller('ProjectCtrl', function ($scope, $routeParams, $location, notificationService) {
        $scope.Page.setTitle('SeaClouds Dashboard - Project details');

        $scope.project = undefined;

        $scope.Projects.getProject($routeParams.project).
            success(function(project){
                $scope.project = project;
            }).
            error(function(){
                notificationService.error("Cannot retrieve the project, please try it again");
            })



        $scope.$watch('projects', function (data) {
            $scope.Projects.getProject($routeParams.project).
                success(function(project){
                    $scope.project = project;
                }).
                error(function(){
                    notificationService.error("Cannot retrieve the project, please try it again");
                })
        })

        var tabSelected = 1;

        $scope.getSelectedTab = function () {
            return tabSelected;
        }

        $scope.setSelectedTab = function (tab) {
            tabSelected = tab;
        }

        $scope.removeProject = function () {
            $scope.Projects.removeProject($scope.project.id).
                success(function () {
                    $location.path("/projects")
                    notificationService.success("The application will be removed soon");
                }).
                error(function () {
                    notificationService.error("An error happened during the removal process, please try it again");
                })
        }

    });