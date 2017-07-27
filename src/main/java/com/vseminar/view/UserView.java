package com.vseminar.view;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vseminar.data.UserData;
import com.vseminar.data.UserSession;
import com.vseminar.data.model.User;

import java.util.List;

/**
 * Created by enosent on 2017. 7. 14..
 */
public class UserView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "user";

    UserData userData;
    BeanItemContainer<User> container;
    UserForm userForm;

    public UserView() {
        userData = UserData.getInstance();

        setHeight(100, Unit.PERCENTAGE);

        Table table = createTable();
        createForm();
        findBean();

        addComponent(createTopBar());
        addComponent(table);
        setExpandRatio(table,1); // 화면에서 테이블 영역이 나머지 영역 모두 차지
    }

    private void createForm() {
        userForm = new UserForm();
        userForm.setSaveHandler(entity -> {
            userForm.closePopup();

            if(UserSession.getUser().getId() == entity.getId()) {
                Page.getCurrent().reload(); // 로그인한 사용자면 메뉴의 사용자명 갱신을 위해 화면 리로드
            }

            findBean(); // 변경된 데이터를 동적으로 갱신
        });

        userForm.setDeleteHandler(entity -> {
            userForm.closePopup();
            findBean(); // 변경된 데이터를 동적으로 갱신
        });
    }

    private Table createTable() {
        Table table = new Table();
        table.setSizeFull();

        container = new BeanItemContainer<>(User.class);
        table.setContainerDataSource(container);

        table.addGeneratedColumn("picture", (source, itemId, columnId) -> {
            User user = (User)itemId;
            Resource image = new ThemeResource(user.getImgPath());

            return new Image(null, image);
        });

        table.setCellStyleGenerator((source, itemId, propertyId) -> {
            if(propertyId != null) {
                if(propertyId.equals("picture")) {
                    return "v-round-image";
                }
            }

            return null;
        });

        table.setVisibleColumns("picture", "name", "email", "password", "role");
        table.setColumnHeaders("IMG", "Name", "Email", "Password", "Role");

        table.setColumnWidth("picture", 60);

        table.setSelectable(true);

        table.addItemClickListener(event -> {
            userForm.lazyInit((User)event.getItemId());
            userForm.openPopup("Edit Profile");
        });

        return table;
    }

    // Table Component 데이터 조작
    private void findBean() {
        List<User> users = userData.findAll();

        if(users.size() > 0) {
            container.removeAllItems();
        }

        // public class Table extends AbstractSelect implements Action.Container ...
        // Container 데이터가 Table 데이터이며, Container 데이터의 변경은 Table 데이터 변경을 의미
        container.addAll(users);
    }

    public HorizontalLayout createTopBar() {
        Label title = new Label("User");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        //
        Button newBtn = new Button("New");
        newBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newBtn.setIcon(FontAwesome.PLUS_CIRCLE);
        newBtn.addClickListener(event -> {
            userForm.lazyInit(new User());
            userForm.openPopup("New User");
        });

        HorizontalLayout topLayout = new HorizontalLayout();

        topLayout.addStyleName("top-bar");
        topLayout.setSpacing(true);
        topLayout.setWidth(100, Unit.PERCENTAGE);
        topLayout.addComponent(title);
        topLayout.addComponent(newBtn);
        topLayout.setComponentAlignment(title, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(title, 1); // 1로 잡으면 오른쪽으로 밀려 정렬

        return topLayout;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

}
