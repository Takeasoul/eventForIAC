package com.events.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "summary")
    private String summary;

    @Column(name = "date")
    private Instant date;

    @Column(name = "reg_open")
    private Boolean regOpen;

    @Column(name = "address")
    private String address;

    @Column(name = "start_registration_date")
    private Instant startRegistrationDate;

    @Column(name = "close_registration_date")
    private Instant closeRegistrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    @ToString.Exclude
    private User organizer;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<EventMember> eventMembers = new ArrayList<>();
}