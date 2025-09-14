package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "teamGoalView")
@Getter
@Setter
@SessionScoped
public class TeamGoalView extends PaginatedTableView<TeamGoal,TeamGoalView,TeamGoalView> {
    private TeamGoalService teamGoalService;
    String searchTerm;
    private Search search;
    private boolean saved;
    private boolean updated;


    @PostConstruct
    public void init(){
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        reloadFilterReset();
    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(teamGoalService.getInstances(this.search,i,i1));
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
        this.search = new Search(TeamGoal.class);
        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterILike("name", "%" + searchTerm + "%");
        }
        super.setTotalRecords(teamGoalService.countInstances(this.search));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }

    public void showSuccessMessage() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (this.saved) {
            // Message for creating a new department
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Goal  created successfully."));
            this.saved = false; // Reset the flag
        }

        if (this.updated) {
            // Message for updating an existing department
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Goal updated successfully."));
            this.updated = false; // Reset the flag
        }
    }

    public void deleteClient(TeamGoal teamGoal) {
        try {
            teamGoalService.deleteInstance(teamGoal);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Goal deleted successfully."));
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }
}
