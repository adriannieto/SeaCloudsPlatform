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

package eu.seaclouds.platform.dashboard;

import com.codahale.metrics.health.HealthCheck;
import eu.seaclouds.platform.dashboard.proxy.DeployerProxy;
import eu.seaclouds.platform.dashboard.proxy.MonitorProxy;
import eu.seaclouds.platform.dashboard.proxy.PlannerProxy;
import eu.seaclouds.platform.dashboard.proxy.SlaProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;

public class DashboardHealthCheck extends HealthCheck {
    private static final Logger LOG = LoggerFactory.getLogger(DashboardHealthCheck.class);
    private static final int TIMEOUT = 500;
    private static final String NAME = "reachability";

    private final DeployerProxy deployer;
    private final MonitorProxy monitor;
    private final SlaProxy sla;
    private final PlannerProxy planner;

    private boolean portIsOpen(String ip, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), DashboardHealthCheck.TIMEOUT);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public DashboardHealthCheck(DeployerProxy deployer, MonitorProxy monitor, SlaProxy sla, PlannerProxy planner){
        this.deployer = deployer;
        this.monitor = monitor;
        this.sla = sla;
        this.planner = planner;
    }

    public String getName(){
        return DashboardHealthCheck.NAME;
    }

    @Override
    protected HealthCheck.Result check() throws Exception {
        DashboardHealthCheck.LOG.warn("This is NOT an integration test. The current Healthcheck only checks if all the endpoints are reachable");

        if(!this.portIsOpen(this.deployer.getHost(), this.deployer.getPort())){
            return HealthCheck.Result.unhealthy("The Deployer endpoint is not ready");
        }

        if(!this.portIsOpen(this.monitor.getHost(), this.monitor.getPort())){
            return HealthCheck.Result.unhealthy("The Monitor endpoint is not ready");
        }

        if(!this.portIsOpen(this.sla.getHost(), this.sla.getPort())) {
            return HealthCheck.Result.unhealthy("The SLA endpoint is not ready");
        }

        if(!this.portIsOpen(this.planner.getHost(), this.planner.getPort())){
            return HealthCheck.Result.unhealthy("The Planner endpoint is not ready");
        }

        return HealthCheck.Result.healthy();
    }
}
