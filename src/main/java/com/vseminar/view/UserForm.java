package com.vseminar.view;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vseminar.data.UserData;
import com.vseminar.data.UserSession;
import com.vseminar.data.model.RoleType;
import com.vseminar.data.model.User;
import com.vseminar.image.ImageUploader;

public class UserForm extends AbstractForm<User> {

    // ...
    TextField name;
    TextField email;
    PasswordField password;
    ComboBox role;

    // ...
    Button save;
    Button delete;

    BeanFieldGroup<User> fieldGroup;
    UserData userData;

    public UserForm() {
        userData = UserData.getInstance();
        fieldGroup = new BeanFieldGroup<User>(User.class);

        VerticalLayout root = new VerticalLayout();
        root.addComponent(createContent());
        root.addComponent(createFooter());

        setCompositionRoot(root);
    }

    @Override
    protected void save(Button.ClickEvent e) {
        try {
            fieldGroup.commit(); // 변경된 필드의 item property(s)의 value 변경

            User item = fieldGroup.getItemDataSource().getBean(); // 변경된 item 조회
            User entity = userData.save(item);

            if(UserSession.getUser().getId() == entity.getId()) {
                UserSession.setUser(entity); // 로그인한 사용자 정보면 Session 갱신
            }

            getSaveHandler().onSave(entity);

        } catch (FieldGroup.CommitException | IllegalArgumentException ex) {
            Notification.show("Error while updating profile", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    protected void delete(Button.ClickEvent e) {
        try {
            User item = fieldGroup.getItemDataSource().getBean();
            userData.delete(item.getId());

            getDeleteHandler().onDelete(item);
        }catch (Exception ex) {
            Notification.show("Error while deleting porfile", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    public void lazyInit(User user) {
        User item = new User(user);

        fieldGroup.bindMemberFields(this); // 개별적 bind 처리를 한번에 적용
        fieldGroup.setItemDataSource(new BeanItem<>(item)); // field Group에 데이터 그룹화

        // null -> ""
        name.setNullRepresentation("");
        email.setNullRepresentation("");
        password.setNullRepresentation("");
        role.setNullSelectionItemId(RoleType.User);

        // fieldGroup commit 시 validation 처리
        name.addValidator(new NullValidator("required name", false));
        email.addValidator(new EmailValidator("Invalid e-mail address {0}"));
        password.addValidator(new NullValidator("required password", false));

        // 수정 금지 처리
        email.setEnabled(item.getId() == null);
        role.setEnabled(UserSession.getUser().getRole() == RoleType.Admin);

        // 로그인한 사용자 또는 신규 유저 생성시에는 삭제 버튼 감추기
        delete.setVisible(item.getId() != UserSession.getUser().getId() && item.getId() != null);

        image.setSource(new ThemeResource(item.getImgPath()));
    }

    private Component createContent() {
        // name, email, password, role field

        HorizontalLayout content = new HorizontalLayout();
        content.setSpacing(true);
        content.setMargin(new MarginInfo(true, true, false, true));

        FormLayout form = new FormLayout();
        form.setSizeUndefined();
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        form.addComponent(name = new TextField("Name"));
        form.addComponent(email = new TextField("Email"));
        form.addComponent(password = new PasswordField("Password"));
        form.addComponent(role = new ComboBox("Role"));

        for(RoleType type : RoleType.values()) {
            role.addItem(type);
        }

        content.addComponent(createUpload());
        content.addComponent(form);

        return content;
    }

    Image image;

    private Component createUpload() {

        image = new Image();
        image.setWidth(100, Unit.PIXELS);
        image.setSource(new ThemeResource("img/profile-pic-300px.jpg"));
        final Upload upload = new Upload();
        upload.setButtonCaption("Change...");
        upload.addStyleName(ValoTheme.BUTTON_TINY);
        upload.setImmediate(true);

        VerticalLayout imageLayout = new VerticalLayout();
        imageLayout.setSpacing(true);
        imageLayout.setSizeUndefined();
        imageLayout.addComponent(image);
        imageLayout.addComponent(upload);

        final ImageUploader imageUploader = new ImageUploader();
        upload.setReceiver(imageUploader);

        upload.addProgressListener((readBytes, contentLength) -> {
            int maxLength = 1024 * 500;

            if(readBytes > maxLength) {
                upload.interruptUpload();
                new Notification("Could not upload file", "file max size: 500kb", Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
            }
        });

        upload.addSucceededListener(event -> {
            if(imageUploader.getSuccessUploadFile() == null) return;

            image.setSource(new ThemeResource(imageUploader.getImgPath()));
            fieldGroup.getItemDataSource().getBean().setImgPath(imageUploader.getImgPath());
        });

        return imageLayout;
    }

    private Component createFooter() {
        // save, delete button
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeUndefined();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100, Sizeable.Unit.PERCENTAGE);

        save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(event -> save(event));

        delete = new Button("Delete");
        delete.addStyleName(ValoTheme.BUTTON_DANGER);
        delete.addClickListener(event -> delete(event));

        footer.addComponents(save, delete);
        footer.setExpandRatio(save, 1);
        footer.setComponentAlignment(save, Alignment.MIDDLE_RIGHT);

        return footer;
    }

}
