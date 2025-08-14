package org.pahappa.systems.kpiTracker.views.systemSetup;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;

import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;

import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "orgFitCategoryView")
@Setter
@Getter
@SessionScoped
public class OrgFitCategoryView extends PaginatedTableView<OrgFitCategory, OrgFitCategoryService,OrgFitCategoryService> {
    private OrgFitCategoryService orgFitService;
    private Search search;
    private double totalWeight;


    @PostConstruct
    public void init(){
        orgFitService = ApplicationContextProvider.getBean(OrgFitCategoryService.class);
        reloadFilterReset();
        loadTotalWeight();
    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(orgFitService.getInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE),i,i1));
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
    public List load(int i, int i1, Map map, Map map1) {
        return null;
    }

    @Override
    public void reloadFilterReset(){
        super.setTotalRecords(orgFitService.countInstances(new Search()));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }

    public void loadTotalWeight(){

        List<OrgFitCategory> categories = orgFitService.getAllInstances();
        totalWeight = categories.stream().mapToDouble(OrgFitCategory::getWeight).sum();
    }

    public void deleteClient(OrgFitCategory orgFitCategory) {
        try {
            orgFitService.deleteInstance(orgFitCategory);
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }

    public String manageItems() {
        // The f:setPropertyActionListener has already set the selected category
        // on the orgFitCategoryItemView bean. Now we just navigate.

        // Replace "yourTargetPage.xhtml" with the actual filename.
        return "/pages/systemSetup/OrgFitCategoryItemTable.xhtml?faces-redirect=true";
    }
}
