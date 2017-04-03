package org.openmrs.module.appui.fragment.controller;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.ObjectResult;
import org.springframework.web.bind.annotation.RequestParam;

public class SessionFragmentController {
	
	public FragmentActionResult setLocation(@RequestParam("locationId") Location location,
	                   @SpringBean("locationService") LocationService locationService, UiSessionContext context) {
		context.setSessionLocation(location);
		return new ObjectResult(ConversionUtil.convertToRepresentation(location, Representation.DEFAULT));  // TODO: the callback in header.gsp should actually use this information instead of automatically setting the session location and id
	}
	
}
