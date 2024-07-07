package com.events.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "event_members")
public class Event_Member {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "middlename")
    private String middlename;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "company")
    private String company;

    @Column(name = "position")
    private String position;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "clubmember")
    private Boolean clubmember;

    @Column(name = "approved")
    private Boolean approved;

    @Column(name = "event_id")
    private Long eventId;
}