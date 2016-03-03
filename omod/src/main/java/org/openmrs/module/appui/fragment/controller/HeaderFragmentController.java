/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.appui.fragment.controller;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.AppUiExtensions;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class HeaderFragmentController {

    // RA-592: don't use PrivilegeConstants.VIEW_LOCATIONS
    private static final String GET_LOCATIONS = "Get Locations";
    private static final String VIEW_LOCATIONS = "View Locations";

    public void controller(@SpringBean AppFrameworkService appFrameworkService, FragmentModel fragmentModel,
                           PageRequest pageRequest, UiSessionContext sessionContext,
                           @CookieValue(value = "referenceapplication.lastSessionLocation",required = false) Location lastLoginLocation) {
        try {
            Context.addProxyPrivilege(GET_LOCATIONS);
            Context.addProxyPrivilege(VIEW_LOCATIONS);
            fragmentModel.addAttribute("loginLocations", appFrameworkService.getLoginLocations());

            List<Extension> exts = appFrameworkService.getExtensionsForCurrentUser(AppUiExtensions.HEADER_CONFIG_EXTENSION);
            Map<String, Object> configSettings = exts.size() > 0 ? exts.get(0).getExtensionParams() : null;
            fragmentModel.addAttribute("configSettings", configSettings);
            List<Extension> userAccountMenuItems = appFrameworkService.getExtensionsForCurrentUser(AppUiExtensions.HEADER_USER_ACCOUNT_MENU_ITEMS_EXTENSION);
            fragmentModel.addAttribute("userAccountMenuItems", userAccountMenuItems);

            Integer sessionLocationId = sessionContext.getSessionLocationId();
            if(lastLoginLocation.getId() != sessionLocationId ){
                HttpServletRequest request = pageRequest.getRequest();
                HttpServletResponse response = pageRequest.getResponse();

                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (int i = 0; i < cookies.length; i++) {
                        if (cookies[i].getName().equals("referenceapplication.lastSessionLocation")) {
                            cookies[i].setPath("/openmrs/");
                            cookies[i].setValue(sessionLocationId.toString());
                            response.addCookie(cookies[i]);
                        }
                    }
                }
            }
        } finally {
            Context.removeProxyPrivilege(GET_LOCATIONS);
            Context.removeProxyPrivilege(VIEW_LOCATIONS);
        }
    }

}
