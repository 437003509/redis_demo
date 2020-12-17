package com.example.redis_demo.Controller;

import com.example.redis_demo.Bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    StringRedisTemplate stringRedisTemplate;//仅支持字符串的数据
    @Autowired
    RedisTemplate redisTemplate;//支持对象的数据,前提需要进行序列化

    @Cacheable(value = "user")
    @GetMapping
    public User user(){
        User user = new User();
        user.setId("1");
        user.setName("zhangshan");
        user.setPhone("133333333");

//        stringRedisTemplate.opsForValue().set("1",user.toString());
//        redisTemplate.opsForValue().set("user",user);


//        return stringRedisTemplate.opsForValue().get("1");
//        return (User)redisTemplate.opsForValue().get("user");
         return user;
    }


}
