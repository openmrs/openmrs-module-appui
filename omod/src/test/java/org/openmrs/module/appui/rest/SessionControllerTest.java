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

    @Test
    public void shouldNotSetSessionLocationDueToWrongUuid()throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/appui/session");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Object previousSession = sessionController.get(request,response);

        request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/appui/session");
        response = new MockHttpServletResponse();
        Object locationUuid = "0000000c-0000-0000-0000-2361b3446eb8";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("location", locationUuid);
        request.addHeader("Content-Type", "text/json");
        Object res = sessionController.set(map, request,response);

        assertTrue(res.equals(previousSession));
    }
    @Test
    public void shouldNotSetSessionLocationDueToEmptyUuid()throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/appui/session");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Object previousSession = sessionController.get(request,response);

        request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/appui/session");
        response = new MockHttpServletResponse();
        Object locationUuid = "";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("location", locationUuid);
        request.addHeader("Content-Type", "text/json");
        Object res = sessionController.set(map, request,response);

        assertTrue(res.equals(previousSession));
    }

    @Test
    public void shouldNotSetSessionLocationDueToNoUuidPassed()throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/appui/session");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Object previousSession = sessionController.get(request,response);

        request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/appui/session");
        response = new MockHttpServletResponse();

        Map<String, Object> map = new HashMap<String, Object>();
        request.addHeader("Content-Type", "text/json");
        Object res = sessionController.set(map, request,response);

        assertTrue(res.equals(previousSession));
    }

}
