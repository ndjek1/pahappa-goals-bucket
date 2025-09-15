package org.pahappa.systems.kpiTracker.views.activities;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.TeamActivityService;
import org.pahappa.systems.kpiTracker.models.activities.DepartmentActivity;
import org.pahappa.systems.kpiTracker.models.activities.TeamActivity;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityType;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.primefaces.PrimeFaces;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "updateTeamActivity")
@SessionScoped
@Getter
@Setter
public class UpdateTeamActivityDialog extends DialogForm<TeamActivity> {
    private static final long serialVersionUID = 1L;

    private TeamActivityService teamActivityService;
    private boolean completed;

    @PostConstruct
    public void init() {
        this.teamActivityService = ApplicationContextProvider.getBean(TeamActivityService.class);
        setFormProperties();
    }

    public UpdateTeamActivityDialog() {
        super(HyperLinks.UPDATE_TEAM_ACTIVITY_FORM_DIALOG, 700, 450);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            if (this.model.getActivityType() == ActivityType.QUALITATIVE && completed) {
                // Mark as completed
                this.model.setStatus(ActivityStatus.COMPLETED);
            }
            else if (this.model.getActivityType() == ActivityType.QUANTITATIVE) {
                // Validate actual value
                if (this.model.getActualValue() <= 0) {
                    UiUtils.ComposeFailure("Validation Error", "Please enter a valid actual value.");
                    return;
                }
                this.model.setStatus(ActivityStatus.COMPLETED);
            }

            teamActivityService.saveInstance(this.model);
            UiUtils.showMessageBox("Success", "Activity updated successfully.");
            resetModal();
            hide();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to update activity: " + e.getMessage());
            throw new OperationFailedException("Failed to update activity", e);
        }
    }

    @Override
    public void hide(){
        resetModal();
        PrimeFaces.current().dialog().closeDynamic(super.getName());
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model == null) {
            super.model = new TeamActivity();
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new TeamActivity();
    }




    public boolean isQualitative() {
        return model != null && model.getActivityType() == ActivityType.QUALITATIVE;
    }

    public boolean isQuantitative() {
        return model != null && model.getActivityType() == ActivityType.QUANTITATIVE;
    }

}
