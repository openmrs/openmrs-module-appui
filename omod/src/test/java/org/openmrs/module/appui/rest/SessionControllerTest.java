package org.openmrs.module.appui.rest;


import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SessionControllerTest extends BaseModuleWebContextSensitiveTest{


    @Autowired
    SessionController sessionController;

    @Test
    public void shouldSetSessionLocation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/appui/session");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object locationUuid = "9356400c-a5a2-4532-8f2b-2361b3446eb8";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("location", locationUuid);
        request.addHeader("Content-Type", "text/json");
        Object res = sessionController.set(map, request,response);

        assertTrue(res.toString().contains("sessionLocation=Xanadu"));

    }

}
