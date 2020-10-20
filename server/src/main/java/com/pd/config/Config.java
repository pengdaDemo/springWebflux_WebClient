package com.pd.config;

import com.pd.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Config {
    @Bean
    public Person testConfig(){
        Person person= new Person();
        person.name = "测试配置";
        return person;
    }
}
