package com.events.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "events")
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id")
    private UUID event_id;

    @Column(name = "event_name")
    private String event_name;

    @Column(name = "event_summary")
    private String event_summary;

    @Column(name = "event_date")
    private Date event_date;

    @Column(name = "reg_open")
    private Boolean reg_open;

    @Column(name = "orgid")
    private UUID orgid;

    @Column(name = "address")
    private String address;


}
