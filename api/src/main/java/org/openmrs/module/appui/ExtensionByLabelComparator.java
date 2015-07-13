package org.openmrs.module.appui;

import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.util.OpenmrsUtil;

import java.util.Comparator;

/**
 * Comparator that can be used to sort extension points by their translated labels
 */
public class ExtensionByLabelComparator implements Comparator<Extension> {

    private UiUtils ui = null;

    public ExtensionByLabelComparator(UiUtils ui) {
        this.ui = ui;
    }

    @Override
    public int compare(Extension ext1, Extension ext2) {
        return OpenmrsUtil.compareWithNullAsGreatest(ui.message(ext1.getLabel()), ui.message(ext2.getLabel()));
    }
}