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


angular.module('seacloudsDashboard.projects.project.status', [])
    .directive('status', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/project/status/status.html',
            controller: 'StatusCtrl'
        };
    })
    .controller('StatusCtrl', function ($scope) {


        var generateTopology = function (parentEntity) {
            var parentTopology = {
                nodes: [],
                links: []
            }

            if (parentEntity) {
                var type = undefined;
                if(parentEntity.type.search("database") >= 0){
                    type = "Database"
                }else if(parentEntity.type.search("webapp") >= 0){
                    type = "WebApplication"
                }else {
                    type = "BasicApplication"
                }

                parentTopology.nodes.push(
                    {
                        name: parentEntity.name,
                        label: parentEntity.name,
                        status: parentEntity.serviceState,
                        type : type
                    })

                if (parentEntity.children) {
                    parentEntity.children.forEach(function (childEntity) {
                        parentTopology.links.push({
                            source: parentEntity.name,
                            target: childEntity.name,
                            properties: {}
                        })

                        var childTopology = generateTopology(childEntity);
                        parentTopology.nodes = parentTopology.nodes.concat(childTopology.nodes);
                        parentTopology.links = parentTopology.links.concat(childTopology.links);
                    })
                }
            }

            return parentTopology;


        }
        $scope.topology = generateTopology($scope.project);
    });