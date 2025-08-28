package org.pahappa.systems.kpiTracker.core.services.ratings.impl;

import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.ratings.SupervisorAssessmentService;
import org.pahappa.systems.kpiTracker.models.rating.SelfAssessment;
import org.pahappa.systems.kpiTracker.models.rating.SupervisorAssessment;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SupervisorAssessmentImpl extends GenericServiceImpl<SupervisorAssessment> implements SupervisorAssessmentService {
    @Override
    public SupervisorAssessment saveInstance(SupervisorAssessment entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(SupervisorAssessment instance) throws OperationFailedException {
        return true;
    }
}
