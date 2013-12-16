package org.openmrs.module.appui.simplifier;

import org.openmrs.Privilege;
import org.openmrs.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quick and dirty class to convert a user object into a simple representation similar to what is created by
 * the rest web services module; this class does *not* yet populate the simple representation with all of the fields
 * that the web services module does
 * Specifically, it would be good to convert Roles here as well as privileges
 */
public class UserSimplifier {

    public Map<String, Object> convert(User user) {

        Map<String, Object> simpleUser = new HashMap<String, Object>();

        simpleUser.put("username", user.getUsername());
        simpleUser.put("display", user.getUsername());
        simpleUser.put("systemId", user.getSystemId());
        simpleUser.put("uuid", user.getUuid());
        simpleUser.put("retired", user.getRetired().toString());

        List<Map<String,Object>> simplePrivileges = new ArrayList<Map<String, Object>>();

        for (Privilege privilege : user.getPrivileges()) {

            Map<String,Object> simplePrivilege = new HashMap<String, Object>();

            simplePrivilege.put("name", privilege.getName());
            simplePrivilege.put("display", privilege.getName());
            simplePrivilege.put("description", privilege.getDescription());
            simplePrivilege.put("uuid", privilege.getUuid());
            simplePrivilege.put("retired", privilege.getRetired().toString());

            simplePrivileges.add(simplePrivilege);
        }

        simpleUser.put("privileges", simplePrivileges);
        simpleUser.put("fn", new UserConverterUtils(user));

        return simpleUser;
    }

    /**
     * Utility methods that are bound to the context to ease evaluation in Javascript
     */
    public class UserConverterUtils {

        private User user;

        public UserConverterUtils(User user) {
            this.user = user;
        }

        public boolean hasPrivilege(String privilege) {

            for (Privilege p : user.getPrivileges()) {
                if (p.getPrivilege().equals(privilege)) {
                    return true;
                }
            }

            return false;
        }
    }
}
