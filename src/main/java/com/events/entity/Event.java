package com.events.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "events")
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    int event_id;

    @Column(name = "event_name")
    private String event_name;

    @Column(name = "event_summary")
    private String event_summary;

    @Column(name = "event_date")
    private Date event_date;

    @Column(name = "reg_open")
    private Boolean reg_open;


}
