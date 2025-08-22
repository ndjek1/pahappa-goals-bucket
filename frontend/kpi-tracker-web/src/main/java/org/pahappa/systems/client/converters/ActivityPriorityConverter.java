package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityPriority;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("activityPriorityConverter")
public class ActivityPriorityConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return ActivityPriority.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof ActivityPriority) {
            return ((ActivityPriority) value).name();
        }
        return value.toString();
    }
}
