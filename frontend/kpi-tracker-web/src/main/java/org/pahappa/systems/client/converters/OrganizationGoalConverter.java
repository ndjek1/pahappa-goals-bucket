package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("organizationGoalConverter")
public class OrganizationGoalConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Look up the goal by ID using the service
            return ApplicationContextProvider.getBean(OrganizationGoalService.class)
                    .getInstanceByID(value);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof OrganizationGoal) {
            return ((OrganizationGoal) value).getId();
        }
        return value.toString();
    }
}
