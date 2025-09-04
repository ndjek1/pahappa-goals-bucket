package org.pahappa.systems.kpiTracker.core.services.impl;


import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.OrgFitCategoryType;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.migrations.Migration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

@Service
@Transactional
public class OrgFitCategoryMigration {

    @Autowired
    private OrgFitCategoryService orgFitCategory;

    @Migration(orderNumber = 2)
    public void initializeDefaultOrgFitCategories2() {
        createIfNotExists("Team Rating", "Collaboration, Communication, etc.", 0.0,OrgFitCategoryType.TEAM_RATING);
        createIfNotExists("Company Values", "Self-management, Resource management, etc.", 0.0,OrgFitCategoryType.COMPANY_VALUES);
        createIfNotExists( "Keeper Test", "Competence, Growth, Innovation", 0.0,OrgFitCategoryType.KEEPER_TEST);
    }

    private void createIfNotExists(String name, String description, double weight,OrgFitCategoryType orgFitCategoryType) {
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
