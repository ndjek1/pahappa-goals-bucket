package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "departmentKPIView")
@Getter
@Setter
@SessionScoped
public class DepartmentKPIView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private KpisService kpisService;
    private DepartmentService departmentService;
    private Department department;
    private User loggedinUser;
    private List<KPI> dataModels;
    private String searchTerm;
    private String dataEmptyMessage = "No department KPIs found.";

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        loadDepartment();
        reloadFilterReset();
    }

    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        Search search1 = new Search();

        // Create AND filter for department goal and recordStatus
        Filter filter = Filter.and(
                Filter.equal("departmentGoal.department", department),
                Filter.equal("recordStatus", RecordStatus.ACTIVE)
        );

        search1.addFilter(filter);

        this.dataModels = kpisService.getInstances(search1, i, i1);
    }

    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    public String getFileName() {
        return null;
    }

    public List load(int i, int i1, Map map, Map map1) {
        return null;
    }

    public void reloadFilterReset() {
        if (department != null) {
            Search search = new Search();
            search.addFilterEqual("departmentGoal.department", department);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            
            if (searchTerm != null && !searchTerm.isEmpty()) {
                search.addFilterILike("name", "%" + searchTerm + "%");
            }
            
            this.dataModels = kpisService.getInstances(search, 0, 1000);
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

    public void deleteKPI(KPI kpi) {
        try {
            kpisService.deleteInstance(kpi);
            reloadFilterReset();
        } catch (OperationFailedException e) {
            e.printStackTrace();
        }
    }
}
