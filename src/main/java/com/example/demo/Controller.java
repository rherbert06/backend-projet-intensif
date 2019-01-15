package com.example.demo;

import com.example.demo.Database.Event;
import com.example.demo.Database.EventRepository;
import com.example.demo.EventJSON.EventJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;


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
    public void createNewEvent(@RequestBody EventJSON json) {
        LOG.info("-- newEvent --");

        int n1 = Integer.parseInt(json.getValues().getValue1());
        int n2 = Integer.parseInt(json.getValues().getValue2());
        int n3 = Integer.parseInt(json.getValues().getValue3());

        repository.save(new Event( n1, n2, n3));
    }

    @RequestMapping(value = "/exampleDB", method = RequestMethod.GET)
    public void exampleDataBase() {
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
    }
}