package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
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

@ManagedBean(name = "teamKPIView")
@Getter
@Setter
@ViewScoped
public class TeamKPIView extends PaginatedTableView<KPI, TeamKPIView, TeamKPIView> {
    
    private KpisService kpisService;
    private Search search;
    private TeamService teamService;
    private Team team;
    private User loggedinUser;

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        reloadFilterReset();
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        loadTeam();
    }

    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        Search search1 = new Search();

        // Create AND filter for team goal and recordStatus
        Filter filter = Filter.and(
                Filter.equal("teamGoal.team", team),
                Filter.equal("recordStatus", RecordStatus.ACTIVE)
        );

        search1.addFilter(filter);

        super.setDataModels(kpisService.getInstances(search1, i, i1));
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
    public void reloadFilterReset() {
        super.setTotalRecords(kpisService.countInstances(new Search()));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    public void loadTeam() {
        if (loggedinUser.hasRole("TEAM_LEAD")) {
            this.team = teamService.getAllInstances()
                    .stream()
                    .filter(t -> t.getTeamHead() != null
                            && t.getTeamHead().equals(loggedinUser))
                    .findFirst()
                    .orElse(null);
        }
    }

    public void deleteKPI(KPI kpi) {
        try {
            kpisService.deleteInstance(kpi);
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }
}
