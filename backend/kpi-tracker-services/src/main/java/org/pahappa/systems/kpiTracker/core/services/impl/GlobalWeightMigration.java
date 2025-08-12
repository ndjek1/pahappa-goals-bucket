package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.GlobalWeightService;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.sers.webutils.model.migrations.Migration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class GlobalWeightMigration {

    @Autowired
    private GlobalWeightService globalWeightService;

    @Migration(orderNumber = 2)
    public void initializeDefaultGlobalWeight1() {
        System.out.println(">>>> Running GlobalWeight migration");
        createIfNotExists(60, 40);
    }


    private void createIfNotExists(double mboWeight, double orgFitWeight) {
        if (globalWeightService.getAllInstances().isEmpty()) {
            GlobalWeight weight = new GlobalWeight();
            weight.setMboWeight(mboWeight);
            weight.setOrgFitWeight(orgFitWeight);
            globalWeightService.mergeBG(weight);
        }
    }

}
