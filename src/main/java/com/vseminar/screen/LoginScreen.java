package com.vseminar.screen;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vseminar.data.UserNotFoundException;
import com.vseminar.data.UserSession;

/**
 * Created by enosent on 2017. 7. 13..
 */
public class LoginScreen extends VerticalLayout {

    UserSession userSession;
    Label errorLabel;

    public LoginScreen() {

        userSession = new UserSession();

        setSizeFull();

        Component loginForm = buildForm();

        addStyleName("login-screen");
        addComponent(loginForm);

        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
    }

    private Component buildForm() {
        final VerticalLayout loginPanel = new VerticalLayout();

        loginPanel.addStyleName("login-panel");

        loginPanel.setSizeUndefined();
        loginPanel.setSpacing(true);
        loginPanel.addComponent(buildLabels());
        loginPanel.addComponent(buildFields());

        return loginPanel;
    }

    private Component buildLabels() {
        Label titleLabel = new Label("welcome to vaadin seminar");
        titleLabel.addStyleName(ValoTheme.LABEL_H4);
        titleLabel.addStyleName(ValoTheme.LABEL_COLORED);

        errorLabel = new Label();
        errorLabel.addStyleName(ValoTheme.LABEL_FAILURE);
        errorLabel.setVisible(false);

        final VerticalLayout labels = new VerticalLayout();
        labels.addComponent(titleLabel);
        labels.addComponent(errorLabel);

        return labels;
    }

    private Component buildFields() {
        final TextField email = new TextField("Email");
        email.setIcon(FontAwesome.USER);
        email.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        email.addValidator(new EmailValidator("Invalid e-mail address {0}"));

        final PasswordField password = new PasswordField("Password");
        password.setIcon(FontAwesome.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        final Button signin = new Button("Sign In");
        signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.focus();

        // event
        signin.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        signin.addClickListener( e -> {
            try {

                userSession.signin(email.getValue(), password.getValue());
                // 로그인 처리 후 화면을 리로드하여 다시 VSeminarUI로 접근 처리
                Page.getCurrent().reload();

            } catch (UserNotFoundException ex) {
                System.err.println(ex.getMessage());

                ///Notification.show("SignIn Failed :", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                errorLabel.setValue(String.format("Login Failed: %s", ex.getMessage()));
                errorLabel.setVisible(true);
            }
        });

        HorizontalLayout fields = new HorizontalLayout();
        fields.setSpacing(true); // Component 사이에 간격 추가
        fields.addComponents(email, password, signin);
        fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

        return fields;
    }
}
