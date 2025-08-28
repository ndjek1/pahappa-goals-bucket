package org.pahappa.systems.kpiTracker.views.activities;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.DepartmentActivityService;
import org.pahappa.systems.kpiTracker.core.services.activities.TeamActivityService;
import org.pahappa.systems.kpiTracker.core.services.activities.IndividualActivityService;
import org.pahappa.systems.kpiTracker.models.activities.DepartmentActivity;
import org.pahappa.systems.kpiTracker.models.activities.TeamActivity;
import org.pahappa.systems.kpiTracker.models.activities.IndividualActivity;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@ManagedBean(name = "activitiesView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks.ACTIVITIES_VIEW)
public class ActivitiesView extends PaginatedTableView<DepartmentActivity, ActivitiesView, ActivitiesView> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ActivitiesView.class.getSimpleName());

    private DepartmentActivityService departmentActivityService;
    private TeamActivityService teamActivityService;
    private IndividualActivityService individualActivityService;

    private List<DepartmentActivity> departmentActivities;
    private List<TeamActivity> teamActivities;
    private List<IndividualActivity> individualActivities;

    private Search search;
    private String searchTerm;
    private ActivityStatus selectedStatus;

    @PostConstruct
    public void init() {
        departmentActivityService = ApplicationContextProvider.getBean(DepartmentActivityService.class);
        teamActivityService = ApplicationContextProvider.getBean(TeamActivityService.class);
        individualActivityService = ApplicationContextProvider.getBean(IndividualActivityService.class);

        reloadFilterReset();
        loadDepartmentActivities();
        loadTeamActivities();
        loadIndividualActivities();
    }

    @Override
    public void reloadFromDB(int first, int pageSize, Map<String, Object> filters) throws Exception {
        super.setDataModels(departmentActivityService.getInstances(buildSearch(), first, pageSize));
    }

    @Override
    public void reloadFilterReset() {
        super.setTotalRecords(departmentActivityService.countInstances(buildSearch()));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    private Search buildSearch() {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterILike("title", "%" + searchTerm + "%");
        }
        if (selectedStatus != null) {
            search.addFilterEqual("status", selectedStatus);
        }
        return search;
    }

    public void loadDepartmentActivities() {
        this.departmentActivities = departmentActivityService.getAllInstances();
    }

    public void loadTeamActivities() {
        this.teamActivities = teamActivityService.getAllInstances();
    }

    public void loadIndividualActivities() {
        this.individualActivities = individualActivityService.getAllInstances();
    }

    public void deleteDepartmentActivity(DepartmentActivity activity) {
        try {
            departmentActivityService.deleteInstance(activity);
            reloadFilterReset();
            loadDepartmentActivities();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }

    public void deleteTeamActivity(TeamActivity activity) {
        try {
            teamActivityService.deleteInstance(activity);
            loadTeamActivities();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }

    public void deleteIndividualActivity(IndividualActivity activity) {
        try {
            individualActivityService.deleteInstance(activity);
            loadIndividualActivities();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }

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
