package com.example.redis_demo.Bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 342249183334628061L;
    private String name;
    private String id;
    private String phone;

}
