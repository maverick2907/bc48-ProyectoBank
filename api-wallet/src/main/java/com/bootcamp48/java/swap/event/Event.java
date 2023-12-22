package com.bootcamp48.java.swap.event;

import lombok.Data;

@Data
public class Event <T> {
    private String id;
    private EventType type;
    private T data;
}
