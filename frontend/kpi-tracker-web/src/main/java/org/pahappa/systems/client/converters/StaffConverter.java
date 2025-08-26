package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("staffConverter")
public class StaffConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        if (arg2 == null || arg2.isEmpty())
            return null;
        return (Staff) ApplicationContextProvider.getBean(StaffService.class)
                .getInstanceByID(arg2);
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
        if (object == null || object instanceof String)
            return null;

        return ((Staff) object).getId();
    }
}
