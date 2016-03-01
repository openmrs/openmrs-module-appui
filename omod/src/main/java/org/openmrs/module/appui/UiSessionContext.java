package org.openmrs.module.appui;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.LocationService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.context.SessionContext;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.util.PrivilegeConstants;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UiSessionContext extends SessionContext {

    public final static String LOCATION_SESSION_ATTRIBUTE = "emrContext.sessionLocationId";

    protected LocationService locationService;

    protected ProviderService providerService;

    protected UserContext userContext;

    protected Provider currentProvider;

    protected Location sessionLocation;
    
    protected HttpSession session;


    /**
     * Default constructor users for testing
     */
    public UiSessionContext() {

    }

    public UiSessionContext(LocationService locationService, ProviderService providerService, HttpServletRequest request) {
        this.locationService = locationService;
        this.providerService = providerService;
        this.session = request.getSession();
        Integer locationId = (Integer) session.getAttribute(LOCATION_SESSION_ATTRIBUTE);
        if (locationId != null) {
            this.setSessionLocationId(locationId);
            sessionLocation = locationService.getLocation(locationId);
        }
        userContext = Context.getUserContext();
        if (userContext != null && userContext.getAuthenticatedUser() != null) {
            User currentUser = userContext.getAuthenticatedUser();
            Collection<Provider> providers;
            try {
                Context.addProxyPrivilege(PrivilegeConstants.VIEW_PROVIDERS);
                providers = providerService.getProvidersByPerson(currentUser.getPerson(), false);
            } finally {
                Context.removeProxyPrivilege(PrivilegeConstants.VIEW_PROVIDERS);
            }
            if (providers.size() > 1) {
                throw new IllegalStateException("Can't handle users with multiple provider accounts");
            } else if (providers.size() == 1) {
                currentProvider = providers.iterator().next();
            }
        }
    }

    public Location getSessionLocation() {
        return sessionLocation;
    }

    public void setSessionLocation(Location sessionLocation) {
    	if (session != null) {
            session.setAttribute(LOCATION_SESSION_ATTRIBUTE, sessionLocation.getId());
        }
        this.sessionLocation = sessionLocation;
        this.sessionLocationId = sessionLocation.getId();
    }

    @Override
    public Integer getSessionLocationId() {
        return sessionLocation == null ? null : sessionLocation.getLocationId();
    }

    public User getCurrentUser() {
        return userContext.getAuthenticatedUser();
    }

    @Override
    public Integer getCurrentUserId() {
        User currentUser = getCurrentUser();
        return currentUser == null ? null : currentUser.getUserId();
    }

    public Provider getCurrentProvider() {
        return currentProvider;
    }

    @Override
    public Integer getCurrentProviderId() {
        return currentProvider == null ? null : currentProvider.getProviderId();
    }

    public boolean isAuthenticated() {
        return userContext.isAuthenticated();
    }

    /**
     * @throws {@link org.openmrs.api.APIAuthenticationException} if no user is authenticated
     */
    public void requireAuthentication() throws APIAuthenticationException {
        if (!isAuthenticated()) {
            throw new APIAuthenticationException();
        }
    }

    public Locale getLocale() {
        return userContext.getLocale();
    }

    public AppContextModel generateAppContextModel() {
        AppContextModel model = new AppContextModel();

        model.put("user", ConversionUtil.convertToRepresentation(userContext.getAuthenticatedUser(), Representation.DEFAULT));

        if (sessionLocation != null) {
            model.put("sessionLocation", ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT));
        }

        if (currentProvider != null) {
            model.put("currentProvider", ConversionUtil.convertToRepresentation(currentProvider, Representation.DEFAULT));
        }

        model.put("util", new AppContextModelUtils());

        return model;
    }


    /**
     * For injecting  mock users during testing
     */
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    /**
     * Utility methods that are bound to the context to ease evaluation in Javascript
     */
    public class AppContextModelUtils {

        public boolean hasMemberWithProperty(List list, String key, Object value) throws Exception {
            if (list == null || value == null) {
                return false;
            }
            for (Object element : list) {
                if (value.equals(PropertyUtils.getProperty(element, key))) {
                    return true;
                }
            }
            return false;
        }
    }

}


