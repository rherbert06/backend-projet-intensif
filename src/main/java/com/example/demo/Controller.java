package com.example.demo;

import com.example.demo.Database.Event;
import com.example.demo.Database.EventRepository;
import com.example.demo.EventJSON.EventJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import javax.json.*;


@RestController
@RequestMapping(value = "/ecociteTeam")
public class Controller {
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);
    private static boolean taskRunning = false;

    private JsonBuilderFactory factory = Json.createBuilderFactory(null);


    @Autowired
    EventRepository repository;

    @Qualifier("applicationTaskExecutor")
    @Autowired
    private TaskExecutor taskExecutor;

    public void executeAsynchronously() {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Event e;
                try {
                    while (taskRunning){
                        e = repository.findTopByOrderByIdDesc();
                        repository.save(new Event(e.getValue1(), e.getValue2(),e.getValue3()));
                        LOG.info("-- AYYY LMAO ASYNCHRONOUS AND SHIT --");
                        Thread.sleep(3000);
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    @PostConstruct
    public void launchApp() {
        LOG.info("Backend is running...");
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String Hello() {
        LOG.info("--- HELLO ---");
        return "Hello !";
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public String getAllEvents() {
        List<Event> events = repository.findAll();

        int led1conso = 0;
        int led2conso = 0;
        int led3conso = 0;

        int led1clicks = 0;
        int led2clicks = 0;
        int led3clicks = 0;

        int prev1 = -1;
        int prev2 = -1;
        int prev3 = -1;

        for (Event e : events){
            led1conso += e.getValue1();
            led2conso += e.getValue2();
            led3conso += e.getValue3();

            if (e.getValue1() != prev1) {
                led1clicks++;
                prev1 = e.getValue1();
            }

            if (e.getValue2() != prev2) {
                led2clicks++;
                prev2 = e.getValue2();
            }

            if (e.getValue3() != prev3) {
                led3clicks++;
                prev3 = e.getValue3();
            }
        }

        JsonObject value = factory.createObjectBuilder()
                .add("leds", factory.createObjectBuilder()
                .add("led1", led1conso)
                .add("led2", led2conso)
                .add("led3", led3conso))
                .add("clicks", factory.createObjectBuilder()
                        .add("led1", led1clicks)
                        .add("led2", led2clicks)
                        .add("led3", led3clicks))
                .build();

        return value.toString();
    }

    @RequestMapping(value = "/lastEvent", method = RequestMethod.GET)
    public String fetchLastEvent() {
        Event e = repository.findTopByOrderByIdDesc();

        if (e == null) return "{\"error\": \"evt null\"}";

        JsonObject value = factory.createObjectBuilder()
                .add("leds", factory.createObjectBuilder()
                    .add("led1", e.getValue1())
                    .add("led2", e.getValue2())
                    .add("led3", e.getValue3()))
                .build();

        return value.toString();
    }

    @RequestMapping(value = "/getMean", method = RequestMethod.GET)
    public String getScoreFor6s() {
        return getScore(0);

    }

    public String getScore(int duration) {
        double score = 0;
        JsonObject value;
        Event e = repository.findTopByOrderByIdDesc();
        if (e == null) return "{\"score\": \"0\"}";
        score = (double)(e.getValue1()+e.getValue2()+e.getValue3())/(double)(50+100+300);
        score = 1.0 - score;
        score = (int)(score * 100.0);

        if (duration > 1) score *= duration;

        value = factory.createObjectBuilder()
                .add("score",  score)
                .build();

        return value.toString();
    }

    @RequestMapping(value = "/getMeanOverOneMonth", method = RequestMethod.GET)
    public String getScoreOfTheMonth() {
        //Mesure arbitraire
        int day = 15;
        int duration = 30 * day;
        return getScore(duration);
    }

    @RequestMapping(value = "/getMeanOverOneyear", method = RequestMethod.GET)
    public String getScoreOfTheYear() {
        //Mesure arbitraire
        int day = 15;
        int duration = 365* day;
        return getScore(duration);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String reset() {
        Event e = repository.findTopByOrderByIdDesc();
        repository.deleteAll();
        Controller.taskRunning = false;
        repository.save(new Event(e.getValue1(), e.getValue2(),e.getValue3()));
        return "Reset OK";
    }

    @RequestMapping(value = "/newEvent", method = RequestMethod.POST)
    public void createNewEvent(@RequestBody EventJSON json) {
        LOG.info("-- newEvent --");

        int n1 = Integer.parseInt(json.getValues().getValue1());
        int n2 = Integer.parseInt(json.getValues().getValue2());
        int n3 = Integer.parseInt(json.getValues().getValue3());

        repository.save(new Event( n1, n2, n3));

        if (!Controller.taskRunning){
            executeAsynchronously();
            Controller.taskRunning = true;
        }
    }

    /**
     * For testing purposes only
     */
    @RequestMapping(value = "/exampleDB", method = RequestMethod.GET)
    public void exampleDataBase() {
        // save a couple of events
        repository.save(new Event(50, 50, 50));
        repository.save(new Event(0, 50, 50));
        repository.save(new Event(0, 0, 50));
        repository.save(new Event(50, 0, 25));
        repository.save(new Event(50, 50, 0));

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