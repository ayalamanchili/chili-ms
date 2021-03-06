/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.chili.admin;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *
 * @author phani
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAdminServer
public class AdminServer {

    public static void main(String[] args) {
        SpringApplication.run(AdminServer.class, args);
    }
}
