package com.vseminar.view;

import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vseminar.data.QuestionData;
import com.vseminar.data.SessionData;
import com.vseminar.data.UserData;
import com.vseminar.data.UserSession;
import com.vseminar.data.model.LevelType;
import com.vseminar.data.model.Question;
import com.vseminar.data.model.Session;
import com.vseminar.data.model.User;
import com.vseminar.image.ImageUploader;
import com.vseminar.push.MessageEventBus;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by enosent on 2017. 7. 14..
 */
public class DashboardView extends VerticalLayout implements View, MessageEventBus.EventBusListener {

    public static final String VIEW_NAME = "";

    SessionData sessionData;
    QuestionData questionData;
    UserData userData;

    CssLayout sessionLayout;
    CssLayout questionLayout;

    private static final MessageEventBus eventBus = new MessageEventBus();

    AtomicBoolean hasNewItem;

    public DashboardView() {
        sessionData = SessionData.getInstance();
        questionData = QuestionData.getInstance();
        userData = UserData.getInstance();

        addStyleName("dashboard-view");
        setHeight(100, Unit.PERCENTAGE);

        Component createContent = createContent();

        findSessionBean(); // SessionTable에 데이터를 채워줌

        addComponent(createTopBar());
        addComponent(createContent); // content tabs 추가
        setExpandRatio(createContent, 1);

        hasNewItem = new AtomicBoolean(); // 신규 메시지 도착 여부
        new RefreshThread().start(); // 새로 고침 스레드 시작
    }

    public HorizontalLayout createTopBar() {
        Label title = new Label("Dashboard");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        /*
        OptionGroup options = new OptionGroup();
        options.addItems("Session", "Question"); // 옵션 아이템
        options.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        options.setVisible(false);

        options.addValueChangeListener(event -> {
            String optionValue = (String) event.getProperty().getValue();
            sessionLayout.setVisible(optionValue.equals("Session"));
            questionLayout.setVisible(optionValue.equals("Question"));
        });

        // 모바일 폰인 경우만 활성화 처리
        if(isPhone()) {
            options.setVisible(true);
            options.select("Session");
        }
        */

        OnOffSwitch onoffSwitch = new OnOffSwitch();
        onoffSwitch.setCaption("Question");
        onoffSwitch.setVisible(false);

        onoffSwitch.addValueChangeListener(event -> {
            boolean checked = (boolean) event.getProperty().getValue();
            // On(true)   : sesseion(x), question(o)
            // Off(false) : sesseion(o), question(x)
            sessionLayout.setVisible(!checked);
            questionLayout.setVisible(checked);
        });

        // 모바일 폰인 경우만 활성화 처리
        if(isPhone()) {
            onoffSwitch.setVisible(true);
        }

        HorizontalLayout topLayout = new HorizontalLayout();

        topLayout.setSpacing(true);
        topLayout.setWidth(100, Unit.PERCENTAGE);
        topLayout.addComponents(title, onoffSwitch);
        topLayout.setComponentAlignment(title, Alignment.MIDDLE_LEFT);
        topLayout.addStyleName("top-bar");
        topLayout.setExpandRatio(title, 1);

        return topLayout;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // 뷰에 접근할 때 마다 호출
    }

    private Component createContent(){
        // (1) contentLayout = (2) sessionLayout + (3) questionLayout
        // HorizontalLayout contentLayout = new HorizontalLayout();
        // contentLayout.setSizeFull();
        // contentLayout.setSpacing(true);

        // 반응형 웹 구현 ( 모바일 사이즈에서는 세로로 배치가 되도록 레이아웃 변경
        CssLayout contentLayout = new CssLayout();
        // Flexible Wrapping 방식의 Responsive.makeResponsive 처리
        Responsive.makeResponsive(contentLayout);
        contentLayout.addStyleName("view-content");
        contentLayout.setSizeFull();

        // (2) sessionLayout ( TabSheet )
        // VerticalLayout sessionLayout = new VerticalLayout();
        sessionLayout = new CssLayout();
        // Responsive-Web 영역 CSS 추가
        sessionLayout.addStyleName("view-content-panel");
        sessionLayout.setSizeFull();
        sessionLayout.addComponent(createSessionTab());

        // (3) questionLayout
        // VerticalLayout questionLayout = new VerticalLayout();
        questionLayout = new CssLayout();
        // Responsive-Web 영역 CSS 추가
        questionLayout.addStyleName("view-content-panel");
        questionLayout.setSizeFull();
        questionLayout.addComponent(createQuestionTab());

        contentLayout.addComponent(sessionLayout);
        contentLayout.addComponent(questionLayout);
        return contentLayout;
    }

    Table sessionTable;

