package org.openmrs.module.appui.fragment.controller;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

public class SessionFragmentController {
	
	public String setLocation(@RequestParam("locationId") Location location,
	                   @SpringBean("locationService") LocationService locationService, UiSessionContext context) {
		context.setSessionLocation(location);
		return null;
	}
	
}
