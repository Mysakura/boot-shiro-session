package com.example.demo.controller;

import com.example.demo.common.entity.User;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @program: boot-shiro
 * @description:
 * @author: 001977
 * @create: 2018-07-12 13:02
 */
@RestController
public class SimpleController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("index");
    }

    @RequestMapping("/login")
    public BaseResponse<String> login(@RequestBody User user){
        BaseResponse<String> response = new BaseResponse<>(0,"登陆成功");
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(
                user.getUsername(), user.getPassword());
        subject.login(token);
        response.setData("/home");
        return response;
    }

    @RequestMapping("/register")
    public BaseResponse register(@RequestBody User user){
        userService.addUser(user);
        return new BaseResponse(0,"注册成功");
    }

    @RequestMapping("/home")
    public ModelAndView home(){
        ModelAndView mv = new ModelAndView("home");
        mv.addObject("users", userService.getUsers());
        return mv;
    }
}
