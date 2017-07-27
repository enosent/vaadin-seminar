package com.vseminar.screen;

//import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vseminar.VSeminarUI;
import com.vseminar.menu.VSeminarMenu;
import com.vseminar.menu.VSeminarNavigator;
//import com.vseminar.data.UserSession;
//import com.vseminar.view.*;

/**
 * Created by enosent on 2017. 7. 13..
 */
public class MainScreen extends HorizontalLayout {

    public MainScreen(VSeminarUI vseminarUI) {
        CssLayout viewArea = new CssLayout();
        viewArea.setSizeFull();

        final VSeminarNavigator navigator = new VSeminarNavigator(vseminarUI.getCurrent(), viewArea);
        //
        final VSeminarMenu menuArea = new VSeminarMenu(navigator);

        ViewChangeListener viewChangeListener = new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                menuArea.setSelectedItem(event.getViewName());
            }
        };

        navigator.addViewChangeListener(viewChangeListener);

        // 반응형 웹 적용
        Responsive.makeResponsive(this);
        // 반응형 메뉴 스타일
        addStyleName(ValoTheme.UI_WITH_MENU);

        // 메뉴 영역, 동적 변경 뷰 영역 순서대로 추가
        addComponents(menuArea, viewArea);
        setExpandRatio(viewArea, 1);
        setSizeFull();
    }

    /*
    public MainScreen(UI ui) {
        Label label = new Label(UserSession.getUser().getEmail());

        final Button signout = new Button("Sign Out");
        signout.addClickListener( e -> {
            UserSession.signout();
        });

        addComponents(label);
        addComponents(signout);

        //
        CssLayout viewContainer = new CssLayout();

        final Navigator navigator = new Navigator(ui, viewContainer);

        // Vaadin URL 규칙 : contextPath/{UI:Resource}/#!{View:Fragement}

        // addView("주소", 구현 View Class)
        navigator.addView(DashboardView.VIEW_NAME, new DashboardView());
        navigator.addView(SessionView.VIEW_NAME, new SessionView());
        navigator.addView(AboutView.VIEW_NAME, new AboutView());
        navigator.addView(UserView.VIEW_NAME, new UserView());

        addComponent(viewContainer);

        navigator.setErrorView(ErrorView.class);

        // 주소에 맞게 View를 동적으로 교체
        navigator.navigateTo(UI.getCurrent().getNavigator().getState());
    }
    */
}
