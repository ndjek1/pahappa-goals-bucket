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
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "test")
@Getter
@Setter
@SessionScoped
public class PeerRatingForm extends DialogForm<PeerRating> {

    private static final long serialVersionUID = 1L;
    private Staff supervisor;
    private Staff staff;
    private ReviewCycle reviewCycle;
    private User loggedInUser;
    private StaffService staffService;
    private OrgFitCategoryService orgFitCategoryService;
    private OrgFitCategoryItemService orgFitCategoryItemService;
    private OrgFitCategory peerRating;
    private List<OrgFitCategoryItem> peerRatingItems;
    private List<ItemRating> peerRatings;


    private PeerRatingService peerRatingService;
    private ReviewCycleService reviewCycleService;
    public PeerRatingForm() {
        super(HyperLinks.PEER_RATING_FORM_DIALOG, 600, 500);
    }

    @PostConstruct
    public void init() {
        this.reviewCycleService =ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.orgFitCategoryService = ApplicationContextProvider.getBean(OrgFitCategoryService.class);
        this.orgFitCategoryItemService = ApplicationContextProvider.getBean(OrgFitCategoryItemService.class);
        this.peerRatingService =  ApplicationContextProvider.getBean(PeerRatingService.class);
        this.loggedInUser = SharedAppData.getLoggedInUser();
        loadSupervisor();
        loadActiveReviewCycle();
        loadCategories();
        loadItems();
    }

    @Override
    public void persist() throws Exception {
        if(this.supervisor != null && this.staff != null) {
            super.model.setStaff(supervisor);
            super.model.setPeer(staff);
        }
        if(this.reviewCycle != null) {
            super.model.setReviewCycle(reviewCycle);
        }
        peerRatingService.saveInstance(super.model);
        savePeerRatingScore();
        resetModal();
        hide();
    }

    public void savePeerRatingScore() throws ValidationFailedException, OperationFailedException {
        PeerRating peerRatingRating = new PeerRating();
        if(this.peerRatingService != null) {
            peerRatingRating.setStaff(this.supervisor);
            peerRatingRating.setStaff(this.staff);
            peerRatingRating.setReviewCycle(this.reviewCycle);
            peerRatingRating.setScore(this.getPeerRatingScore());
            peerRatingService.saveInstance(peerRatingRating);
        }
    }
    public void loadActiveReviewCycle() {
        this.reviewCycle = this.reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
    }
    public  void loadSupervisor(){
        if(this.loggedInUser != null) {
            this.supervisor = staffService.searchUniqueByPropertyEqual("user.id",loggedInUser.getId());
        }
    }

    public void loadCategories(){
        if(this.orgFitCategoryService != null) {
            this.peerRating = orgFitCategoryService.searchUniqueByPropertyEqual("ratingCategory", OrgFitCategoryType.KEEPER_TEST);
        }
    }


    public void loadItems(){
        if(this.peerRating != null) {
            Search search = new Search(OrgFitCategoryItem.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus", RecordStatus.ACTIVE),
                    Filter.equal("orgFitCategory.id",peerRating.getId())
            );
            this.peerRatingItems = orgFitCategoryItemService.getInstances(search,0,0);
            // Wrap into ItemRatings
            this.peerRatings = peerRatingItems.stream()
                    .map(item -> new ItemRating(item, null)) // rating initially null
                    .collect(Collectors.toList());
        }

    }


    public int getPeerRatingScore() {
        int total = peerRatings.stream()
                .filter(ir -> ir.getRating() != null)
                .mapToInt(ItemRating::getRating)
                .sum();

        return Math.round((float) total / peerRatingItems.size());
    }



    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new PeerRating();
    }
}
