package com.vseminar.menu;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vseminar.data.UserSession;
import com.vseminar.data.model.RoleType;
import com.vseminar.view.DashboardView;
import com.vseminar.view.UserForm;

/**
 * Created by enosent on 2017. 7. 14..
 */
public class VSeminarMenu extends CssLayout {

    private static final String VALO_MENU_VISIBLE = "valo-menu-visible";
    private static final String VALO_MENU_TOGGLE  = "valo-menu-toggle";
    private static final String VALO_MENUITEMS    = "valo-menuitems";

    private CssLayout menuPart;
    private CssLayout menuItems;

    private RoleType sectionType = null;

    public VSeminarMenu(final VSeminarNavigator navigator) {
        setPrimaryStyleName(ValoTheme.MENU_ROOT);

        menuPart = new CssLayout();
        menuPart.addStyleName(ValoTheme.MENU_PART);

        addComponent(menuPart);

        //
        final Label title = new Label("<h3>Vaadin <strong>Seminar</strong></h3>", ContentMode.HTML);
        title.setSizeUndefined();

        final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        top.setSpacing(true);
        top.addComponent(title);

        menuPart.addComponent(top);

        //
        final MenuBar userMenu = new MenuBar();
        userMenu.addStyleName("user-menu");
        menuPart.addComponent(userMenu);

        Resource image = new ThemeResource(UserSession.getUser().getImgPath());
        final MenuBar.MenuItem userMenuItem = userMenu.addItem(UserSession.getUser().getName(), image, null);

        userMenuItem.addItem("Edit Profile", ((selectedItem -> {
            final UserForm userForm = new UserForm();
            userForm.lazyInit(UserSession.getUser());
            userForm.openPopup("Edit Profile");

            userForm.setSaveHandler(user -> {
                userForm.closePopup();
                Page.getCurrent().reload();
            });

        })));
        userMenuItem.addItem("Sign Out", ((selectedItem -> UserSession.signout())));

        //
        final Button showMenu = new Button("Menu", event -> {
           if(menuPart.getStyleName().contains(VALO_MENU_VISIBLE)) {
               menuPart.removeStyleName(VALO_MENU_VISIBLE);
           } else {
               menuPart.addStyleName(VALO_MENU_VISIBLE);
           }
        });

        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(FontAwesome.NAVICON);

        menuPart.addComponent(showMenu);

        //
        menuItems = new CssLayout();
        menuItems.setPrimaryStyleName(VALO_MENUITEMS);

        navigator.getActiveNaviMaps().forEach((key, item) ->{
            final String fragment = item.getFragment();	 // vaadin-seminar/#!{fragment} 주소
            final String viewName = item.getViewName();	 // 메뉴명
            final Class<? extends View> viewClass = item.getViewClass(); // View 클래스
            final Resource icon = item.getIcon(); // 메뉴명 아이콘
            final RoleType roleType = item.getRoleType(); // 접근권한(USER, ADMIN)

            if(viewClass!=DashboardView.class && sectionType!=roleType) {
                sectionType = roleType;
                Label label = new Label("Role_" + sectionType.name(), ContentMode.HTML);
                label.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
                label.addStyleName("h4");
                label.setSizeUndefined();
                // 메뉴 아이템 그룹(USER, ADMIN)별 언더라인스타일 추가
                menuItems.addComponent(label);
            }

            // 네비버튼
            final Button naviBtn = new Button(viewName, (event ->  navigator.navigateTo(fragment)));

            // 네비버튼에 fragment값 넣기두기
            naviBtn.setData(fragment);

            // 네비버튼스타일추가
            naviBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
            naviBtn.setIcon(icon);

            // 네비버튼을 메뉴아이템에 추가
            menuItems.addComponent(naviBtn);
        });

        menuPart.addComponent(menuItems);

        addComponent(menuPart);
    }

    public void setSelectedItem(String viewName) {
        if(menuItems.getComponentCount() <= 0) {
            return;
        }

        menuItems.forEach(item -> {
            if(item instanceof Button) {
                final Button naviBtn = (Button)item;

                naviBtn.removeStyleName("selected");
                String fragment= (String)naviBtn.getData();

                if(fragment.equals(viewName)) {
                    item.addStyleName("selected");
                }
            }
        });

        menuPart.removeStyleName(VALO_MENU_VISIBLE);
    }
}
