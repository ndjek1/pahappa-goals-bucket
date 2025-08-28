package org.pahappa.systems.kpiTracker.views.activities;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.TeamActivityService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.activities.DepartmentActivity;
import org.pahappa.systems.kpiTracker.models.activities.TeamActivity;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
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

@ManagedBean(name = "teamActivitiesView")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.TEAM_ACTIVITIES_VIEW)
public class TeamActivitiesView extends PaginatedTableView<TeamActivity, DepartmentActivitiesView, DepartmentActivitiesView> implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient TeamActivityService teamActivityService;
    private transient TeamService teamService;

    // Filters
    private String searchTerm;
    private  Search search;
    private ActivityStatus selectedStatus;
    private Date createdFrom, createdTo;

    // Restriction
    private User loggedInUser;
    private Team team;

    @PostConstruct
    public void init() {
        this.teamActivityService = ApplicationContextProvider.getBean(TeamActivityService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);

        loggedInUser = SharedAppData.getLoggedInUser();
        loadTeam();
        reloadFilterReset();
    }

    @Override
    public void reloadFromDB(int first, int pageSize, Map<String, Object> filters) throws Exception {

        super.setDataModels(teamActivityService.getInstances(this.search, first, pageSize));
    }

    @Override
    public void reloadFilterReset() {
        this.search = new Search(TeamActivity.class);
        search.addFilterAnd(
                Filter.equal("recordStatus",RecordStatus.ACTIVE),
                Filter.equal("team.id",this.team.getId())
        );
        super.setTotalRecords(teamActivityService.countInstances(this.search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }


    public void loadTeam() {
        if (loggedInUser.hasRole("Team Lead")) {
            this.team = teamService.getAllInstances()
                    .stream()
                    .filter(d -> d.getTeamHead() != null
                            && d.getTeamHead().equals(loggedInUser))
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
