package com.vseminar.config;

import com.vaadin.server.*;
import org.jsoup.nodes.Element;


public class VSeminarSessionInitListener implements SessionInitListener {

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        event.getSession().addBootstrapListener(new BootstrapListener() {

            @Override
            public void modifyBootstrapFragment(BootstrapFragmentResponse response) {


            }

            @Override
            public void modifyBootstrapPage(BootstrapPageResponse response) {
                final Element head = response.getDocument().head();

                // 모바일 브라우저의 크기에 맞도록 스케일 처리
                head.appendElement("meta")
                        .attr("name", "viewport")
                        .attr("content", "width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no");
            }
        });
    }

}
