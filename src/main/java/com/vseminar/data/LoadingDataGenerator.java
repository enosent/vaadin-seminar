package com.vseminar.data;

import com.vaadin.server.FontAwesome;
import com.vseminar.data.model.*;
import com.vseminar.menu.Navi;
import com.vseminar.menu.VSeminarNavigator;
import com.vseminar.view.AboutView;
import com.vseminar.view.DashboardView;
import com.vseminar.view.SessionView;
import com.vseminar.view.UserView;

/**
 * Created by enosent on 2017. 7. 13..
 */
public class LoadingDataGenerator {

    static {
        createUsers();
        createNavis();
        createSessions();
    }

    private static void createUsers() {
        UserData userData = UserData.getInstance();
        userData.save(new User("user1", "user1@vseminar.com", "1234", "img/upload/1.jpg", RoleType.User));
        userData.save(new User("user2", "user2@vseminar.com", "1234", "img/upload/2.jpg", RoleType.User));
        userData.save(new User("user3", "user3@vseminar.com", "1234", "img/upload/3.jpg", RoleType.User));
        userData.save(new User("admin", "admin@vseminar.com", "1234", null, RoleType.Admin));
    }

    private static void createNavis() {
        VSeminarNavigator.naviMaps.put("",
                new Navi(DashboardView.VIEW_NAME, "Dashboard",
                        DashboardView.class, FontAwesome.HOME, RoleType.User));
        VSeminarNavigator.naviMaps.put("session",
                new Navi(SessionView.VIEW_NAME, "Session",
                        SessionView.class, FontAwesome.CUBE, RoleType.User));
        VSeminarNavigator.naviMaps.put("about",
                new Navi(AboutView.VIEW_NAME, "About",
                        AboutView.class, FontAwesome.INFO, RoleType.User));
        VSeminarNavigator.naviMaps.put("user",
                new Navi(UserView.VIEW_NAME, "User",
                        UserView.class, FontAwesome.USERS, RoleType.Admin));
    }

    private static void createSessions() {
        UserData userData = UserData.getInstance();
        Long user1 = userData.findOne(1L).getId();
        Long user2 = userData.findOne(2L).getId();
        Long user3 = userData.findOne(3L).getId();

        SessionData sessionData = SessionData.getInstance();

        String slideUrl = "http://www.slideshare.net/slideshow/embed_code/key/wcZuA4l1M1Fgwv";
        String vaadinUrl = "https://demo.vaadin.com/sampler";

        createQuestions(sessionData.save(new Session("Vaadin Architecture", LevelType.Junior, slideUrl, user1,"speaker_1",  "")), user1);
        createQuestions(sessionData.save(new Session("Vaadin Writing a ...", LevelType.Senior, vaadinUrl, user1,"speaker_1",  "")), user1);

        sessionData.save(new Session("Vaadin User Interface ...", LevelType.Junior, slideUrl, user2,"speaker_2",  ""));
        sessionData.save(new Session("Vaadin Managing ...", LevelType.Senior, vaadinUrl, user2,"speaker_2",  ""));
        sessionData.save(new Session("Vaadin Designer", LevelType.Junior, slideUrl, user3,"speaker_3",  ""));
    }

    private static void createQuestions(Session session, Long userId) {
        QuestionData questionData  = QuestionData.getInstance();
        for(int i = 1; i <= 30; i++) {
            questionData.save(new Question(session.getId(), "test sample question " + i + " : " + session.getTitle(), userId));
        }
    }
}
