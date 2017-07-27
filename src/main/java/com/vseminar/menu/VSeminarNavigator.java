package com.vseminar.menu;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import com.vseminar.data.UserSession;
import com.vseminar.data.model.RoleType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by enosent on 2017. 7. 14..
 */
public class VSeminarNavigator extends Navigator {

    // Menu Mock Data ( key : fragment )
    public static final Map<String, Navi> naviMaps = new LinkedHashMap<>();

    private Map<String, Navi> activeNaviMaps;

    public VSeminarNavigator(UI ui, ComponentContainer container) {
        super(ui, container);

        final RoleType userRoleType = UserSession.getUser().getRole();

        activeNaviMaps = new LinkedHashMap<>();

        naviMaps.forEach( (key, item) -> {
            // 현재 권한 >= 메뉴 권한
            if(userRoleType.ordinal() >= item.getRoleType().ordinal()) {
                super.addView(item.getFragment(), item.getViewClass());
                activeNaviMaps.put(item.getFragment(), item);
            }
        });
    }

    public Map<String, Navi> getActiveNaviMaps() {
        return activeNaviMaps;
    }

}
