package org.openmrs.module.appui.fragment.controller;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.openmrs.Location;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.ui.framework.formatter.FormatterService;
import org.openmrs.ui.framework.fragment.FragmentActionUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class SessionFragmentControllerTest {
    
    @Mock
    private AppFrameworkService appFrameworkService;

    @Mock
    private MessageSource messageSource;

    private List<Location> loginLocations;

    @Before
    public void setup() {
        loginLocations = new ArrayList<Location>();

        Location location1 = new Location();
        location1.setId(1);
        location1.setUuid("location1_uuid");
        location1.setName("location1");

        Location location2 = new Location();
        location2.setId(3);
        location2.setUuid("location2_uuid");
        location2.setName("location2");

        loginLocations.add(location1);
        loginLocations.add(location2);

        when(appFrameworkService.getLoginLocations()).thenReturn(loginLocations);
    }

    @Test
    public void getLoginLocations_shouldReturnSimplifiedJsonLoginLocations() {
        // setup
        FormatterService formatterService = new FormatterService();
        formatterService.setMessageSource(messageSource);
        UiUtils ui = new FragmentActionUiUtils(null, null, null, formatterService);
        
        // replay
        List<SimpleObject> actualLoginLocations = new SessionFragmentController().getLoginLocations(ui, appFrameworkService);

        // verify
        assertEquals(loginLocations.size(), actualLoginLocations.size());
        assertEquals(loginLocations.get(0).getName(), actualLoginLocations.get(0).get("name"));
        assertEquals(loginLocations.get(0).getUuid(), actualLoginLocations.get(0).get("uuid"));
        assertEquals(loginLocations.get(0).getId(), actualLoginLocations.get(0).get("id"));
        assertEquals(loginLocations.get(1).getName(), actualLoginLocations.get(1).get("name"));
        assertEquals(loginLocations.get(1).getUuid(), actualLoginLocations.get(1).get("uuid"));
        assertEquals(loginLocations.get(1).getId(), actualLoginLocations.get(1).get("id"));
    }
}
