package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "organizationGoalsView")
@Getter
@Setter
@ViewScoped
public class OrganizationGoalView extends PaginatedTableView<OrganizationGoal, OrganizationGoalView,OrganizationGoalView> {

    private OrganizationGoalService organizationGoalService;
    private Search search;


    @PostConstruct
    public void init(){
        organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        reloadFilterReset();
    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(organizationGoalService.getInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE),i,i1));
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
        super.setTotalRecords(organizationGoalService.countInstances(new Search()));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }

    public void deleteClient(OrganizationGoal organizationGoal) {
        try {
            organizationGoalService.deleteInstance(organizationGoal);
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }
}
