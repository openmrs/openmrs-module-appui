package org.openmrs.module.appui.fragment.controller;

import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.List;

public class ExtensionPointFragmentController {

    public void controller(FragmentConfiguration config,
                           @FragmentParam("id") String id,
                           @FragmentParam("contextModel") AppContextModel contextModel,
                           @SpringBean AppFrameworkService appFrameworkService,
                           FragmentModel model) {
        config.require("id", "contextModel");

        List<Extension> extensions = appFrameworkService.getExtensionsForCurrentUser(id, contextModel);
        model.addAttribute("extensions", extensions);
        model.addAttribute("contextModel", contextModel);
    }

}
