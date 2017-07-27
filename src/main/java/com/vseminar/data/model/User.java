package com.vseminar.data.model;

/**
 * Created by enosent on 2017. 7. 13..
 */
public class User {

    private static final String DEFAULT_PROFILE_PATH = "img/profile-pic-300px.jpg";

    private Long id;
    private String name;
    private String email;
    private String password;
    private String imgPath;
    private RoleType role;

    public User() {
        this.role = RoleType.User;
    }

    public User(User other) {
        this.id = other.getId();
        this.name = other.getName();
        this.email = other.getEmail();
        this.password = other.getPassword();
        this.imgPath = other.getImgPath();
        this.role = other.getRole();
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.role = RoleType.User;
    }

    public User(String name, String email, String password, String imgPath, RoleType role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.imgPath = imgPath;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgPath() {
        return (imgPath == null)?DEFAULT_PROFILE_PATH:imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }
}
