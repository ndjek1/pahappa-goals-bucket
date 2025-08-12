package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;


import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "orgFitCategoryForm")
@SessionScoped
public class OrgFitCategoryForm extends DialogForm<OrgFitCategory> {
    private OrgFitCategoryService orgFitCategoryService;

    public OrgFitCategoryForm() {
        super(HyperLinks.ORG_FIT_CATEGORY_DIALOG, 700, 800);
    }

    @PostConstruct
    public void init() {
        this.orgFitCategoryService = ApplicationContextProvider.getBean(OrgFitCategoryService.class);

    }

    @Override
    public void persist() throws Exception {

        orgFitCategoryService.saveInstance(super.model);
    }


    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new OrgFitCategory();
    }
}