    private Component createSessionTab() {
        // (2-1) sessionTabSheet = sessionTable + browserFrame
        TabSheet sessionTabSheet = new TabSheet();
        sessionTabSheet.setSizeFull();

        sessionTable = createSessionTable();
        sessionTabSheet.addTab(sessionTable, "Session", FontAwesome.CUBES);

        // BrowserFrame - iframe에 element 기능으로 external URL 연결
        final BrowserFrame browserFrame = new BrowserFrame();
        browserFrame.setSizeFull();

        sessionTabSheet.addTab(browserFrame, "Presentation", FontAwesome.EXTERNAL_LINK);

        sessionTabSheet.addSelectedTabChangeListener(event -> {
            Session session = (Session) sessionTable.getNullSelectionItemId();
            browserFrame.setSource(new ExternalResource(session.getEmbeddedUrl()));
        });

        return sessionTabSheet;
    }

    private Table createSessionTable() {
        Table table = new Table();
        table.setSizeFull();
        table.setSelectable(true);
        table.setContainerDataSource(new BeanItemContainer<>(Session.class));

        // Session Item이 클릭되면 해당 Questions으로 QuestionTable 데이터 변경
        table.addItemClickListener(event -> clickSession((Session) event.getItemId()));

        table.addGeneratedColumn("level", (source, itemId, columnId) -> {
            Session session = (Session) itemId;

            String color = session.getLevel() == LevelType.Junior ? "#2dd085":"#f54993";
            String iconTag = "<span class=\"v-icon\" style=\"font-family: "
                    + FontAwesome.CIRCLE.getFontFamily() + ";color:" + color
                    + "\">&#x"
                    + Integer.toHexString(FontAwesome.CIRCLE.getCodepoint())
                    + ";</span>";

            String html = iconTag + " " + session.getLevel().name();

            Label label = new Label(html, ContentMode.HTML);
            label.setSizeUndefined();
            return label;
        });

        // visibleColumns : title, level, speaker
        table.setVisibleColumns("title", "level", "speaker");
        // ColumnHeaders : Session, Level, Speaker
        table.setColumnHeaders("Session", "Level", "Speaker");
        // ColumnWidth : title의 폭의 비중을 가장 크게 설정
        table.setColumnExpandRatio("title", 1);

        return table;
    }

    Table questionTable;
    TextArea textArea;
    Button button;

    private Component createQuestionTab() {
        // (3-1) questionTabSheet = tabLayout
        TabSheet questionTabSheet = new TabSheet();
        questionTabSheet.setSizeFull();

        // (4) tabLayout = (4-1) questionTable + (4-2) sendLayout(textBox + button)
        VerticalLayout tabLayout = new VerticalLayout();
        tabLayout.setSizeFull();
        tabLayout.setSpacing(true);

        // (4-1) questionTable
        tabLayout.addComponent(questionTable = createQuestionTable());
        // (4-2) sendLayout(textBox + button)
        tabLayout.addComponent(createSendLayout());
        tabLayout.setExpandRatio(questionTable, 1);

        // 첫 번째 탭 = 질문테이블 + 질문 입력 텍스트 박스 + 질문 추가 버튼
        questionTabSheet.addTab(tabLayout, "Question", FontAwesome.QUESTION_CIRCLE);

        return questionTabSheet;
    }

    private Component createSendLayout() {
        textArea = new TextArea();
        textArea.setWidth(100, Unit.PERCENTAGE);
        textArea.setHeight(50,Unit.PIXELS);

        button = new Button();
        button.setIcon(FontAwesome.SEND);
        button.setWidth(50, Unit.PIXELS);
        button.setHeight(50, Unit.PIXELS);
        button.setStyleName(ValoTheme.BUTTON_LARGE);
        button.setStyleName(ValoTheme.BUTTON_PRIMARY);
        button.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        button.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        textArea.addShortcutListener(new ShortcutListener("Send", null, ShortcutAction.KeyCode.ENTER, ShortcutAction.ModifierKey.SHIFT) {
            @Override
            public void handleAction(Object sender, Object target) {
                sendMessage();
            }
        });

        button.addClickListener(event -> sendMessage());

        HorizontalLayout sendLayout = new HorizontalLayout();
        sendLayout.setWidth(100, Unit.PERCENTAGE);
        sendLayout.setSpacing(true);
        sendLayout.addComponent(textArea);
        sendLayout.addComponent(button);
        sendLayout.setExpandRatio(textArea, 1);

        return sendLayout;
    }

    private void findSessionBean() {
        List<Session> sessions = sessionData.findAll();
        if(sessions.size() <= 0) return;

        sessionTable.getContainerDataSource().removeAllItems();
        for(Session session : sessions) {
            sessionTable.getContainerDataSource().addItem(session);
        }

        clickSession(sessions.get(0)); // 세션 데이터 조회시 0번째 item 선택 처리
    }

    private void clickSession(Session session) {
        // 현재 선택된 item(Session) 정보 담아주기
        sessionTable.setNullSelectionItemId(session);

        Set<Long> questionIds = session.getQuestions();
        if(questionIds.size() <= 0){
            questionTable.getContainerDataSource().removeAllItems();
            return;
        }
        findByQuestionIds(session.getQuestions());
    }

