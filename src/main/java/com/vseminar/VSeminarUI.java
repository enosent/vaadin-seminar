package com.vseminar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.*;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;
import com.vseminar.config.VSeminarSessionInitListener;
import com.vseminar.data.LoadingDataGenerator;
import com.vseminar.data.UserSession;
import com.vseminar.screen.LoginScreen;
import com.vseminar.screen.MainScreen;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Title("Vaadin Seminar")
@Theme("vseminar")
@Widgetset("com.vseminar.VSeminarWidgetset")
@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
public class VSeminarUI extends UI {

    private static final LoadingDataGenerator dataGenerator = new LoadingDataGenerator();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // 반응형 웹 설정
        Responsive.makeResponsive(this);

        if(UserSession.isSingedIn()) {
            setContent(new MainScreen(this));

            // 현재 요청된 주소 ( location ) 값에 맞게 뷰를 동적으로 전환9
            getNavigator().navigateTo(getNavigator().getState());
            return;
        }

        setContent(new LoginScreen());
    }

    // productionMode = false : Debug 사용가능 ( http://localhost:8080/vaadin-seminar?debug )
    @WebServlet(urlPatterns = "/*", name = "VSeminarUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = VSeminarUI.class, productionMode = false)
    public static class VSeminarUIServlet extends VaadinServlet {

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().addSessionInitListener(new VSeminarSessionInitListener());
        }
    }
}
