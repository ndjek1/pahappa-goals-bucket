package org.pahappa.systems.kpiTracker.views.ratings;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryItemService;
import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.core.services.ratings.PeerRatingService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.rating.PeerRating;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategoryItem;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.OrgFitCategoryType;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.pahappa.systems.kpiTracker.views.goals.ItemRating;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
// FIX 1: Switched to modern CDI annotations for bean management.
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


// FIX 1: Replaced @ManagedBean with @Named and @SessionScoped with the CDI equivalent.
@ManagedBean(name = "test")
@Getter
@Setter
@SessionScoped
public class PeerRatingForm extends DialogForm<PeerRating> implements Serializable { // Added Serializable

    private static final long serialVersionUID = 1L;
    private Staff supervisor;
    private Staff staff;
    private ReviewCycle reviewCycle;
    private User loggedInUser;
    private StaffService staffService;
    private OrgFitCategoryService orgFitCategoryService;
    private OrgFitCategoryItemService orgFitCategoryItemService;
    private OrgFitCategory peerRatingCategory; // Renamed for clarity
    private List<OrgFitCategoryItem> peerRatingItems;
    private List<ItemRating> peerRatings;

    private PeerRatingService peerRatingService;
    private ReviewCycleService reviewCycleService;

    public PeerRatingForm() {
        super(HyperLinks.PEER_RATING_FORM_DIALOG, 600, 500);
    }

    @PostConstruct
    public void init() {
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.orgFitCategoryService = ApplicationContextProvider.getBean(OrgFitCategoryService.class);
        this.orgFitCategoryItemService = ApplicationContextProvider.getBean(OrgFitCategoryItemService.class);
        this.peerRatingService = ApplicationContextProvider.getBean(PeerRatingService.class);
        this.loggedInUser = SharedAppData.getLoggedInUser();
        this.model = new PeerRating();
        loadSupervisor();
        loadActiveReviewCycle();
        loadCategories();
        loadItems();
    }

    // FIX 3: Consolidated and corrected the persistence logic.
    @Override
    public void persist() throws Exception {
        if (this.supervisor == null || this.staff == null || this.reviewCycle == null) {
            throw new ValidationFailedException("Cannot save rating. Supervisor, Staff, or ReviewCycle is missing.");
        }

        // Populate the single model instance with all required data
        super.model.setStaff(supervisor); // The rater
        super.model.setPeer(staff); // The one being rated
        super.model.setReviewCycle(reviewCycle);
        super.model.setScore(this.getPeerRatingScore()); // Calculate and set the score

        // Save the fully populated model once
        peerRatingService.saveInstance(super.model);

        resetModal();
        hide();
    }

    // FIX 3: Removed the buggy and redundant savePeerRatingScore() method.

    public void loadActiveReviewCycle() {
        this.reviewCycle = this.reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
    }

    public void loadSupervisor() {
        if (this.loggedInUser != null) {
            this.supervisor = staffService.searchUniqueByPropertyEqual("user.id", loggedInUser.getId());
        }
    }

    public void loadCategories() {
        if (this.orgFitCategoryService != null) {
            this.peerRatingCategory = orgFitCategoryService.searchUniqueByPropertyEqual("ratingCategory", OrgFitCategoryType.TEAM_RATING);
        }
    }

    public void loadItems() {
        if (this.peerRatingCategory != null) {
            Search search = new Search(OrgFitCategoryItem.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus", RecordStatus.ACTIVE),
                    Filter.equal("orgFitCategory.id", peerRatingCategory.getId())
            );
            this.peerRatingItems = orgFitCategoryItemService.getInstances(search, 0, 0);
            // Wrap into ItemRatings
            this.peerRatings = peerRatingItems.stream()
                    .map(item -> new ItemRating(item, 0)) // rating initially 0
                    .collect(Collectors.toList());
        } else {
            this.peerRatingItems = Collections.emptyList();
            this.peerRatings = Collections.emptyList();
        }
    }

    // Made the method more robust against division by zero.
    public int getPeerRatingScore() {
        if (peerRatings == null || peerRatings.isEmpty()) {
            return 0;
        }

        int total = peerRatings.stream()
                .filter(ir -> ir.getRating() != null)
                .mapToInt(ItemRating::getRating)
                .sum();

        // Avoid division by zero
        if (peerRatingItems.isEmpty()) {
            return 0;
        }

        return Math.round((float) total / peerRatingItems.size());
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new PeerRating();
        // Also clear the ratings list for the next user
        if(this.peerRatings != null){
            this.peerRatings.forEach(ir -> ir.setRating(0));
        }
    }

}