package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("organizationGoalConverter")
public class OrganizationGoalConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        if (arg2 == null || arg2.isEmpty())
            return null;
        return ApplicationContextProvider.getBean(OrganizationGoalService.class)
                .getObjectById(arg2);
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
        if (object == null || object instanceof String)
            return null;

        return ((OrganizationGoal) object).getId();
    }
}
