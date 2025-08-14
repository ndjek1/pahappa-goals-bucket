package org.pahappa.systems.kpiTracker.views.systemSetup;

import com.cloudinary.utils.StringUtils;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;

import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryItemService;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategoryItem;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.utils.GeneralSearchUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.utils.SearchField;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "orgFitCategoryItemView")
@Setter
@Getter
@SessionScoped
public class OrgFitCategoryItemView extends PaginatedTableView<OrgFitCategoryItem, OrgFitCategoryItemService,OrgFitCategoryItemService> {

    private OrgFitCategoryItemService orgFitCategoryItemServiceService;
    private Search search;
    private String searchTerm;
    private List<OrgFitCategory> orgFitCategories;
    private OrgFitCategory selectedOrgFitCategory;
    private List<SearchField> searchFields, selectedSearchFields;
    private Date createdFrom, createdTo;


    @PostConstruct
    public void init(){
        orgFitCategoryItemServiceService = ApplicationContextProvider.getBean(OrgFitCategoryItemService.class);
        reloadFilterReset();
    }

    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(
                orgFitCategoryItemServiceService.getInstances(this.search, i, i1)
        );
    }



    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public List<OrgFitCategoryItem> load(int i, int i1, Map map, Map map1) {
        return getDataModels();
    }
    @Override
    public void reloadFilterReset() {
        this.search = new Search(OrgFitCategoryItem.class);
        this.search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        // Text search for name OR description
        if (!StringUtils.isEmpty(searchTerm)) {
            this.search.addFilterOr(
                    Filter.ilike("name", "%" + searchTerm + "%"),
                    Filter.ilike("description", "%" + searchTerm + "%")
            );
        }

        // Filter by category
        if (selectedOrgFitCategory != null) {
            this.search.addFilterEqual("orgFitCategory", selectedOrgFitCategory);
        }

        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Count total records for pagination
        super.setTotalRecords(orgFitCategoryItemServiceService.countInstances(this.search));
    }


    public void deleteClient(OrgFitCategoryItem orgFitCategoryItem) {
        try {
            orgFitCategoryItemServiceService.deleteInstance(orgFitCategoryItem);
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }

    public String prepareForCategory(OrgFitCategory category) {
        this.selectedOrgFitCategory = category;
        reloadFilterReset();
        return "/pages/systemSetup/OrgFitCategoryItemTable.xhtml?faces-redirect=true";
    }

    public String backToCategories(){
        return "/pages/systemSetup/OrgFitCategoryTable.xhtml";
    }

}
