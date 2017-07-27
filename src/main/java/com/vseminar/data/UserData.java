package com.vseminar.data;

import com.vseminar.data.model.User;
import com.vseminar.data.model.VSeminarData;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by enosent on 2017. 7. 13..
 */
public class UserData implements VSeminarData<User> {

    private static volatile UserData INSTANCE = null;
    private Map<Long, User> users;
    private AtomicLong nextId;

    private UserData() {
        nextId = new AtomicLong();
        users = new LinkedHashMap<>();
    }

    public synchronized static UserData getInstance() {
        if(INSTANCE == null) {
            synchronized (UserData.class) {
                if(INSTANCE == null) {
                    INSTANCE = new UserData();
                }
            }
        }

        return INSTANCE;
    }

    @Override
    public synchronized User findOne(long id) {
        User user = users.get(id);
        if(user != null) return user;
        return new User();
    }

    @Override
    public synchronized List<User> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(users.values()));
    }

    @Override
    public int count() {
        return users.size();
    }

    @Override
    public synchronized User save(User user) {
        User checkUser;

        if(user.getId() == null) {
            checkUser = findByNameOrEmail(user.getName(), user.getEmail());
            if(checkUser.getId() != null) {
                throw new IllegalArgumentException("Duplicated user name or email");
            }

            user.setId(nextId.incrementAndGet());
            users.put(user.getId(), user);

            return user;
        }

        checkUser = findByName(user.getName());

        if(users.containsKey(user.getId())) {
            if(user.getId() != checkUser.getId() && user.getName().equals(checkUser.getName())) {
                throw new IllegalArgumentException("Duplicated user name");
            }

            users.put(user.getId(), user);
            return user;
        }

        throw new IllegalArgumentException("No user with id " + user.getId() + " found");
    }

    @Override
    public synchronized void delete(long id) {
        User user = findOne(id);

        if(user == null) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }

        users.remove(user.getId());
    }

    public synchronized User findByName(String name) {
        List<User> users = findAll();
        return users.stream().filter(user -> user.getName().equals(name)).findAny().orElse(new User());
    }

    public synchronized User findByNameOrEmail(String name, String email) {
        List<User> users = findAll();
        return users.stream().filter(user -> (user.getName().equals(name) || user.getEmail().equals(email))).findAny().orElse(new User());
    }

    public synchronized User findByEmailAndPassword(String email, String password) {
        List<User> users = findAll();
        return users.stream().filter(user -> (user.getEmail().equals(email) && user.getPassword().equals(password))).findAny().orElse(new User());
    }
}
