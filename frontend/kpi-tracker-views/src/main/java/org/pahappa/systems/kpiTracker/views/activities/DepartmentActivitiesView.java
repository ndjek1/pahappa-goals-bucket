package org.pahappa.systems.kpiTracker.views.activities;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.DepartmentActivityService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.activities.DepartmentActivity;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "departmentActivitiesView")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.DEPARTMENT_ACTIVITIES_VIEW)
public class DepartmentActivitiesView extends PaginatedTableView<DepartmentActivity, DepartmentActivitiesView, DepartmentActivitiesView> implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient DepartmentActivityService departmentActivityService;
    private transient DepartmentService departmentService;

    // Filters
    private String searchTerm;
    private  Search search;
    private ActivityStatus selectedStatus;
    private Date createdFrom, createdTo;

    // Restriction
    private User loggedInUser;
    private Department currentDepartment;

    @PostConstruct
    public void init() {
        this.departmentActivityService = ApplicationContextProvider.getBean(DepartmentActivityService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);

        loggedInUser = SharedAppData.getLoggedInUser();
        loadDepartment();
        reloadFilterReset();
    }

    @Override
    public void reloadFromDB(int first, int pageSize, Map<String, Object> filters) throws Exception {

        super.setDataModels(departmentActivityService.getInstances(this.search, first, pageSize));
    }

    @Override
    public void reloadFilterReset() {
        this.search = new Search(DepartmentActivity.class);
        search.addFilterAnd(
                Filter.equal("recordStatus",RecordStatus.ACTIVE),
                Filter.equal("department.id",this.currentDepartment.getId())
        );
        super.setTotalRecords(departmentActivityService.countInstances(this.search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }


    public void loadDepartment() {
        if (loggedInUser != null && loggedInUser.hasRole("Department Lead")) {
            this.currentDepartment = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null && d.getDepartmentHead().equals(loggedInUser))
                    .findFirst()
                    .orElse(null);
        }
    }



    public void clearFilters() {
        this.searchTerm = null;
        this.selectedStatus = null;
        this.createdFrom = null;
        this.createdTo = null;
        reloadFilterReset();
    }


    public void deleteActivity(DepartmentActivity activity) { try { if (activity != null) { departmentActivityService.deleteInstance(activity); UiUtils.showMessageBox("Action successful", "Activity has been deleted successfully."); reloadFilterReset(); } } catch (Exception e) { UiUtils.ComposeFailure("Action failed", "Failed to delete activity: " + e.getMessage()); } }

    @Override
    public List getExcelReportModels() {
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
}
