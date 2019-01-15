package com.example.demo.Database;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
    // Equivalent to find the last event
    Event findTopByOrderByIdDesc();

    // Equivalent to find the last 50 events
    List<Event> findTop50ByOrderByIdDesc();
}