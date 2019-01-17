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
import java.util.Date;
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
                    while (true){
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

        JsonObject value = factory.createObjectBuilder()
                .add("leds", factory.createObjectBuilder()
                    .add("led1", e.getValue1())
                    .add("led2", e.getValue2())
                    .add("led3", e.getValue3()))
                .build();

        return value.toString();
    }


    @RequestMapping(value = "/getMeanOverDuration", method = RequestMethod.GET)
    public String getScoreForDuration(@RequestParam("duration") String sDuration) {
        int duration = Integer.parseInt(sDuration);
        JsonObject value;
        double mean1 = 0;
        double mean2 = 0;
        double mean3 = 0;

        long systemTime = System.currentTimeMillis();


        if (repository.count() < 1) {
            value = factory.createObjectBuilder()
                    .add("error", "Pas assez de données pour la période")
                    .build();
            return value.toString();
        }


        if (new Date(systemTime - duration * 1000).getTime() < repository.findTopByOrderById().get(0).getDate().getTime()) {
            value = factory.createObjectBuilder()
                    .add("error", "Durée spécifiée trop longue")
                    .build();
            return value.toString();
        }

        List<Event> events = repository.findByDateAfter(new Date(systemTime - duration * 1000));

        Event lastEvent = null;

        for (Event e : events) {

            long eventDateTime = e.getDate().getTime();

            if (lastEvent == null ) { // First event of the list
                lastEvent = repository.findById(e.getId()-1).get();
                long timeLapse = new Date(systemTime - duration * 1000).getTime();
                long factor = eventDateTime - timeLapse;

                mean1 += lastEvent.getValue1() * factor;
                mean2 += lastEvent.getValue2() * factor;
                mean3 += lastEvent.getValue3() * factor;
            } else {
                long lastEventDateTime = lastEvent.getDate().getTime();
                long factor = eventDateTime - lastEventDateTime;
                mean1 += lastEvent.getValue1() * factor;
                mean2 += lastEvent.getValue2() * factor;
                mean3 += lastEvent.getValue3() * factor;
            }
            lastEvent = e;
        }

        long currentTime = new Date(systemTime).getTime();
        long timeLapse = new Date(systemTime - duration * 1000).getTime();


        if (lastEvent == null) { // Mean that during last period of time no event were registered
            lastEvent = repository.findTopByOrderByIdDesc();
            long factor = currentTime - timeLapse;
            mean1 += lastEvent.getValue1() * factor;
            mean2 += lastEvent.getValue2() * factor;
            mean3 += lastEvent.getValue3() * factor;
        } else {
            long lastEventTime = lastEvent.getDate().getTime();
            long factor = currentTime - lastEventTime;
            mean1 += lastEvent.getValue1() * factor;
            mean2 += lastEvent.getValue2() * factor;
            mean3 += lastEvent.getValue3() * factor;
        }


        mean1 /=  100 * duration * 1000;
        mean2 /=  100 * duration * 1000;
        mean3 /=  100 * duration * 1000;

        double score;
        score =  100 - ((mean1 + mean2 + mean3) * 100)/3;

        value = factory.createObjectBuilder()
                .add("score", score)
                .build();

        return value.toString();
    }

    @RequestMapping(value = "/getMeanOverOneMonth", method = RequestMethod.GET)
    public String getScoreOfTheMonth() {
        int day = 15;
        int duration = 30 * day;
        return getScoreForDuration(Integer.toString(duration));
    }

    @RequestMapping(value = "/getMeanOverOneyear", method = RequestMethod.GET)
    public String getScoreOfTheYear() {
        int day = 15;
        int duration = 365* day;
        return getScoreForDuration(Integer.toString(duration));
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String reset() {
        repository.deleteAll();
        Controller.taskRunning = false;
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

        if (!Controller.taskRunning){
            executeAsynchronously();
            Controller.taskRunning = true;
        }
    }
}