package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "departmentGoalsView")
@Getter
@Setter
@ViewScoped
public class DepartmentGoalsView extends PaginatedTableView<DepartmentGoal, DepartmentGoalsView,DepartmentGoalsView> {
    private DepartmentGoalService departmentGoalservice;
    private Search search;
    private DepartmentService departmentService;
    private Department department;
    private User loggedinUser;


    @PostConstruct
    public void init(){
        this.departmentGoalservice = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        reloadFilterReset();
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        loadDepartment();
    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        Search search1 = new Search();

        // Create AND filter for department and recordStatus
        Filter filter = Filter.and(
                Filter.equal("department", department),
                Filter.equal("recordStatus", RecordStatus.ACTIVE)
        );

        search1.addFilter(filter);

        super.setDataModels(departmentGoalservice.getInstances(search1, i, i1));
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
        super.setTotalRecords(departmentGoalservice.countInstances(new Search()));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }

    public void loadDepartment() {
        if (loggedinUser.hasRole("DEPT_LEAD")) {
            this.department = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null
                            && d.getDepartmentHead().equals(loggedinUser))
                    .findFirst()
                    .orElse(null);
        }
    }
    public void deleteClient(DepartmentGoal departmentGoal) {
        try {
            departmentGoalservice.deleteInstance(departmentGoal);
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }
}
