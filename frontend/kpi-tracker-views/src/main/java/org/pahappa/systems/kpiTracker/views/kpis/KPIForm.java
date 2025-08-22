package org.pahappa.systems.kpiTracker.views.kpis;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.Frequency;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.MeasurementUnit;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "kpiForm")
@Getter
@Setter
@SessionScoped
public class KPIForm extends DialogForm<KPI> {

    private static final long serialVersionUID = 1L;
    private KpisService kpisService;
    private DepartmentGoalService departmentGoalService;
    private TeamGoalService teamGoalService;
    private List<DepartmentGoal> departmentGoals;
    private List<TeamGoal> teamGoals;
    private List<MeasurementUnit> measurementUnits;
    private List<Frequency> frequencies;
    
    // Add edit field like user dialogs
    private boolean edit;

    public KPIForm() {
        super(HyperLinks.KPI_FORM_DIALOG, 700, 600);
    }

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        loadData();
    }

    private void loadData() {
        this.departmentGoals = this.departmentGoalService.getAllInstances();
        this.teamGoals = this.teamGoalService.getAllInstances();
        this.measurementUnits = Arrays.asList(MeasurementUnit.values());
        this.frequencies = Arrays.asList(Frequency.values());
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            validateForm();
            kpisService.saveInstance(super.model);
            
            // Show success message
            UiUtils.showMessageBox("Success", "KPI saved successfully!");
            
            // Handle successful save and trigger refresh
            handleSuccessfulSave();
            
        } catch (Exception e) {
            throw new OperationFailedException("Failed to save KPI: " + e.getMessage());
        }
    }

    /**
     * Handle successful save and trigger refresh in parent views
     */
    private void handleSuccessfulSave() {
        try {
            // Hide the dialog
            hide();
            
            // Trigger dialogReturn event for automatic refresh
            PrimeFaces.current().executeScript("PF('kpiForm').fireEvent('dialogReturn');");
            
            // Reset the modal for next use
            resetModal();
            
            System.out.println("KPI saved successfully, dialog closed and refresh triggered");
        } catch (Exception e) {
            System.err.println("Error handling successful save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void validateForm() throws ValidationFailedException {
        if (model.getName() == null || model.getName().trim().isEmpty()) {
            throw new ValidationFailedException("KPI must have a name.");
        }
        if (model.getTargetValue() == null || model.getTargetValue() <= 0) {
            throw new ValidationFailedException("Target value must be greater than 0.");
        }
        if (model.getMeasurementUnit() == null) {
            throw new ValidationFailedException("KPI must have a measurement unit.");
        }
        if (model.getFrequency() == null) {
            throw new ValidationFailedException("KPI must have a frequency.");
        }
        if (model.getStartDate() == null) {
            throw new ValidationFailedException("KPI must have a start date.");
        }
        
        // Validate date range if end date is provided
        if (model.getEndDate() != null && model.getStartDate() != null) {
            if (model.getEndDate().before(model.getStartDate())) {
                throw new ValidationFailedException("End date cannot be before start date.");
            }
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new KPI();
        setEdit(false);
    }
    
    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            setEdit(false);
        }
    }
    
    /**
     * Refresh the data when needed
     */
    public void refreshData() {
        loadData();
    }
    
    public void show() {
        // Show the dialog using the DialogForm base class method
        super.show(null);
    }
    
    public void hide() {
        // Hide the dialog using the DialogForm base class method
        super.hide();
    }
}
