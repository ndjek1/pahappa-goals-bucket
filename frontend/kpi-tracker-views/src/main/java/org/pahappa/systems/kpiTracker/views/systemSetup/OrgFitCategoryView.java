package org.pahappa.systems.kpiTracker.views.systemSetup;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;

import org.pahappa.systems.kpiTracker.core.services.GlobalWeightService;
import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;

import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "orgFitCategoryView")
@Setter
@Getter
@ViewScoped
public class OrgFitCategoryView extends PaginatedTableView<OrgFitCategory, OrgFitCategoryService,OrgFitCategoryService> {
    private OrgFitCategoryService orgFitService;
    private Search search;
    private double totalWeight;
    private GlobalWeightService globalWeightService;
    private ReviewCycleService reviewCycleService;


    @PostConstruct
    public void init(){
        orgFitService = ApplicationContextProvider.getBean(OrgFitCategoryService.class);
        globalWeightService = ApplicationContextProvider.getBean(GlobalWeightService.class);
        reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
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
            loadTotalWeight();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }

    public void loadTotalWeight(){

        List<OrgFitCategory> categories = orgFitService.getAllInstances();
        GlobalWeight globalWeight =this.getGlobalWeightForActiveCycle();
        totalWeight = (categories.stream().mapToDouble(OrgFitCategory::getWeight).sum()*100)/globalWeight.getOrgFitWeight();
    }

    public void deleteClient(OrgFitCategory orgFitCategory) {
        try {
            orgFitService.deleteInstance(orgFitCategory);
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }

    public GlobalWeight getGlobalWeightForActiveCycle() {
        ReviewCycle activeReviewCycle = this.reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
        return this.globalWeightService.searchUniqueByPropertyEqual("reviewCycle", activeReviewCycle);
    }
}
