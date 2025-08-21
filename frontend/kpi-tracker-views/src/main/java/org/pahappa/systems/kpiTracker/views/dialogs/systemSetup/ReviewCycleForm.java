package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleType;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.RecordStatus;
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
    private List<ReviewCycleStatus> reviewCycleStatusList = new ArrayList<>();

    public ReviewCycleForm() {
        super(HyperLinks.REVIEW_CYCLE_DIALOG, 500, 400);
    }

    @PostConstruct
    public void init() {
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.reviewCycleTypes = Arrays.asList(ReviewCycleType.values());
        this.reviewCycleStatusList = Arrays.asList(ReviewCycleStatus.values());
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
        Search search = new Search();
        search.addFilterAnd(
                Filter.equal("type", model.getType() ),
                Filter.equal("status", ReviewCycleStatus.ACTIVE),
                Filter.equal("recordStatus", RecordStatus.ACTIVE)
        );
        List<ReviewCycle> reviewCycleList = this.reviewCycleService.getInstances(search,0,0);

        if (reviewCycleList.isEmpty()) {
            // Auto-set end date based on type
            model.setEndDate(calculateEndDate(model.getType(), model.getStartDate()));
            reviewCycleService.saveInstance(super.model);
        }else {
            UiUtils.ComposeFailure("Duplicate review cycles", "Cannot have two active review cycles of the same type");
            return;
        }

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
