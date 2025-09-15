package org.pahappa.systems.kpiTracker.views;

import com.googlecode.genericdao.search.Search;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.client.controllers.WebAppExceptionHandler;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.utils.SortField;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean(name = "dashboard")
@SessionScoped
@Getter
@Setter
@ViewPath(path = HyperLinks.DASHBOARD)
public class Dashboard extends WebAppExceptionHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    private User loggedinUser;
    private DepartmentService departmentService;
    private StaffService staffService;
    private ReviewCycleService reviewCycleService;
    private DepartmentGoalService departmentGoalService;
    private TeamGoalService teamGoalService;
    private IndividualGoalService individualGoalService;
    private int goalCount;

    Search search = new Search();
    @Getter
    private String searchTerm;
    private SortField selectedSortField;
    private int departmentCount;
    private int staffCount;
    private ReviewCycle activeReviewCycle;

    @SuppressWarnings("unused")
    private String viewPath;

    @PostConstruct
    public void init() {
        loggedinUser = SharedAppData.getLoggedInUser();
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.departmentCount = this.departmentService.getAllInstances().size();
        this.staffCount = this.staffService.getAllInstances().size();
        loadReviewCycle();
        loadGoals();
    }


    public void loadReviewCycle(){
        if(this.reviewCycleService != null){
            this.activeReviewCycle = reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
        }
    }

    public void loadGoals(){
        if(this.departmentGoalService != null){
            this.goalCount += this.departmentGoalService.getAllInstances().size();
        }
        if(this.teamGoalService != null){
            this.goalCount += this.teamGoalService.getAllInstances().size();
        }
        if(this.individualGoalService != null){
            this.goalCount += this.individualGoalService.getAllInstances().size();
        }
    }


}
