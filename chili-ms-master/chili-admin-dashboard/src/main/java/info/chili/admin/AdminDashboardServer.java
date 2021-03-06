/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.chili.admin;

import be.ordina.msdashboard.EnableMicroservicesDashboardServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *
 * @author phani
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableMicroservicesDashboardServer
public class AdminDashboardServer {

    public static void main(String[] args) {
        SpringApplication.run(AdminDashboardServer.class, args);
    }
}
