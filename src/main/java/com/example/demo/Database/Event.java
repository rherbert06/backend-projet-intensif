package com.example.demo.Database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

@Entity
public class Event {
    @Id
    @SequenceGenerator(name = "mySeqGen", sequenceName = "mySeq", initialValue = 5, allocationSize = 1)
    @GeneratedValue
    private Long id;
    private int value1;
    private int value2;
    private int value3;
    private Date date;

    public Event() {
        super();
    }

    public int getValue1() {
        return value1;
    }

    public int getValue2() {
        return value2;
    }

    public int getValue3() {
        return value3;
    }

    public Date getDate() {
        return date;
    }

    public Event(int value1, int value2, int value3) {
        super();
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.date = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return  "{\"values\" : \n" +
                " \t{\n" +
                "      \"value1\" : \"" + value1 + "\",\n" +
                "      \"value2\" : \"" + value2 + "\",\n" +
                "      \"value3\" : \"" + value3 + "\"\n" +
                "    }\n" +
                "}";
    }
}
