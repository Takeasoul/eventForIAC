package com.events.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "event_members")
public class EventMember {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

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
    private UUID eventId;

    @Column(name = "event_members_role_id")
    private UUID eventMembersRoleId;

}