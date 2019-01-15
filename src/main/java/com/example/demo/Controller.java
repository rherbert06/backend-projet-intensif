package com.example.demo;

import com.example.demo.Database.Event;
import com.example.demo.Database.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;

import java.util.Random;


// JPA hibernate

@RestController
@RequestMapping(value = "/ecociteTeam")
public class Controller {
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    @Autowired
    EventRepository repository;

    @PostConstruct
    public void launchApp() {
        LOG.info("Backend is running...");
    }


    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String Hello() {
        LOG.info("--- HELLO ---");

        // save a couple of events
        repository.save(new Event(50, 50, 50));
        repository.save(new Event(0, 50, 50));
        repository.save(new Event(0, 0, 50));
        repository.save(new Event(50, 0, 25));
        repository.save(new Event(50, 50, 10));

        // fetch all events
        LOG.info("Events found with findAll():");
        LOG.info("-------------------------------");
        for (Event event : repository.findAll()) {
            LOG.info(event.toString());
        }

        LOG.info("");

        // fetch an individual event by ID
        repository.findById(1L)
                .ifPresent(customer -> {
                    LOG.info("Event found with findById(1L):");
                    LOG.info("--------------------------------");
                    LOG.info(customer.toString());
                    LOG.info("");
                });

        // fetch last event
        LOG.info("Last event :");
        LOG.info("--------------------------------------------");
        LOG.info(repository.findTopByOrderByIdDesc().toString());
        LOG.info("");

        return "Hello !";
    }


    @RequestMapping(value = "/lastEvent", method = RequestMethod.GET)
    public String fetchLastEvent() {
        return repository.findTopByOrderByIdDesc().toString();
    }

    @RequestMapping(value = "/last50Event", method = RequestMethod.GET)
    public String fetchLast50Event() {
        return repository.findTop50ByOrderByIdDesc().toString();
    }

    @RequestMapping(value = "/newEvent", method = RequestMethod.POST)
    public void createNewEvent( ) {
        LOG.info("-- newEvent --");
        Random rand = new Random();
        int n1 = (rand.nextInt(50)%2) * 50;
        int n2 = (rand.nextInt(50)%2) * 50;
        int n3 = rand.nextInt(50);
        repository.save(new Event( n1, n2, n3));
    }
}