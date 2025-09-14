package org.pahappa.systems.kpiTracker.core.services.impl;


import org.pahappa.systems.kpiTracker.core.services.systemSetupService.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.OrgFitCategoryType;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.RatingCategory;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.migrations.Migration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class OrgFitCategoryMigration {

    @Autowired
    private OrgFitCategoryService orgFitCategory;

    @Migration(orderNumber = 2)
    public void initializeDefaultOrgFitCategories3() {
        createIfNotExists("Team Rating", "Collaboration, Communication, etc.", 0.0,RatingCategory.PEER);
        createIfNotExists("Company Values", "Self-management, Resource management, etc.", 0.0,RatingCategory.SUPERVISOR);
        createIfNotExists( "Keeper Test", "Competence, Growth, Innovation", 0.0,RatingCategory.SUPERVISOR);
    }

    private void createIfNotExists(String name, String description, double weight, RatingCategory orgFitCategoryType) {
        if (orgFitCategory.getAllInstances().isEmpty()) {
            OrgFitCategory category = new OrgFitCategory();
            category.setName(name);
            category.setDescription(description);
            category.setWeight(weight);
            category.setRecordStatus(RecordStatus.ACTIVE);
            category.setRatingCategory(orgFitCategoryType);
            category.setDateCreated(new Date());
            category.setDateChanged(new Date());
            orgFitCategory.mergeBG(category);
        }
    }
}
