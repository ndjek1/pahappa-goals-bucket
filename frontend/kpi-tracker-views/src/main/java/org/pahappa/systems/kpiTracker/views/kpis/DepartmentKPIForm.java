package org.pahappa.systems.kpiTracker.views.kpis;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.Frequency;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.MeasurementUnit;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "departmentKpiForm", eager = true)
@Getter
@Setter
@SessionScoped
public class DepartmentKPIForm extends DialogForm<KPI> {

    private static final long serialVersionUID = 1L;
    
    private KpisService kpisService;
    private DepartmentGoalService departmentGoalService;
    
    private List<DepartmentGoal> departmentGoals;
    private List<MeasurementUnit> measurementUnits;
    private List<Frequency> frequencies;
    
    private DepartmentGoal selectedDepartmentGoal;
    
    // Add edit field like user dialogs
    private boolean edit;

    public DepartmentKPIForm() {
        super(HyperLinks.DEPARTMENT_KPI_FORM_DIALOG, 700, 600);
    }

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        
        loadData();
        resetModal();
    }

    private void loadData() {
        try {
            this.departmentGoals = departmentGoalService.getAllInstances();
        } catch (Exception e) {
            this.departmentGoals = Arrays.asList();
        }
        
        try {
            this.measurementUnits = Arrays.asList(MeasurementUnit.values());
        } catch (Exception e) {
            this.measurementUnits = Arrays.asList();
        }
        
        try {
            this.frequencies = Arrays.asList(Frequency.values());
        } catch (Exception e) {
            this.frequencies = Arrays.asList();
        }
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            validateForm();
            
            // Set the department goal (clear other goal types)
            if (selectedDepartmentGoal != null) {
                model.setDepartmentGoal(selectedDepartmentGoal);
                model.setOrganizationGoal(null);
                model.setTeamGoal(null);
            }
            
            kpisService.saveInstance(super.model);
        } catch (ValidationFailedException e) {
            UiUtils.ComposeFailure("Validation Error", e.getMessage());
            throw e;
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Operation Error", e.getMessage());
            throw e;
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to save department KPI: " + e.getMessage());
            throw new OperationFailedException("Failed to save department KPI: " + e.getMessage());
        }
    }

    private void validateForm() throws ValidationFailedException {
        if (model.getName() == null || model.getName().trim().isEmpty()) {
            throw new ValidationFailedException("KPI name is required.");
        }
        
        if (selectedDepartmentGoal == null) {
            throw new ValidationFailedException("Department goal is required.");
        }
        
        if (model.getMeasurementUnit() == null) {
            throw new ValidationFailedException("Measurement unit is required.");
        }
        
        if (model.getFrequency() == null) {
            throw new ValidationFailedException("Frequency is required.");
        }
        
        if (model.getTargetValue() == null) {
            throw new ValidationFailedException("Target value is required.");
        }
        
        if (model.getStartDate() == null) {
            throw new ValidationFailedException("Start date is required.");
        }
        
        if (model.getEndDate() == null) {
            throw new ValidationFailedException("End date is required.");
        }
        
        if (model.getEndDate().before(model.getStartDate())) {
            throw new ValidationFailedException("End date cannot be before start date.");
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new KPI();
        setEdit(false);
        clearSelections();
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (this.model != null && this.model.getId() != null) {
            this.edit = true;
            // Set selections based on existing model
            if (model.getDepartmentGoal() != null) {
                selectedDepartmentGoal = model.getDepartmentGoal();
            }
        } else {
            this.edit = false;
        }
    }

    private void clearSelections() {
        selectedDepartmentGoal = null;
    }

    public void show() {
        super.show(null);
    }

    public void hide() {
        super.hide();
    }
}
