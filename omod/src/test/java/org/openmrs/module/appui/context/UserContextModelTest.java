package org.openmrs.module.appui.context;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkServiceImpl;
import org.openmrs.module.appui.UiSessionContext;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserContextModelTest {

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
        editPatients = new Privilege("Edit Patients");

        doctor.addPrivilege(viewPatients);

        admin.addPrivilege(editPatients);
        admin.addPrivilege(editPatients);

        user = new User();
        userContext = mock(UserContext.class);
        when(userContext.getAuthenticatedUser()).thenReturn(user);

    }

    @Test
    public void shouldGenerateUserContextModel() {

        user.addRole(doctor);
        user.addRole(admin);

        UserContextModel userContextModel = new UserContextModel(userContext);

        assertThat(userContextModel.getRoles(), containsInAnyOrder("Doctor", "Admin"));
        assertThat(userContextModel.getPrivileges(), containsInAnyOrder("View Patients", "Edit Patients"));
    }

    @Test
    public void shouldTestAgainstUserContextModel() {

        user.addRole(doctor);
        user.addRole(admin);

        UiSessionContext uiSessionContext = new UiSessionContext();
        uiSessionContext.setUserContext(userContext);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        AppFrameworkServiceImpl service = new AppFrameworkServiceImpl(null, null, null, null, null, null);

        assertTrue(service.checkRequireExpression(extensionRequiring("user.privileges.contains('View Patients')"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("user.privileges.contains('Delete Patients')"), appContextModel));
        assertTrue(service.checkRequireExpression(extensionRequiring("user.privileges.contains('View Patients') || user.privileges.contains('Delete Patients')"), appContextModel));
        assertFalse(service.checkRequireExpression(extensionRequiring("user.privileges.contains('View Patients') && user.privileges.contains('Delete Patients')"), appContextModel));
    }

    private Extension extensionRequiring(String requires) {
        Extension extension = new Extension();
        extension.setRequire(requires);
        return extension;
    }
}
