package org.pahappa.systems.kpiTracker.core.services.ratings.impl;

import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.ratings.SelfAssessmentService;
import org.pahappa.systems.kpiTracker.models.rating.PeerRating;
import org.pahappa.systems.kpiTracker.models.rating.SelfAssessment;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SelfAssessmentServiceImpl extends GenericServiceImpl<SelfAssessment> implements SelfAssessmentService {
    @Override
    public SelfAssessment saveInstance(SelfAssessment entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(SelfAssessment instance) throws OperationFailedException {
        return true;
    }
}
