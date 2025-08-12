package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleType;
import org.sers.webutils.model.Gender;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("reviewCycleTypeConverter")
public class ReviewCycleTypeConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

        if (arg2.equalsIgnoreCase(ReviewCycleType.QUARTERLY.name())) {
            return ReviewCycleType.QUARTERLY;
        }

        if (arg2.equalsIgnoreCase(ReviewCycleType.WEEKLY.name())) {
            return ReviewCycleType.WEEKLY;
        }

        if (arg2.equalsIgnoreCase(ReviewCycleType.MONTHLY.name())) {
            return ReviewCycleType.MONTHLY;
        }
        if (arg2.equalsIgnoreCase(ReviewCycleType.YEARLY.name())) {
            return ReviewCycleType.YEARLY;
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
        if (object == null || object instanceof String)
            return null;
        return ((ReviewCycleType) object).name();
    }
}
