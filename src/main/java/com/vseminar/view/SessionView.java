package com.vseminar.view;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vseminar.data.SessionData;
import com.vseminar.data.UserSession;
import com.vseminar.data.model.RoleType;
import com.vseminar.data.model.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enosent on 2017. 7. 14..
 */
public class SessionView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "session";

    Grid grid;
    BeanItemContainer<Session> container;
    SessionData sessionData;

    Button newBtn;
    Button delBtn;

    public SessionView() {
        sessionData = SessionData.getInstance();

        setHeight(100, Unit.PERCENTAGE);

        grid = createGrid();
        findBean();

        addComponent(createTopBar());
        addComponent(grid);
        setExpandRatio(grid, 1);
    }

    private void findBean() {
        List<Session> sessions = new ArrayList<>();

        if(UserSession.getUser().getRole() == RoleType.Admin) {
            sessions.addAll(sessionData.findAll());
        } else {
            sessions.addAll(sessionData.findByOwner(UserSession.getUser()));
        }

        if(sessions.size() <= 0) return;

        container.removeAllItems();
        container.addAll(sessions);

        grid.sort(Sort.by("startDate", SortDirection.ASCENDING));
    }

    private Grid createGrid() {
        final Grid grid = new Grid();
        grid.setSizeFull();
        grid.setEditorEnabled(true); // 편집모드 활성화

        container = new BeanItemContainer<>(Session.class, null);
        grid.setContainerDataSource(container);

        grid.setColumnOrder("id", "title", "level", "startDate", "endDate", "embeddedUrl", "speaker", "description", "ownerId");
        grid.getColumn("id").setHeaderCaption("ID")
                .setHidden(true)
                .setEditable(false);
        grid.getColumn("title").setHeaderCaption("Title")
                .setEditorField(textEditorField());
        grid.getColumn("level").setHeaderCaption("Level");
        grid.getColumn("startDate").setHeaderCaption("StartDate")
                .setEditorField(dateEditorField());
        grid.getColumn("endDate").setHeaderCaption("EndDate")
                .setEditorField(dateEditorField());
        grid.getColumn("speaker").setHeaderCaption("Speaker")
                .setEditorField(textEditorField());
        grid.getColumn("embeddedUrl").setHeaderCaption("Presentation")
                .setEditorField(textEditorField())
                .setMaximumWidth(200);
        grid.getColumn("description").setHeaderCaption("Description")
                .setEditorField(textEditorField())
                .setMaximumWidth(200);
        grid.getColumn("ownerId").setHeaderCaption("Owner")
                .setHidden(true)
                .setEditable(false);

        grid.getColumn("title").getEditorField()
                .addValidator(new StringLengthValidator("The name must be 1-50 letters (was {0})", 1, 50, true));

        grid.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                Session session = (Session) grid.getEditedItemId();
                sessionData.save(session);
            }
        });

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addSelectionListener(event -> {
            delBtn.setEnabled(grid.getSelectedRows().size() > 0);
        });

        grid.getColumn("questions").setHidden(true).setEditable(false);

        return grid;
    }

    public HorizontalLayout createTopBar() {
        Label title = new Label("Session");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        newBtn = new Button("New");
        newBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newBtn.setIcon(FontAwesome.PLUS_CIRCLE);

        newBtn.addClickListener(event -> {
            container.addItemAt(0, new Session(UserSession.getUser().getId()));
            grid.scrollToStart();
        });

        delBtn = new Button("Delete");
        delBtn.addStyleName(ValoTheme.BUTTON_DANGER);
        delBtn.setIcon(FontAwesome.MINUS_CIRCLE);
        delBtn.setEnabled(false);
        delBtn.addClickListener(event -> {
            Grid.MultiSelectionModel selection = (Grid.MultiSelectionModel) grid.getSelectionModel();

            for(Object itemId : selection.getSelectedRows()) {
                Session session = (Session) itemId;
                if(session.getId() != null) {
                    sessionData.delete(session.getId());
                }
                grid.getContainerDataSource().removeItem(session);
            }

            grid.getSelectionModel().reset();
            delBtn.setEnabled(false);
        });

        HorizontalLayout topLayout = new HorizontalLayout();

        topLayout.addStyleName("top-bar"); // title과 table간의 여백 추가
        topLayout.setSpacing(true);
        topLayout.setWidth(100, Unit.PERCENTAGE);
        topLayout.addComponent(title);
        topLayout.addComponent(newBtn);
        topLayout.addComponent(delBtn);
        topLayout.setComponentAlignment(title, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(title, 1); // component 어떤것을 대상으로 설정하냐 따라 화면이 달라짐

        return topLayout;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    private DateField dateEditorField() {
        DateField dateField = new DateField();
        dateField.setResolution(Resolution.MINUTE); // 달력 + 시:분
        return dateField;
    }

    private TextField textEditorField() {
        TextField textField = new TextField();
        textField.setNullRepresentation(""); // null 대신 empty
        return textField;
    }

}
