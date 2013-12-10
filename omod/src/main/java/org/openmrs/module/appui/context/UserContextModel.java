package org.openmrs.module.appui.context;

import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.UserContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple context model used to store information about a user (usually used to store
 * roles & privileges of the current authenticated user)
 */
public class UserContextModel {

    private List<String> privileges;

    private List<String> roles;

    public UserContextModel() {

    }

    public UserContextModel(UserContext userContext) {

        User user = userContext.getAuthenticatedUser();

        privileges = new ArrayList<String>();

        for (Privilege privilege : user.getPrivileges()) {
            privileges.add(privilege.getPrivilege());
        }

        roles = new ArrayList<String>();

        for (Role role : user.getRoles()) {
            roles.add(role.getRole());
        }

    }

    public List<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
