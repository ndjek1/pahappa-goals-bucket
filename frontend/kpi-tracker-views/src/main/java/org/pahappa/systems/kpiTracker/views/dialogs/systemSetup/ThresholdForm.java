package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ThresholdService;
import org.pahappa.systems.kpiTracker.models.systemSetup.Threshold;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ThresholdLevel;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "thresholdForm")
@Getter
@Setter
@SessionScoped
public class ThresholdForm extends DialogForm<Threshold> {
    private static final long serialVersionUID = 1L;
    private ThresholdService thresholdService;
    private List<ThresholdLevel> thresholdLevels;

    public ThresholdForm() {
        super(HyperLinks.THRESHOLD_DIALOG, 500, 400);
    }

    @PostConstruct
    public void init() {
        this.thresholdService = ApplicationContextProvider.getBean(ThresholdService.class);
        this.thresholdLevels = Arrays.asList(ThresholdLevel.values());
    }

    @Override
    public void persist() throws Exception {

        //  Prevent duplicate thresholds for same level
        Threshold existingThreshold = thresholdService.searchUniqueByPropertyEqual("level", model.getLevel());
        if (existingThreshold != null && (model.getId() == null || !existingThreshold.getId().equals(model.getId()))) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Duplicate Threshold",
                            "A threshold already exists for level: " + model.getLevel().name()));
            return;
        }

        //  Validation rules
        if (model.getNeedImprovementScore() >= model.getBelowExpectationScore()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid Values",
                            "Need Improvement score must be less than Below Expectation score."));
            return;
        }

        if (model.getBelowExpectationScore() >= model.getMeetExpectationScore()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid Values",
                            "Below Expectation score must be less than Meet Expectation score."));
            return;
        }

        if (model.getMeetExpectationScore() >= model.getExceedsExpectationScore()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid Values",
                            "Meet Expectation score must be less than Exceeds Expectation score."));
            return;
        }

        // Save and reset
        thresholdService.saveInstance(super.model);
        resetModal();
        hide();
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Threshold();
    }
}
