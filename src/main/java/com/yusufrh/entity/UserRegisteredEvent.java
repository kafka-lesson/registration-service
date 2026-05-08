package com.yusufrh.entity;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRegisteredEvent {
    public UserRegisteredEvent(UUID userId, int age, String email, String username){
        this.userId = userId;
        this.age = age;
        this.email = email;
        this.username = username;
    }
    
    UUID userId;
    int age;
    String email;
    String username; 
}
