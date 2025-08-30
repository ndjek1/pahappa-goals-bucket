package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.Frequency;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.MeasurementUnit;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name = "individualKPIForm", eager = true)
@Getter
@Setter
@SessionScoped
public class IndividualKPIForm extends DialogForm<KPI> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(IndividualKPIForm.class.getSimpleName());
    private KpisService kpisService;
    private StaffService staffService;
    private IndividualGoalService individualGoalService;
    
    private List<IndividualGoal> individualGoals;
    private List<MeasurementUnit> measurementUnits;
    private List<Frequency> frequencies;
    
    private IndividualGoal selectedIndividualGoal;
    private Staff staff;
    private User loggedinUser;
    
    // Add edit field like user dialogs
    private boolean edit;

    public IndividualKPIForm() {
        super(HyperLinks.INDIVIDUAL_KPI_FORM_DIALOG, 750, 400);
    }

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.loggedinUser = SharedAppData.getLoggedInUser();
        loadStaff();
        loadData();
        resetModal();
    }

    private void loadData() {
        try {
            Search search = new Search(IndividualGoal.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus", RecordStatus.ACTIVE),
                    Filter.equal("staff.id",staff.getId())
            );
            this.individualGoals = individualGoalService.getInstances(search,0,0);
        } catch (Exception e) {
            this.individualGoals = Arrays.asList();
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
    public void loadStaff() {
        try {
            // Get staff record for the logged-in user
            this.staff = staffService.searchUniqueByPropertyEqual("user.id", loggedinUser.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading staff information", e);
        }
    }
    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            validateForm();
            
            // Set the individual goal (clear other goal types)
            if (selectedIndividualGoal != null) {
                model.setIndividualGoal(selectedIndividualGoal);
                model.setOrganizationGoal(null);
                model.setDepartmentGoal(null);
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
            UiUtils.ComposeFailure("Error", "Failed to save individual KPI: " + e.getMessage());
            throw new OperationFailedException("Failed to save individual KPI: " + e.getMessage());
        }
    }

    private void validateForm() throws ValidationFailedException {
        if (model.getName() == null || model.getName().trim().isEmpty()) {
            throw new ValidationFailedException("KPI name is required.");
        }
        
        if (selectedIndividualGoal == null) {
            throw new ValidationFailedException("Individual goal is required.");
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
        
        if (model.getEndDate() != null && model.getEndDate().before(model.getStartDate())) {
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
            if (model.getIndividualGoal() != null) {
                selectedIndividualGoal = model.getIndividualGoal();
            }
        } else {
            this.edit = false;
        }
    }

    private void clearSelections() {
        selectedIndividualGoal = null;
    }

    public void show() {
        super.show(null);
    }

    public void hide() {
        super.hide();
    }
}