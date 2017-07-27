package com.vseminar.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by enosent on 2017. 7. 14..
 */
public class AboutView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "about";

    public AboutView() {
        addComponents(createTopBar());
    }
    public HorizontalLayout createTopBar() {
        Label title = new Label("About");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        HorizontalLayout topLayout = new HorizontalLayout();

        topLayout.setSpacing(true);
        topLayout.setWidth(100, Unit.PERCENTAGE);
        topLayout.addComponents(title);
        topLayout.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

        return topLayout;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

}