    private void findByQuestionIds(Set<Long> ids) {
        if(ids.size() <= 0) return;

        List<Question> questions = questionData.findByIds(ids);
        questionTable.getContainerDataSource().removeAllItems();

        for(Question itemId : questions) {
            questionTable.getContainerDataSource().addItem(itemId);
        }

        scrollEnd();
    }

    private void scrollEnd() {
        int itemSize = questionTable.size();
        if(itemSize <= 0) return;
        if(itemSize < questionTable.getPageLength()) {
            questionTable.setPageLength(itemSize);
        }

        // 스크롤을 해당 아이템으로 움직여 주기
        questionTable.setCurrentPageFirstItemId(questionTable.lastItemId());
        // 해당 아이템을 클릭 상태로 변경
        questionTable.select(questionTable.lastItemId());
    }

    private void sendMessage() {
        String textValue = textArea.getValue(); // 질문 입력 값 가져오기
        if(textValue.isEmpty()) return;

        // 현재 선택된 item (Session) 정보 가져오기
        Session session = (Session)sessionTable.getNullSelectionItemId();
        // 질문 메시지 저장
        Question question = questionData.save(new Question(session.getId(), textValue, UserSession.getUser().getId()));
        // 테이블에 데이터 추가 하기
        questionTable.getContainerDataSource().addItem(question);
        // 입력 필드 초기화
        textArea.setValue("");
        // 테이블 스크롤 이동
        scrollEnd();

        // 신규 메시지 보내기
        eventBus.send(question);
    }

    private Table createQuestionTable() {
        // (4-1) 질문리스트 테이블 구성
        Table table = new Table();
        table.setSizeFull();

        table.addStyleName(ValoTheme.TABLE_NO_HEADER);
        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        table.addStyleName(ValoTheme.TABLE_SMALL);

        table.setContainerDataSource(new BeanItemContainer<>(Question.class));

        table.addGeneratedColumn("user", (source, itemId, columnId) -> {
            Question message = (Question)itemId;
            User user = userData.findOne(message.getCreateBy());

            String iconTag = "<img class=\"v-image v-widget v-round-image\" src=\""
                    + ImageUploader.getUrl(user.getImgPath()) + "\" alt=\"\">";

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
            String time = dateFormat.format(message.getCreateDate());

            StringBuffer htmls = new StringBuffer();
            htmls.append(iconTag);
            htmls.append(" ");
            htmls.append("<div style=\"text-align:center;\"><b>" + user.getName() + "</b></div>");
            htmls.append("<div style=\"text-align:center;font-size:small\"><b>" + time + "</b></div>");

            Label label = new Label(htmls.toString(), ContentMode.HTML);
            label.setSizeUndefined();
            return label;
        });

        table.addGeneratedColumn("message", (source, itemId, columnId) -> {
            Question message = (Question)itemId;
            TextArea textArea = new TextArea();
            textArea.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
            textArea.setHeight(70, Unit.PIXELS);
            textArea.setWidth(100, Unit.PERCENTAGE);
            textArea.setValue(message.getMessage());
            return textArea;
        });

        // visibleColumns : user, message
        table.setVisibleColumns("user", "message");
        // ColumnHeader : Session
        table.setColumnHeaders("User", "Message");
        // message가 폭(ColumnWidth)의 비중을 가장 크게 설정
        table.setColumnExpandRatio("message", 1);
        return table;
    }

    // Component가 Window 객체에 바인딩(추가) 될 때 호출
    @Override
    public void attach() {
        eventBus.register(this); // 구독 등록
        super.attach();
    }

    // Component가 Window 객체에 언바인딩(제거) 될 때 호출
    @Override
    public void detach() {
        eventBus.unregister(this); // 구독 해제
        super.detach();
    }

    @Override
    public void receive(Question question) {
        // 신규 메시지 수신하기
        Session selectedSession = (Session) sessionTable.getNullSelectionItemId();
        // 신규 메시지가 현재 선택된 세션과 동일한지
        if(selectedSession.getId() != question.getSessionId()) return;
        // 신규 메시지가 중복된 메시지 인지
        if(questionTable.getContainerDataSource().containsId(question)) return;
        // 신규 메시지 질문 테이블에 추가
        questionTable.getContainerDataSource().addItem(question);
        // 신규 메시지 도착으로 상태값 변경
        hasNewItem.set(true);
    }

    private boolean isPhone() {
        if(UI.getCurrent().getPage().getWebBrowser().isAndroid() ||
                UI.getCurrent().getPage().getWebBrowser().isIPhone()) {
            if(UI.getCurrent().getPage().getBrowserWindowWidth() < 500) {
                return true;
            }
        }
        return false;
    }

    class RefreshThread extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(1000);
                    if(hasNewItem.get()) {
                        // 신규 메시지가 도착 상태면 서버 푸쉬 진행
                        UI.getCurrent().access(() -> scrollEnd());
                        hasNewItem.set(false);
                    }
                } catch (InterruptedException ex) {
                    hasNewItem.set(false);
                }
            }
        }
    }
}
