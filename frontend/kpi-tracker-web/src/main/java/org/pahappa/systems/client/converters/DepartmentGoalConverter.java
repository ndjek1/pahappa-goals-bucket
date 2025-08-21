package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("departmentGoalConverter")
public class DepartmentGoalConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            // For now, return the string value. In a real implementation, you might want to look up the goal by ID
            // This is a simplified version - you may need to inject a service to do proper lookup
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof DepartmentGoal) {
            return ((DepartmentGoal) value).getName();
        }
        return value.toString();
    }
}
