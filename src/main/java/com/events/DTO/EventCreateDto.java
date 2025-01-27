package com.events.DTO;

import com.events.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EventCreateDto {
    private UUID ogr_id;
    private String event_name;
    private String event_summary;
    private Date event_date;
    private Boolean reg_open;
    private String address;
}