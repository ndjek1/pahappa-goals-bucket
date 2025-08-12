package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleType;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ManagedBean(name = "reviewCycleForm")
@Getter
@Setter
@SessionScoped
public class ReviewCycleForm extends DialogForm<ReviewCycle> {
    private static final long serialVersionUID = 1L;
    private ReviewCycleService reviewCycleService;
    private List<ReviewCycleType> reviewCycleTypes = new ArrayList<>();

    public ReviewCycleForm() {
        super(HyperLinks.REVIEW_CYCLE_DIALOG, 700, 800);
    }

    @PostConstruct
    public void init() {
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.reviewCycleTypes = Arrays.asList(ReviewCycleType.values());
    }

    @Override
    public void persist() throws Exception {
        if (model.getStartDate() == null || model.getType() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Missing Information",
                            "Please select both review cycle type and start date."));
            return;
        }

        // Auto-set end date based on type
        model.setEndDate(calculateEndDate(model.getType(), model.getStartDate()));

        reviewCycleService.saveInstance(super.model);
    }



    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new ReviewCycle();
    }
    private Date calculateEndDate(ReviewCycleType type, Date start) {
        long daysToAdd;
        switch (type) {
            case WEEKLY:
                daysToAdd = 6; // start day counts as 1
                break;
            case MONTHLY:
                daysToAdd = 30; // approx 1 month
                break;
            case QUARTERLY:
                daysToAdd = 91; // approx 3 months
                break;
            case YEARLY:
                daysToAdd = 365;
                break;
            default:
                daysToAdd = 0;
        }
        return new Date(start.getTime() + TimeUnit.DAYS.toMillis(daysToAdd));
    }
    public void updateEndDate() {
        if (model.getStartDate() != null && model.getType() != null) {
            model.setEndDate(calculateEndDate(model.getType(), model.getStartDate()));
        }
    }


}
