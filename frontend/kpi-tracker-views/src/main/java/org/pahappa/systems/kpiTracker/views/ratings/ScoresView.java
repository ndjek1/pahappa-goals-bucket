package org.pahappa.systems.kpiTracker.views.ratings;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.core.services.ratings.CompanyValueRatingService;
import org.pahappa.systems.kpiTracker.core.services.ratings.KeeperTestRatingService;
import org.pahappa.systems.kpiTracker.core.services.ratings.PeerRatingService;
import org.pahappa.systems.kpiTracker.models.rating.CompanyValue;
import org.pahappa.systems.kpiTracker.models.rating.KeeperTest;
import org.pahappa.systems.kpiTracker.models.rating.PeerRating;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// FIX: Added annotations to make this a JSF View Scoped Bean named 'scoresView'
@ManagedBean(name = "scoresView")
@ViewScoped
@Getter
@Setter
public class ScoresView implements Serializable {
    private static final long serialVersionUID = 1L;
    private CompanyValueRatingService companyValueRatingService;
    private KeeperTestRatingService keeperTestRatingService;
    private PeerRatingService peerRatingService;

    private List<CompanyValue> companyValueRatings;
    private List<KeeperTest> keeperTestRatings;
    private List<PeerRating> peerRatings;
    private ReviewCycleService reviewCycleService;
    private ReviewCycle activeReviewCycle;

    @PostConstruct
    public void init() {
        this.companyValueRatingService = ApplicationContextProvider.getBean(CompanyValueRatingService.class);
        this.keeperTestRatingService = ApplicationContextProvider.getBean(KeeperTestRatingService.class);
        this.peerRatingService = ApplicationContextProvider.getBean(PeerRatingService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);

        loadActiveReviewCycle();
        loadData();
    }

    public void loadData() {
        // Guard clause: if there's no active cycle, do nothing and ensure lists are empty.
        if (this.activeReviewCycle == null) {
            this.companyValueRatings = Collections.emptyList();
            this.keeperTestRatings = Collections.emptyList();
            this.peerRatings = Collections.emptyList();
            return;
        }

        if (this.companyValueRatingService != null) {
            Search search = new Search(CompanyValue.class);
            search.addFilterEqual("reviewCycle.id", this.activeReviewCycle.getId());
            this.companyValueRatings = companyValueRatingService.getInstances(search, 0, 0);
        }

        if (this.keeperTestRatingService != null) {
            Search search = new Search(KeeperTest.class);
            search.addFilterEqual("reviewCycle.id", this.activeReviewCycle.getId());
            this.keeperTestRatings = keeperTestRatingService.getInstances(search, 0, 0);
        }

        if (this.peerRatingService != null) {
            Search search = new Search(PeerRating.class);
            search.addFilterEqual("reviewCycle.id", this.activeReviewCycle.getId());
            this.peerRatings = peerRatingService.getInstances(search, 0, 0);
        }
    }

    public void loadActiveReviewCycle() {
        this.activeReviewCycle = this.reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
    }
}