package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "reviewCycleConverter")
public class ReviewCycleConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return ApplicationContextProvider.getBean(ReviewCycleService.class)
                .getObjectById(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if (object == null || object instanceof String)
            return null;
        return ((ReviewCycle) object).getId();
    }
}

