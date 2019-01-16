package com.example.demo;

import com.example.demo.Database.Event;
import com.example.demo.Database.EventRepository;
import com.example.demo.EventJSON.EventJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.List;
import java.util.Date;


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

    @RequestMapping(value = "/getMeanOverDuration", method = RequestMethod.GET)
    public int getScoreOfTheDay(@RequestParam("duration") String sDuration) {
        int duration = Integer.parseInt(sDuration);
        double mean1 = 0;
        double mean2 = 0;
        double mean3 = 0;


        if (repository.count() < 2) {
            return -1; // Not enough event in DB
        }


        if (new Date(System.currentTimeMillis() - duration * 1000).getTime() < repository.findById(1L).get().getDate().getTime()) {
            return -1; // Duration too high
        }

        List<Event> events = repository.findByDateAfter(new Date(System.currentTimeMillis() - duration * 1000));

        Event lastEvent = null;

        for (Event e : events) {
            if (lastEvent == null ) { // First event of the list
                lastEvent = repository.findById(e.getId()-1).get();

                mean1 += lastEvent.getValue1() * (e.getDate().getTime() - (new Date(System.currentTimeMillis() - duration * 1000).getTime()));
                mean2 += lastEvent.getValue2() * (e.getDate().getTime() - (new Date(System.currentTimeMillis() - duration * 1000).getTime()));
                mean3 += lastEvent.getValue3() * (e.getDate().getTime() - (new Date(System.currentTimeMillis() - duration * 1000).getTime()));
                lastEvent = e;
            } else {
                mean1 += lastEvent.getValue1() * (e.getDate().getTime() - lastEvent.getDate().getTime());
                mean2 += lastEvent.getValue2() * (e.getDate().getTime() - lastEvent.getDate().getTime());
                mean3 += lastEvent.getValue3() * (e.getDate().getTime() - lastEvent.getDate().getTime());
                lastEvent = e;
            }
        }

        if (lastEvent == null) { // Mean that during last period of time no event were registered
            lastEvent = repository.findTopByOrderByIdDesc();
            mean1 += lastEvent.getValue1() * ((new Date(System.currentTimeMillis())).getTime() - (new Date(System.currentTimeMillis() - duration * 1000).getTime()));
            mean2 += lastEvent.getValue2() * ((new Date(System.currentTimeMillis())).getTime() - (new Date(System.currentTimeMillis() - duration * 1000).getTime()));
            mean3 += lastEvent.getValue3() * ((new Date(System.currentTimeMillis())).getTime() - (new Date(System.currentTimeMillis() - duration * 1000).getTime()));
        } else {
            mean1 += lastEvent.getValue1() * ((new Date(System.currentTimeMillis())).getTime() - lastEvent.getDate().getTime());
            mean2 += lastEvent.getValue2() * ((new Date(System.currentTimeMillis())).getTime() - lastEvent.getDate().getTime());
            mean3 += lastEvent.getValue3() * ((new Date(System.currentTimeMillis())).getTime() - lastEvent.getDate().getTime());
        }


        mean1 /=  100 * duration * 1000;
        mean2 /=  100 * duration * 1000;
        mean3 /=  100 * duration * 1000;

        double score;

        if (mean1 == 0 && mean2 == 0 && mean3 == 0) {
            score = 100;
        } else {
            score =  ((mean1 + mean2 + mean3) * 100)/3;
            score = 100-score;
        }

        return (int) score;
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