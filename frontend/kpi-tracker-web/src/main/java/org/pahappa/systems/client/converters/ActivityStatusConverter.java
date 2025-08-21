package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("activityStatusConverter")
public class ActivityStatusConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return ActivityStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof ActivityStatus) {
            return ((ActivityStatus) value).name();
        }
        return value.toString();
    }
}
