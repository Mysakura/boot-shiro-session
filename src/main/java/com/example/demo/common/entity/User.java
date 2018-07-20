package com.example.demo.common.entity;

import java.io.Serializable;

/**
 * @program: boot-shiro
 * @description:
 * @author: 001977
 * @create: 2018-07-17 19:48
 */
public class User implements Serializable{

    private String username;
    private String password;
    private String show;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", show='" + show + '\'' +
                '}';
    }
}
