package org.pahappa.systems.kpiTracker.core.services.systemSetupService.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReviewCycleServiceImpl extends GenericServiceImpl<ReviewCycle> implements ReviewCycleService {

    @Override
    public ReviewCycle saveInstance(ReviewCycle entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(ReviewCycle instance) throws OperationFailedException {
        return true;
    }

    @Override
    public Object getObjectById(String id) {
        return super.getInstanceByID(id);
    }

    public void updateReviewCycleStatuses() {
        Date today = new Date();

        // 1. End expired cycles
        List<ReviewCycle> activeCycles = this.getInstances(
                new Search().addFilterEqual("status", ReviewCycleStatus.ACTIVE), 0, 0);
        for (ReviewCycle cycle : activeCycles) {
            if (cycle.getEndDate() != null && cycle.getEndDate().before(today)) {
                cycle.setStatus(ReviewCycleStatus.ENDED);
                try {
                    saveInstance(cycle);
                } catch (ValidationFailedException | OperationFailedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // 2. Activate new cycles if startDate has reached
        List<ReviewCycle> draftCycles = this.getInstances(
                new Search().addFilterEqual("status", ReviewCycleStatus.DRAFT), 0, 0);
        for (ReviewCycle cycle : draftCycles) {
            if (cycle.getStartDate() != null && !cycle.getStartDate().after(today)) {
                // Ensure no other cycle is active
                boolean hasActive = this.countInstances(
                        new Search().addFilterEqual("status", ReviewCycleStatus.ACTIVE)
                ) > 0;

                if (!hasActive) {
                    cycle.setStatus(ReviewCycleStatus.ACTIVE);
                    try {
                        saveInstance(cycle);
                    } catch (ValidationFailedException | OperationFailedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
