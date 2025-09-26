package com.ai.role.playing.airoleplaying.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    // 测试接口：返回简单字符串
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }

    // 测试接口：返回JSON数据，显式指定返回JSON格式
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser() {
        // 模拟用户数据
        return new User(1, "张三", 25);
    }

    // 内部静态类：用户实体
    static class User {
        private int id;
        private String name;
        private int age;

        // 构造方法
        public User(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        // 必须添加getter方法（JSON序列化依赖getter）
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        // setter方法可选（如果不需要修改字段可以不写）
        // public void setId(int id) {
        //     this.id = id;
        // }
        //
        // public void setName(String name) {
        //     this.name = name;
        // }
        //
        // public void setAge(int age) {
        //     this.age = age;
        // }
    }
}

