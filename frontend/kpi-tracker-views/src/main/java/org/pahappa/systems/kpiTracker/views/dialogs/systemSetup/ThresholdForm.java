package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ThresholdService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.Threshold;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleType;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ThresholdLevel;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ManagedBean(name = "thresholdForm")
@Getter
@Setter
@SessionScoped
public class ThresholdForm extends DialogForm<Threshold> {
    private static final long serialVersionUID = 1L;
    private ThresholdService thresholdService;
    private List<ThresholdLevel> thresholdLevels = new ArrayList<>();

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
        if (model.getLevel() == null) {
            UiUtils.showMessageBox("Missing Information","Please select both review cycle type and start date.");
            return;
        }

        // Check if a threshold already exists for the same level (and it's not the same one being edited)

        Threshold existingThreshold = thresholdService.searchUniqueByPropertyEqual("level", model.getLevel());
        if (existingThreshold != null && (model.getId() == null || !existingThreshold.getId().equals(model.getId()))) {

            UiUtils.showMessageBox("Duplicate Threshold","A threshold already exists for level: " + model.getLevel().name());

            return;
        }

        thresholdService.saveInstance(super.model);

    }



    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Threshold();
    }
}
