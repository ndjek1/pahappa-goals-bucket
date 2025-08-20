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
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

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

    public KPIForm() {
        super("KPIForm", 600, 500);
    }

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.departmentGoals = this.departmentGoalService.getAllInstances();
        this.teamGoals = this.teamGoalService.getAllInstances();
        this.measurementUnits = Arrays.asList(MeasurementUnit.values());
        this.frequencies = Arrays.asList(Frequency.values());
        
        // Initialize model to prevent null pointer exceptions
        if (super.model == null) {
            super.model = new KPI();
        }
    }

    @Override
    public void persist() throws Exception {
        if (model.getName() == null || model.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("KPI must have a name.");
        }
        if (model.getTargetValue() == null || model.getTargetValue() <= 0) {
            throw new IllegalArgumentException("Target value must be greater than 0.");
        }
        if (model.getMeasurementUnit() == null) {
            throw new IllegalArgumentException("KPI must have a measurement unit.");
        }
        if (model.getFrequency() == null) {
            throw new IllegalArgumentException("KPI must have a frequency.");
        }
        if (model.getStartDate() == null) {
            throw new IllegalArgumentException("KPI must have a start date.");
        }
        
        kpisService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new KPI();
    }
    
    @Override
    public void setFormProperties() {
        super.setFormProperties();
        // The parent class already sets isEditing = true when model is not null
    }
}
