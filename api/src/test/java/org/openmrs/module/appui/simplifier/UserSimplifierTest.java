package org.openmrs.module.appui.simplifier;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.UserContext;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserSimplifierTest {


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
    public void shouldGenerateSimpleUser() {

        // user is just doctor here
        user.addRole(doctor);

        Map<String,Object> simpleUser = new UserSimplifier().convert(user);

        assertThat((String) simpleUser.get("username"),is("bobMeIn"));
        assertThat((String) simpleUser.get("display"),is("bobMeIn"));
        assertThat((String) simpleUser.get("uuid"),is("123-456"));
        assertThat((String) simpleUser.get("systemId"),is("abc"));
        assertThat((String) simpleUser.get("retired"),is("true"));
        assertThat(((List) simpleUser.get("privileges")).size(), is(1));

        Map<String,Object> simplePrivilege = (Map<String,Object>) ((List) simpleUser.get("privileges")).get(0);

        assertThat((String) simplePrivilege.get("name"),is("View Patients"));
        assertThat((String) simplePrivilege.get("uuid"),is("654-321"));
        assertThat((String) simplePrivilege.get("description"),is("Ability to view patients"));
        assertThat((String) simplePrivilege.get("retired"),is("false"));

        assertNotNull(simpleUser.get("fn"));
        assertTrue(((UserSimplifier.UserConverterUtils) simpleUser.get("fn")).hasPrivilege("View Patients"));
        assertFalse(((UserSimplifier.UserConverterUtils) simpleUser.get("fn")).hasPrivilege("Edit Patients"));
    }

}
