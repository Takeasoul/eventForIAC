package com.events.DTO;

import com.events.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;


@AllArgsConstructor
@Data
public class EventRegistrationDto {
    private int id;
    private String firstname;
    private String middlename;
    private String lastname;
    private String company;
    private String position;
    private String email;
    private String phone;
    private Boolean clubmember;
    private int event_id;
}
