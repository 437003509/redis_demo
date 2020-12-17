package com.example.redis_demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController {

    @GetMapping("test")
    public String test(){
        return "hello docker";
    }

}
