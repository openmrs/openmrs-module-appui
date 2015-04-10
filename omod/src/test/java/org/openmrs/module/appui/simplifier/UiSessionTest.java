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

        doctor = new Role("Doctor");
        admin = new Role("Admin");

        viewPatients = new Privilege("View Patients");
        viewPatients.setUuid("654-321");
        viewPatients.setDescription("Ability to view patients");

        editPatients = new Privilege("Edit Patients");

        doctor.addPrivilege(viewPatients);

        admin.addPrivilege(editPatients);
        admin.addPrivilege(editPatients);

        user = new User();
        user.setUsername("bobMeIn");
        user.setUuid("123-456");
        user.setSystemId("abc");
        user.setRetired(true);
        userContext = mock(UserContext.class);
        when(userContext.getAuthenticatedUser()).thenReturn(user);

    }

    @Test
    public void shouldTestAgainstSimpleUserInJavaScriptContext() throws Exception{

        user.addRole(doctor);
        user.addRole(admin);

        UiSessionContext uiSessionContext = new UiSessionContext();
        uiSessionContext.setUserContext(userContext);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        AppFrameworkServiceImpl service = new AppFrameworkServiceImpl(null, null, null, null, null, null, null, null);

        assertTrue(service.checkRequireExpression(extensionRequiring("user.get('fn').hasPrivilege('View Patients')"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("user.get('fn').hasPrivilege('Delete Patients')"), appContextModel));
        assertTrue(service.checkRequireExpression(extensionRequiring("user.get('fn').hasPrivilege('View Patients') || user.get('fn').hasPrivilege('Delete Patients')"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("user.get('fn').hasPrivilege('View Patients') && user.get('fn').hasPrivilege('Delete Patients')"), appContextModel));

    }

    @Test
    public void shouldTestAgainstALocationInJavascript() {

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

        assertTrue(service.checkRequireExpression(extensionRequiring("sessionLocation.get('uuid') == '123abc'"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("sessionLocation.get('uuid') == '456efg'"), appContextModel));

    }

    @Test
    public void shouldTagsTestAgainstALocationInJavascript() {

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

        assertTrue(service.checkRequireExpression(extensionRequiring("sessionLocation.get('uuid') == '123abc'"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("sessionLocation.get('uuid') == '456efg'"), appContextModel));

    }

    @Test
    public void shouldTestForMemberWithProperty() {

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

        assertTrue(service.checkRequireExpression(extensionRequiring("util.arrayHasMemberWithProperty(sessionLocation.get('tags'), 'display', 'Admit')"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("util.arrayHasMemberWithProperty(sessionLocation.get('tags'), 'display', 'Inpatient')"), appContextModel));

        // confirm that it doesn't fail if no matching key
        assertFalse(service.checkRequireExpression(extensionRequiring("util.arrayHasMemberWithProperty(sessionLocation.get('tags'), 'bogus', 'Transfer')"), appContextModel));

        // confirm that it doesn't fail if no matching array
        assertFalse(service.checkRequireExpression(extensionRequiring("util.arrayHasMemberWithProperty(sessionLocation.get('bogus'), 'display', 'Transfer')"), appContextModel));

    }

    private Extension extensionRequiring(String requires) {
        Extension extension = new Extension();
        extension.setRequire(requires);
        return extension;
    }

}
