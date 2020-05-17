package com.baffle.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

@RestController
public class HelloController {

    @Autowired
            @Qualifier("restTemplateOne")
    RestTemplate restTemplateOne;

    @Autowired
    DiscoveryClient discoveryClient;

    @GetMapping("/hello2")
    public String hello2() {
        List<ServiceInstance> list = discoveryClient.getInstances("provider");
        ServiceInstance instance = list.get(0);
        String host = instance.getHost();
        int port = instance.getPort();
        StringBuffer sb = new StringBuffer();
        sb.append("http://")
                .append(host)
                .append(":")
                .append(port)
                .append("/hello");
        HttpURLConnection conn = null;
        try {
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String s = br.readLine();
                br.close();
                return s;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "error";
    }


    @Autowired
    @Qualifier("restTemplate")
    RestTemplate restTemplate;

    @GetMapping("hello3")
    public String hello3(){
        return restTemplate.getForObject("http://provider/hello", String.class);
    }

    @GetMapping("hello4")
    public String hello4(){
        String strA = restTemplate.getForObject("http://provider/hello2?name={1}", String.class,"kongqh");
        System.out.println(strA);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://provider/hello2?name={1}", String.class,"kongqh");
        String body = responseEntity.getBody();
        HttpStatus httpStatus = responseEntity.getStatusCode();
        int statusCodeValue = responseEntity.getStatusCodeValue();
        HttpHeaders headers = responseEntity.getHeaders();
        Set<String> keyset = headers.keySet();
        for(String s: keyset) {

        }
        return strA + "---\r\n"
                + "body:" + body
                + "httpstatus:" + httpStatus
                + "statusCodeValue:" + statusCodeValue;
    }

}
