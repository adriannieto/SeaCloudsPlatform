/*
    Copyright 2014 SeaClouds
    Contact: SeaClouds

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
'use strict';

var seacloudsDashboard = angular.module('seacloudsDashboard', [
    'ui.bootstrap',
    'ngAnimate',
    'seacloudsDashboard.header',
    'seacloudsDashboard.footer',
    'seacloudsDashboard.signin',
    'seacloudsDashboard.about',
    'seacloudsDashboard.help',
    'seacloudsDashboard.projects',
    'seacloudsDashboard.projects.addApplicationWizard'

]);

seacloudsDashboard.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when("/",{redirectTo: '/projects'})
    //TODO: Create a not available view
    $routeProvider.otherwise({redirectTo: '/not-available.html'});
}]);


seacloudsDashboard.factory('Page', function(){
    var title = 'SeaClouds Dashboard';
    return {
        getTitle: function() { return title; },
        setTitle: function(newTitle) { title = newTitle; }
    };
});

seacloudsDashboard.factory('UserCredentials', function($location){
    var authenticatedUser = {
        id: 1337,
        username: "Manager",
        email: "admin@example.com"
    };

    return {
        getUser: function() { return authenticatedUser; },
        isUserAuthenticated : function() { return !(!authenticatedUser)},
        login : function(userCredentials) {
            authenticatedUser = {
                id: 1337,
                username: "Manager",
                email: "admin@example.com"
            };
            $location.path('/projects');
        },
        logout : function(){
            authenticatedUser = undefined;
            $location.path('/signin');
        }
    };

});

seacloudsDashboard.factory('Projects', function(){
    var projects = [
        {id: 1, name:'WebChat Application', status:'STARTING', slaStatus:'OK'},
        {id: 2, name:'Nuro Game Server', status:'OK',  slaStatus:'FAILED'},
        {id: 3, name:'IoT Broker', status:'OK',  slaStatus:'OK'},
        {id: 4, name:'A really big application name', status:"FAILURE",  slaStatus:'OK'},
        {id: 5, name:'ATOS Case Study', status:"RECONFIGURE",  slaStatus:'FAILED'},
        {id: 4, name:'An awesome application', status:"OK",  slaStatus:'OK'}];

    return {
        getProjects: function() { return projects; },
        getProject: function(index){
            return projects.filter(function(project){
              return project.id == index;
            })[0]}
    };
});



seacloudsDashboard.controller('GlobalCtrl', function($scope, Page, UserCredentials, Projects){
    $scope.Page = Page;
    $scope.UserCredentials = UserCredentials;
    $scope.Projects = Projects;

    if(!UserCredentials.isUserAuthenticated()){
        $location.path('/access-restricted.html');
    }
});

