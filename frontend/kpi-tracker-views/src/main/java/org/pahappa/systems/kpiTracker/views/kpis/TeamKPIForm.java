package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.Frequency;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.MeasurementUnit;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "teamKpiForm", eager = true)
@Getter
@Setter
@SessionScoped
public class TeamKPIForm extends DialogForm<KPI> {

    private static final long serialVersionUID = 1L;
    
    private KpisService kpisService;
    private TeamGoalService teamGoalService;
    
    private List<TeamGoal> teamGoals;
    private List<MeasurementUnit> measurementUnits;
    private List<Frequency> frequencies;
    
    private TeamGoal selectedTeamGoal;
    
    // Add edit field like user dialogs
    private boolean edit;

    public TeamKPIForm() {
        super(HyperLinks.TEAM_KPI_FORM_DIALOG, 750, 400);
    }

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        
        loadData();
        resetModal();
    }

    private void loadData() {
        try {
            this.teamGoals = teamGoalService.getAllInstances();
        } catch (Exception e) {
            this.teamGoals = Arrays.asList();
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
            
            // Set the team goal (clear other goal types)
            if (selectedTeamGoal != null) {
                model.setTeamGoal(selectedTeamGoal);
                model.setOrganizationGoal(null);
                model.setDepartmentGoal(null);
            }
            double remaining = 0.0;
            if(this.selectedTeamGoal != null) {
                remaining =  canAddKPIForGoal(this.selectedTeamGoal);
            }

            if( remaining<model.getWeight()){
                if(remaining<=0){
                    UiUtils.showMessageBox("Warning", "Can no longer  contribute to the selected goal");
                }else {
                    UiUtils.showMessageBox("Contribution weight to high", "Can only contribute " + remaining + " to this selected goal");
                }
                return;
            }
            
            kpisService.saveInstance(super.model);
            resetModal();
            hide();
        } catch (ValidationFailedException e) {
            UiUtils.ComposeFailure("Validation Error", e.getMessage());
            throw e;
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Operation Error", e.getMessage());
            throw e;
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to save team KPI: " + e.getMessage());
            throw new OperationFailedException("Failed to save team KPI: " + e.getMessage());
        }
    }

    private void validateForm() throws ValidationFailedException {
        if (model.getName() == null || model.getName().trim().isEmpty()) {
            throw new ValidationFailedException("KPI name is required.");
        }
        
        if (selectedTeamGoal == null) {
            throw new ValidationFailedException("Team goal is required.");
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
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new KPI();
        setEdit(false);
        clearSelections();
    }

    public double canAddKPIForGoal(TeamGoal goal){
        double totalWeight = 0.0;
        Search search = new Search(KPI.class);
        search.addFilterAnd(
                Filter.equal("recordStatus" , RecordStatus.ACTIVE),
                Filter.equal("teamGoal.id", goal.getId())
        );

        List<KPI> kpis = this.kpisService.getInstances(search,0,0);
        if(kpis.size() > 0){
            for(KPI kp : kpis){
                totalWeight += kp.getWeight();
            }
        }
        return  Math.max(0,100-totalWeight);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (this.model != null && this.model.getId() != null) {
            this.edit = true;
            // Set selections based on existing model
            if (model.getTeamGoal() != null) {
                selectedTeamGoal = model.getTeamGoal();
            }
        } else {
            this.edit = false;
        }
    }

    private void clearSelections() {
        selectedTeamGoal = null;
    }

    public void show() {
        super.show(null);
    }

    public void hide() {
        super.hide();
    }
}
