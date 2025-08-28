package org.pahappa.systems.kpiTracker.views.ratings;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.core.services.ratings.SupervisorAssessmentService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.rating.SupervisorAssessment;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "supervisorAssessmentForm")
@Getter
@Setter
@SessionScoped
public class SupervisorAssessmentForm extends DialogForm<SupervisorAssessment> {
    private Staff supervisor;
    private Staff staff;
    private ReviewCycle reviewCycle;
    private User loggedInUser;
    private StaffService staffService;

    private SupervisorAssessmentService supervisorAssessmentService;
    private ReviewCycleService reviewCycleService;
    public SupervisorAssessmentForm() {
        super(HyperLinks.SUPERVISOR_ASSESSMENT_FORM_DIALOG, 500, 200);
    }

    @PostConstruct
    public void init() {
        this.supervisorAssessmentService =ApplicationContextProvider.getBean(SupervisorAssessmentService.class);
        this.reviewCycleService =ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.loggedInUser = SharedAppData.getLoggedInUser();
        super.model = new  SupervisorAssessment();
        loadSupervisor();
        loadActiveReviewCycle();
    }

    @Override
    public void persist() throws Exception {
        if(this.supervisor != null && this.staff != null) {
            super.model.setSupervisor(supervisor);
            super.model.setStaff(staff);
        }
        if(this.reviewCycle != null) {
            super.model.setReviewCycle(reviewCycle);
        }
        supervisorAssessmentService.saveInstance(super.model);
        resetModal();
        hide();
    }
    public void loadActiveReviewCycle() {
        this.reviewCycle = this.reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
    }
    public  void loadSupervisor(){
        if(this.loggedInUser != null) {
            this.supervisor = staffService.searchUniqueByPropertyEqual("user.id",loggedInUser.getId());
        }
    }



    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new SupervisorAssessment();
    }
}
