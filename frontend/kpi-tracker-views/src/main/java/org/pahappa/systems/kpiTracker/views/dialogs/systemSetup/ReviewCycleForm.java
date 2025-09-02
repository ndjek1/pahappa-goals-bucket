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
import javax.faces.bean.ViewScoped;
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
        super(HyperLinks.REVIEW_CYCLE_DIALOG, 500, 300);
    }

    @PostConstruct
    public void init() {
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.reviewCycleTypes = Arrays.asList(ReviewCycleType.values());
        this.reviewCycleStatusList = Arrays.asList(ReviewCycleStatus.values());
    }

    @Override
    public void persist() throws Exception {
        if (model.getTitle() == null || model.getType() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Missing Information",
                            "Please provide both review cycle title and type."));
            return;
        }


        Search titleSearch = new Search(ReviewCycle.class);
        titleSearch.addFilterILike("title", model.getTitle().trim());
        titleSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        List<ReviewCycle> existingCycles = reviewCycleService.getInstances(titleSearch, 0, 0);
        boolean duplicateTitle = existingCycles.stream()
                .anyMatch(cycle -> !cycle.getId().equals(model.getId())); // exclude self when editing

        if (duplicateTitle) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Duplicate Title",
                            "A review cycle with this title already exists."));
            return;
        }


        model.setEndDate(calculateEndDate(model.getType(), model.getStartDate()));

        if (model.getStatus() == ReviewCycleStatus.ACTIVE) {
            // Check for another ACTIVE cycle
            Search activeSearch = new Search(ReviewCycle.class);
            activeSearch.addFilterAnd(
                    Filter.equal("status", ReviewCycleStatus.ACTIVE),
                    Filter.equal("recordStatus", RecordStatus.ACTIVE)
            );

            List<ReviewCycle> activeCycles = reviewCycleService.getInstances(activeSearch, 0, 0);

            if (!activeCycles.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Activation Denied",
                                "Another active review cycle already exists."));
                return;
            }
        }


        reviewCycleService.saveInstance(model);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Action Successful",
                        "Review cycle saved successfully."));

        super.resetModal();
        hide();
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
