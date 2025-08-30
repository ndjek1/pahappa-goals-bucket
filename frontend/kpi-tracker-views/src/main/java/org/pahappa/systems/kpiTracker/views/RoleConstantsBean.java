package org.pahappa.systems.kpiTracker.views;

import org.pahappa.systems.kpiTracker.models.security.RoleConstants;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name="roles")
@ApplicationScoped
public class RoleConstantsBean {

    public String getROLE_CEO() {
        return RoleConstants.ROLE_CEO;
    }

    public String getROLE_HR() {
        return RoleConstants.ROLE_HR;
    }

    public String getROLE_DEPARTMENT_LEAD() {
        return RoleConstants.ROLE_DEPARTMENT_LEAD;
    }

    public String getROLE_TEAM_LEAD() {
        return RoleConstants.ROLE_TEAM_LEAD;
    }

    public String getROLE_INDIVIDUAL() {
        return RoleConstants.ROLE_INDIVIDUAL;
    }

    // Add ROLE_ADMINISTRATOR too if you have it elsewhere
    public String getROLE_ADMINISTRATOR() {
        return "ROLE_ADMINISTRATOR";
    }
}

