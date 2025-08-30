package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.models.systemSetup.enums.RatingCategory;
import org.sers.webutils.model.Gender;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("ratingCategoryConverter")
public class RatingCategoryConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

        if (arg2.equalsIgnoreCase(RatingCategory.SUPERVISOR.getDisplayName())) {
            return RatingCategory.SUPERVISOR;
        }

        if (arg2.equalsIgnoreCase(RatingCategory.PEER.getDisplayName())) {
            return RatingCategory.PEER;
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
        if (object == null || object instanceof String)
            return null;
        return ((Gender) object).getName();
    }
}
