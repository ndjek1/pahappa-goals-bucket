package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GlobalWeightService;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "globalWeightsForm")
@Getter
@Setter
@SessionScoped
public class GlobalWeightForm extends DialogForm<GlobalWeight> {
    private static final long serialVersionUID = 1L;

    private GlobalWeightService globalWeightService;

    public GlobalWeightForm() {
        super(HyperLinks.GLOBAL_WEIGHT_DIALOG, 800, 700);
    }

    @PostConstruct
    public void init() {
        globalWeightService = ApplicationContextProvider.getBean(GlobalWeightService.class);
    }

    @Override
    public void persist() throws Exception {
        globalWeightService.saveInstance(super.model);
    }


    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new GlobalWeight();
    }


}
