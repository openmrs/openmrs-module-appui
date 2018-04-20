package org.openmrs.module.appui.fragment.controller;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.appframework.domain.Extension;

import java.util.ArrayList;
import java.util.List;

public class HeaderFragmentControllerTest {

    Extension ext1 = new Extension("1","app1","extp1","type","ext1","url",1);
    Extension ext2 = new Extension("2","app2","extp2","type","ext2","url",2);
    Extension ext3 = new Extension("3","app3","extp3","type","ext3","url",3);

    @Test
    public void shouldReturnLowsetExtension() {
        List<Extension> exts = new ArrayList<Extension>();
        exts.add(ext1);
        exts.add(ext2);
        exts.add(ext3);

        HeaderFragmentController headerFragmentController=new HeaderFragmentController();
        Assert.assertEquals(headerFragmentController.getLowestOrderExtenstion(exts),ext1);

    }
}
