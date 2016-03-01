package org.openmrs.module.appui.simplifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkServiceImpl;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.SimpleObject;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConversionUtil.class)
public class UiSessionTest {

    private User user;

    private UserContext userContext;

    private Role doctor;

    private Role admin;

    private Privilege viewPatients;

    private Privilege editPatients;


    @Before
    public void setup() {
        user = new User();
        userContext = mock(UserContext.class);
        when(userContext.getAuthenticatedUser()).thenReturn(user);
    }

    @Test
    public void shouldTestAgainstUserInJavaScriptContext() throws Exception{

        SimpleObject userRestRep = new SimpleObject();
        SimpleObject viewPatientsRestRep = new SimpleObject();
        viewPatientsRestRep.put("name", "View Patients");
        SimpleObject editPatientsRestRep = new SimpleObject();
        editPatientsRestRep.put("name", "Edit Patients");

        userRestRep.put("privileges", Arrays.asList(viewPatientsRestRep, editPatientsRestRep));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(user, Representation.DEFAULT)).thenReturn(userRestRep);

        UiSessionContext uiSessionContext = new UiSessionContext();
        uiSessionContext.setUserContext(userContext);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        AppFrameworkServiceImpl service = new AppFrameworkServiceImpl(null, null, null, null, null, null, null, null);

        assertTrue(service.checkRequireExpression(extensionRequiring("hasMemberWithProperty(user.privileges, 'name', 'View Patients')"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("hasMemberWithProperty(user.privileges, 'name', 'Delete Patients')"), appContextModel));
        assertTrue(service.checkRequireExpression(extensionRequiring("hasMemberWithProperty(user.privileges, 'name', 'View Patients') || hasMemberWithProperty(user.privileges, 'name', 'Delete Patients')"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("hasMemberWithProperty(user.privileges, 'name', 'View Patients') && hasMemberWithProperty(user.privileges, 'name', 'Delete Patients')"), appContextModel));
    }

    @Test
    public void shouldTestAgainstALocationInJavascript() throws Exception {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        UiSessionContext uiSessionContext = new UiSessionContext();
        uiSessionContext.setUserContext(userContext);
        uiSessionContext.setSessionLocation(sessionLocation);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        AppFrameworkServiceImpl service = new AppFrameworkServiceImpl(null, null, null, null, null, null, null, null);

        assertTrue(service.checkRequireExpression(extensionRequiring("sessionLocation.uuid == '123abc'"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("sessionLocation.uuid == '456efg'"), appContextModel));

    }

    @Test
    public void shouldTagsTestAgainstALocationInJavascript() throws Exception {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Admit");
        SimpleObject transferTag = new SimpleObject();
        transferTag .put("display", "Transfer");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag, transferTag));


        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        UiSessionContext uiSessionContext = new UiSessionContext();
        uiSessionContext.setUserContext(userContext);
        uiSessionContext.setSessionLocation(sessionLocation);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        AppFrameworkServiceImpl service = new AppFrameworkServiceImpl(null, null, null, null, null, null, null, null);

        assertTrue(service.checkRequireExpression(extensionRequiring("sessionLocation.uuid == '123abc'"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("sessionLocation.uuid == '456efg'"), appContextModel));

    }

    @Test
    public void shouldTestForMemberWithProperty() throws Exception {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Admit");
        SimpleObject transferTag = new SimpleObject();
        transferTag .put("display", "Transfer");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag, transferTag));


        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        UiSessionContext uiSessionContext = new UiSessionContext();
        uiSessionContext.setUserContext(userContext);
        uiSessionContext.setSessionLocation(sessionLocation);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        AppFrameworkServiceImpl service = new AppFrameworkServiceImpl(null, null, null, null, null, null, null, null);

        assertTrue(service.checkRequireExpression(extensionRequiring("hasMemberWithProperty(sessionLocation.tags, 'display', 'Admit')"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("hasMemberWithProperty(sessionLocation.tags, 'display', 'Inpatient')"), appContextModel));

        // confirm that it doesn't fail if no matching key
        assertFalse(service.checkRequireExpression(extensionRequiring("hasMemberWithProperty(sessionLocation.tags, 'bogus', 'Transfer')"), appContextModel));

        // confirm that it doesn't fail if no matching array
        assertFalse(service.checkRequireExpression(extensionRequiring("hasMemberWithProperty(sessionLocation.bogus, 'display', 'Transfer')"), appContextModel));

    }

    private Extension extensionRequiring(String requires) {
        Extension extension = new Extension();
        extension.setRequire(requires);
        return extension;
    }

}
