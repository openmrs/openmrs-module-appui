package org.openmrs.module.appui.fragment.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.AppUiConstants;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.ObjectResult;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

public class SessionFragmentController {
    
    public FragmentActionResult setLocation(@RequestParam("locationId") Location location,
                                            @RequestParam(value = "clientTimezone", required = false) String clientTimezone,
                                            @SpringBean("locationService") LocationService locationService,
                                            UiUtils ui,
                                            UiSessionContext context, ServletContext servletContext,
                                            HttpServletResponse httpResponse) {
        context.setSessionLocation(location);
        // Update lastSessionLocation cookie which was set from the context path,
        // so next time someone logs, it will default to the same location
        httpResponse.setHeader("Set-Cookie",
                AppUiConstants.COOKIE_NAME_LAST_SESSION_LOCATION + "=" + location.getLocationId()
                        + "; HttpOnly; Path=" + servletContext.getContextPath());
        //Add client timezone to property
        if (StringUtils.isNotBlank(clientTimezone)) {
            ui.setClientTimezone(clientTimezone);
        }
        return new ObjectResult(ConversionUtil.convertToRepresentation(location,
                Representation.DEFAULT));  // TODO: the callback in header.gsp should actually use this information instead of automatically setting the session location and id
    }

    public List<SimpleObject> getLoginLocations(UiUtils ui, @SpringBean AppFrameworkService appFrameworkService) {

        List<Location> loginLocations = appFrameworkService.getLoginLocations();
        List<SimpleObject> ret = new ArrayList<SimpleObject>();
        for (Location location : loginLocations) {
            SimpleObject obj = new SimpleObject();
            obj.put("id", location.getId());
            obj.put("uuid", location.getUuid());
            obj.put("name", location.getName());
            obj.put("display", ui.format(location));
            ret.add(obj);
        }
        return ret;
    }
}
