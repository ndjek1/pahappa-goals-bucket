package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.OrgFitCategoryItemService;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategoryItem;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;

@ManagedBean(name = "orgFitCategoryItemForm")
@Getter
@Setter
@SessionScoped
public class OrgFitCategoryItemForm extends DialogForm<OrgFitCategoryItem> {
    private OrgFitCategoryItemService orgFitCategoryItemService;
    private OrgFitCategoryService orgFitCategoryService;
    private OrgFitCategory selectedOrgFitCategory;
    private boolean edit;


    private List<OrgFitCategory> orgFitCategoryList;

    public OrgFitCategoryItemForm() {
        super(HyperLinks.ORG_FIT_CATEGORY_ITEM_DIALOG, 500, 200);
    }

    @PostConstruct
    public void init() {
        this.orgFitCategoryItemService = ApplicationContextProvider.getBean(OrgFitCategoryItemService.class);
        this.orgFitCategoryService = ApplicationContextProvider.getBean(OrgFitCategoryService.class);
        loadOrgFitCategoryList();
    }

    @Override
    public void persist() throws Exception {
        super.model.setOrgFitCategory(this.selectedOrgFitCategory);
        orgFitCategoryItemService.saveInstance(super.model);
    }

    public void update(){
        orgFitCategoryItemService.merge(this.model);
        hide();
    }


    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new OrgFitCategoryItem();
        setEdit(false);
    }
    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if(super.model != null)
            setEdit(true);
    }

    public void handleAction() throws Exception {
        if (edit) {
            update();
        } else {
            save();
        }
    }

    private void loadOrgFitCategoryList() {
        this.orgFitCategoryList = orgFitCategoryService.getAllInstances();
    }

    public void prepareForCategory(OrgFitCategory category) {
        this.selectedOrgFitCategory = category;
        this.show(null);
    }

}
