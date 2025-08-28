package org.pahappa.systems.kpiTracker.core.services.ratings.impl;

import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.ratings.CompanyValueRatingService;
import org.pahappa.systems.kpiTracker.models.rating.CompanyValue;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompanyValueRatingServiceImpl extends GenericServiceImpl<CompanyValue> implements CompanyValueRatingService {
    @Override
    public CompanyValue saveInstance(CompanyValue entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(CompanyValue instance) throws OperationFailedException {
        return true;
    }
}
