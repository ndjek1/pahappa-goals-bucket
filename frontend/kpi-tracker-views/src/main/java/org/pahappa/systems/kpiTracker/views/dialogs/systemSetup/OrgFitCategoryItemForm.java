package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;

import com.google.common.collect.Sets;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryItemService;
import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategoryItem;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean(name = "orgFitCategoryItemForm")
@Getter
@Setter
@SessionScoped
public class OrgFitCategoryItemForm extends DialogForm<OrgFitCategoryItem> {
    private OrgFitCategoryItemService orgFitCategoryItemService;
    private OrgFitCategoryService orgFitCategoryService;
    private OrgFitCategory selectedOrgFitCategory;


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


    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new OrgFitCategoryItem();
    }

    private void loadOrgFitCategoryList() {
        this.orgFitCategoryList = orgFitCategoryService.getAllInstances();
    }

    public void prepareForCategory(OrgFitCategory category) {
        this.selectedOrgFitCategory = category;
        this.show(null);
    }

}
