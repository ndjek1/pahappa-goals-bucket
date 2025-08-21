package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("departmentGoalConverter")
public class DepartmentGoalConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        if (arg2 == null || arg2.isEmpty())
            return null;
        return ApplicationContextProvider.getBean(DepartmentGoalService.class)
                .getObjectById(arg2);
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
        if (object == null || object instanceof String)
            return null;

        return ((OrganizationGoal) object).getId();
    }
}
