package com.example.demo.service;

import com.example.demo.common.entity.User;

import java.util.List;


public interface UserService {

    void addUser(User user);

    User login(User user);

    List<User> getUsers();

}
