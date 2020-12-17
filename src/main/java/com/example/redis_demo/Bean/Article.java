package com.example.redis_demo.Bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Article implements Serializable {

    private static final long serialVersionUID = 5479628953999467774L;
    private String id;
    private String title;
    private String link;
    private String poster;
    private String time;
    private String votes;

    public Article(String title, String link, String poster) {
        this.title=title;
        this.link=link;
        this.poster=poster;
    }
}
