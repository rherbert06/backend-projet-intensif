package com.example.demo.Database;
import java.util.List;
import java.util.Date;

import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
    // Equivalent to find the last event
    Event findTopByOrderByIdDesc();

    // Equivalent to find the last 2 events
    List<Event> findTop2ByOrderByIdDesc();

    // Get events since <date>
    List<Event> findByDateAfter(Date date);

    // Get all events
    List<Event> findAll();

    // Get the firts event
    List<Event>  findTopByOrderById();

}