package com.alicloud.microservices.refapp.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class SampleServiceApplication {

    @Autowired
    private DiscoveryClient discoveryClient;


    public static void main(String[] args) {
        SpringApplication.run(SampleServiceApplication.class, args);
        System.out.println("Running " + SampleServiceApplication.class + " via Spring Boot!");
    }

    @RequestMapping("/")
    public String home(@RequestParam(value = "service", required = false) String serviceName)
            throws MalformedURLException {
        List<ServiceInstance> list = discoveryClient.getInstances(serviceName);
        if (list != null && list.size() > 0) {
            String serviceURL = list.get(0).getUri().toURL().toString();
            serviceURL += "/hello?service=" + serviceURL;
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(serviceURL, String.class);

        }
        return "Hello! This is from Sample Service 1!";
    }

    @RequestMapping("/hello")
    public String hello(@RequestParam(value = "service", required = false) String serviceName) {
        String services = discoveryClient.getServices().stream().collect(Collectors.joining(","));


        return "Hello! This is from " + "! ," + services + " ,client: " + discoveryClient.description();
    }
}
