package org.openmrs.module.appui;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.appframework.context.SessionContext;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.ui.framework.fragment.FragmentContext;
import org.openmrs.ui.framework.fragment.FragmentModelConfigurator;
import org.openmrs.ui.framework.fragment.PossibleFragmentActionArgumentProvider;
import org.openmrs.ui.framework.fragment.PossibleFragmentControllerArgumentProvider;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageModelConfigurator;
import org.openmrs.ui.framework.page.PossiblePageControllerArgumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class AppUiArgumentProvider implements PageModelConfigurator, FragmentModelConfigurator,
        PossiblePageControllerArgumentProvider, PossibleFragmentControllerArgumentProvider,
        PossibleFragmentActionArgumentProvider {

    @Autowired
    LocationService locationService;

    @Autowired
    ProviderService providerService;
    
    @Autowired
    PatientService patientService;
    
    @Autowired
    AdtService adtService;

    @Autowired
    @Qualifier("featureToggles")
    FeatureToggleProperties featureToggle;

    public static final String SESSION_CONTEXT_ATTR = "sessionContext";

    @Override
    public void configureModel(PageContext pageContext) {
        HttpServletRequest request = pageContext.getRequest().getRequest();
        pageContext.getModel().addAttribute(SESSION_CONTEXT_ATTR, new UiSessionContext(locationService, providerService, patientService, adtService, request));
        pageContext.getModel().addAttribute("featureToggles", featureToggle);
    }

    @Override
    public void configureModel(FragmentContext fragmentContext) {
        PageModel pageModel = fragmentContext.getPageContext().getModel();
        fragmentContext.getModel().addAttribute(SESSION_CONTEXT_ATTR, pageModel.getAttribute(SESSION_CONTEXT_ATTR));
        fragmentContext.getModel().addAttribute("featureToggles", featureToggle);
    }

    @Override
    public void addPossiblePageControllerArguments(Map<Class<?>, Object> possibleArguments) {
        PageModel pageModel = (PageModel) possibleArguments.get(PageModel.class);
        Object attribute = pageModel.getAttribute(SESSION_CONTEXT_ATTR);
        possibleArguments.put(SessionContext.class, attribute);
        possibleArguments.put(UiSessionContext.class, attribute);
    }

    @Override
    public void addPossibleFragmentControllerArguments(Map<Class<?>, Object> possibleArguments) {
        PageModel pageModel = (PageModel) possibleArguments.get(PageModel.class);
        Object attribute = pageModel.getAttribute(SESSION_CONTEXT_ATTR);
        possibleArguments.put(SessionContext.class, attribute);
        possibleArguments.put(UiSessionContext.class, attribute);
    }

    @Override
    public void addPossibleFragmentActionArguments(Map<Class<?>, Object> possibleArguments) {
        HttpServletRequest request = (HttpServletRequest) possibleArguments.get(HttpServletRequest.class);
        UiSessionContext sessionContext = new UiSessionContext(locationService, providerService, patientService, adtService, request);
        possibleArguments.put(SessionContext.class, sessionContext);
        possibleArguments.put(UiSessionContext.class, sessionContext);
    }

}
