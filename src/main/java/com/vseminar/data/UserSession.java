package com.vseminar.data;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vseminar.data.model.User;

import java.io.Serializable;

/**
 * Created by enosent on 2017. 7. 13..
 */
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SESSION_KEY = UserSession.class.getCanonicalName();

    private UserData userData;

    public UserSession() {
        this.userData = UserData.getInstance();
    }

    public static User getUser() {
        User user = (User) getCurrentSession().getAttribute(SESSION_KEY);
        return user;
    }

    public static void setUser(User user) {
        if(user == null) {
            getCurrentSession().removeAttribute(SESSION_KEY);
        } else {
            getCurrentSession().setAttribute(SESSION_KEY, user);
        }
    }

    public static boolean isSingedIn() {
        return getUser() != null;
    }

    public void signin(String email, String password) {
        User user = userData.findByEmailAndPassword(email, password);
        if(user.getId() == null) {
            throw new UserNotFoundException("user not found");
        }

        setUser(user);
    }

    public static void signout() {
        getCurrentSession().invalidate();
        Page.getCurrent().reload(); // 현재 페이지 리로딩
    }

    // 바딘의 세션 객체
    private static WrappedSession getCurrentSession() {
        VaadinRequest request = VaadinService.getCurrentRequest();

        if(request == null) {
            throw new IllegalStateException("No request bound to current thread");
        }

        WrappedSession session = request.getWrappedSession();

        if(session == null) {
            throw new IllegalStateException("No Session bound to current thread");
        }

        return session;
    }

}
