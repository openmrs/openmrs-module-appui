package org.openmrs.module.appui.converter;

import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class StringToAppDescriptorConverter implements Converter<String, AppDescriptor> {

    @Autowired
    private AppFrameworkService appFrameworkService;

    @Override
    public AppDescriptor convert(String appId) {
        return appFrameworkService.getApp(appId);
    }

}
