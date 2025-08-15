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
    public void initializeDefaultOrgFitCategories1() {
        createIfNotExists("Team Rating", "Collaboration, Communication, etc.", 5.0);
        createIfNotExists("Company Values", "Self-management, Resource management, etc.", 20.0);
        createIfNotExists( "Keeper Test", "Competence, Growth, Innovation", 15.0);
    }

    private void createIfNotExists(String name, String description, double weight) {
        if (orgFitCategory.getAllInstances().isEmpty()) {
            OrgFitCategory category = new OrgFitCategory();
            category.setName(name);
            category.setDescription(description);
            category.setWeight(weight);
            category.setRecordStatus(RecordStatus.ACTIVE);
            category.setDateCreated(new Date());
            category.setDateChanged(new Date());
            orgFitCategory.mergeBG(category);
        }
    }
}
