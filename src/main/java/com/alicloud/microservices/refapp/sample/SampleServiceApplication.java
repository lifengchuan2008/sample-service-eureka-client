package com.alicloud.microservices.refapp.sample;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@RestController
public class SampleServiceApplication {

    private static final String HOSTNAME = "HOSTNAME";

    private AtomicInteger count = new AtomicInteger(0);

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
    @ResponseBody
    public ResponseEntity hello(@RequestParam(value = "service", required = false) String serviceName2) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test", "sdsdf");
        jsonObject.put("demo", "demo");
        jsonObject.put("count", count.incrementAndGet());


        return ResponseEntity.ok(jsonObject);
    }
}
