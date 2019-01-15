package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping(value = "/ecociteTeam")
public class Controller {
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    @PostConstruct
    public void launchApp() {
        LOG.info("Backend is running...");
    }


    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String Hello() {
        LOG.info("--- HELLO ---");

        return "Hello !";
    }
}