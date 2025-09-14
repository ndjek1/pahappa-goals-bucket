package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleType;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
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
        if (model.getTitle() == null || model.getType() == null || model.getStartDate() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Missing Information",
                            "Please provide review cycle title, type and start date."));
            return;
        }

        // Check duplicate title
        Search titleSearch = new Search(ReviewCycle.class);
        titleSearch.addFilterILike("title", model.getTitle().trim());
        titleSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        List<ReviewCycle> existingCycles = reviewCycleService.getInstances(titleSearch, 0, 0);

        boolean duplicateTitle = existingCycles.stream()
                .anyMatch(cycle -> !cycle.getId().equals(model.getId()));

        if (duplicateTitle) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Duplicate Title",
                            "A review cycle with this title already exists."));
            return;
        }

        // Auto-set end date
        model.setEndDate(calculateEndDate(model.getType(), model.getStartDate()));

        // Check for date overlaps
        Search allCyclesSearch = new Search(ReviewCycle.class);
        allCyclesSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        List<ReviewCycle> allCycles = reviewCycleService.getInstances(allCyclesSearch, 0, 0);
        for (ReviewCycle cycle : allCycles) {
            if (model.getId() != null && model.getId().equals(cycle.getId())) continue; // skip self when editing

            if (datesOverlap(model.getStartDate(), model.getEndDate(),
                    cycle.getStartDate(), cycle.getEndDate())) {

                Date suggestedDate = new Date(cycle.getEndDate().getTime() + TimeUnit.DAYS.toMillis(1));

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Date Conflict",
                                "The selected start date overlaps with the cycle '" + cycle.getTitle() +
                                        "' (" + cycle.getStartDate() + " to " + cycle.getEndDate() + "). " +
                                        "Please choose a start date on or after " + suggestedDate + "."));
                return;
            }
        }

        // Ensure only one ACTIVE cycle
        if (model.getStatus() == ReviewCycleStatus.ACTIVE) {
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

        // Save
        reviewCycleService.saveInstance(model);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Action Successful",
                        "Review cycle saved successfully."));

        super.resetModal();
        hide();
    }

    private boolean datesOverlap(Date start1, Date end1, Date start2, Date end2) {
        return (start1.before(end2) || start1.equals(end2)) &&
                (end1.after(start2) || end1.equals(start2));
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
