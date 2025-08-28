package org.pahappa.systems.kpiTracker.core.services.ratings.impl;

import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.ratings.PeerRatingService;
import org.pahappa.systems.kpiTracker.models.rating.PeerRating;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PeerRatingServiceImpl extends GenericServiceImpl<PeerRating> implements PeerRatingService {
    @Override
    public PeerRating saveInstance(PeerRating entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(PeerRating instance) throws OperationFailedException {
        return true;
    }
}
