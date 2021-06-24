package com.pd;

import com.pd.hadoop.hbase.HBaseClientTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@ServletComponentScan
public class ServerApplication {
    public Logger logger = LoggerFactory.getLogger(ServerApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
