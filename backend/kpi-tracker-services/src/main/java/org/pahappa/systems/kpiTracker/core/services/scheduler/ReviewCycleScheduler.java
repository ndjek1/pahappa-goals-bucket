package org.pahappa.systems.kpiTracker.core.services.scheduler;

import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewCycleScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewCycleScheduler.class);

    @Autowired
    private ReviewCycleService reviewCycleService;

    // Run every minute for testing
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoUpdateCycles() {
        System.out.println("ReviewCycleScheduler triggered...");
        try {
            reviewCycleService.updateReviewCycleStatuses();
            System.out.println(" Review cycle statuses updated successfully.");
        } catch (Exception e) {
            System.out.println(" Error while updating review cycles: "+ e);
        }
    }
}


