package com.example.demo;

import com.example.demo.Database.Event;
import com.example.demo.Database.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BackendApplication extends SpringBootServletInitializer{

    @Autowired
    static EventRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);

    }
}

