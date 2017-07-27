package com.vseminar.menu;

import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vseminar.data.model.RoleType;

/**
 * Created by enosent on 2017. 7. 14..
 */
public class Navi {

    private String fragment; // vaadin-seminar/#!{fragment} 주소
    private String viewName;
    private Class<? extends View> viewClass;
    private Resource icon;
    private RoleType roleType;

    public Navi(String fragment, String viewName, Class<? extends View> viewClass, Resource icon, RoleType roleType) {
        this.fragment = fragment;
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.roleType = roleType;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public void setViewClass(Class<? extends View> viewClass) {
        this.viewClass = viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public void setIcon(Resource icon) {
        this.icon = icon;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
