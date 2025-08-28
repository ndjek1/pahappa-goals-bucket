package org.pahappa.systems.kpiTracker.core.services.ratings.impl;

import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.rating.KeeperTest;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KeeperTestRatingService extends GenericServiceImpl<KeeperTest> implements org.pahappa.systems.kpiTracker.core.services.ratings.KeeperTestRatingService {
    @Override
    public KeeperTest saveInstance(KeeperTest entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(KeeperTest instance) throws OperationFailedException {
        return true;
    }
}
