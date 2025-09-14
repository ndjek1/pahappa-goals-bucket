package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "individualGoalsView")
@Getter
@Setter
@SessionScoped
public class IndividualGoalsView extends PaginatedTableView<IndividualGoal,IndividualGoalsView,IndividualGoalsView> {
    private Search search;
    String searchTerm;
    private boolean saved;
    private boolean updated;
    private IndividualGoalService individualGoalService;
    private StaffService staffService;
    private User loggedinUser;
    private Staff loggedinStaff;


    @PostConstruct
    public void init(){
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.loggedinUser = SharedAppData.getLoggedInUser();
        loadStaff();
        reloadFilterReset();
    }

    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(individualGoalService.getInstances(this.search,i,i1));
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
        loadStaff();
        search = new Search(IndividualGoal.class);

        if (this.loggedinStaff != null) {
            search.addFilterAnd(
                    Filter.equal("staff.id", this.loggedinStaff.getId()),
                    Filter.equal("recordStatus", RecordStatus.ACTIVE)
            );
        } else {
            UiUtils.ComposeFailure("Error", "Staff not found for the logged in user.");
            return;
        }
        if(this.searchTerm != null && !this.searchTerm.isEmpty()){
            search.addFilterILike("name", "%" + searchTerm + "%");
        }

        super.setTotalRecords(this.individualGoalService.countInstances(search));
        try {
            super.reloadFilterReset();
        } catch(Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    public void loadStaff() {
        if (loggedinUser != null) {
            this.loggedinStaff = staffService.searchUniqueByPropertyEqual("user.id", loggedinUser.getId());
            if (this.loggedinStaff == null) {
                UiUtils.ComposeFailure("Error", "No staff record found for user " + loggedinUser.getUsername());
            }
        } else {
            UiUtils.ComposeFailure("Error", "No logged-in user found in session.");
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

    public void deleteClient(IndividualGoal individualGoal) {
        try {
            individualGoalService.deleteInstance(individualGoal);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Goal deleted successfully."));
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }
}
