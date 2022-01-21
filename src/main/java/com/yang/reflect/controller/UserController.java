package com.yang.reflect.controller;


import com.yang.reflect.anno.RequestMapping;
import com.yang.reflect.controller.dto.JoinDto;
import com.yang.reflect.controller.dto.LoginDto;

public class UserController {


    @RequestMapping("/user/join")
    public String join(JoinDto dto) { // username, password, email
        System.out.println("join 함수 호출");
        System.out.println(dto);
        return "/";
    }

    @RequestMapping("/user/login")
    public String login(LoginDto dto) { // username, password
        System.out.println("login 함수 호출");
        System.out.println(dto);
        return "/";
    }

    @RequestMapping("/user")
    public String user() {
        System.out.println("user 함수 호출");
        return "/";
    }

    @RequestMapping("/hello")
    public String hello() {
        System.out.println("hello 함수 호출");
        return "/";
    }
}
