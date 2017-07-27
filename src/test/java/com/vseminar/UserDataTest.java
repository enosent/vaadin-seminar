package com.vseminar;

import com.vseminar.data.LoadingDataGenerator;
import com.vseminar.data.UserData;
import com.vseminar.data.model.User;

/**
 * Created by enosent on 2017. 7. 13..
 */
public class UserDataTest {

    private static final LoadingDataGenerator dataGenerator = new LoadingDataGenerator();

    public static void main(String[] args) {
        UserData instance = UserData.getInstance();

        instance.findAll().forEach( System.out::println );

        User success = instance.findByName("user1");
        System.out.println( success.getEmail() );

        User fail = instance.findByName("user4");
        System.out.println( fail.getEmail() );

        User signin = instance.findByEmailAndPassword("user1@vseminar.com", "1234");
        System.out.println( signin.getEmail() + " : " + signin.getName());

    }

}