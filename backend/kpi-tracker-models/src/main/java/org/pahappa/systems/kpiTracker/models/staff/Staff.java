package org.pahappa.systems.kpiTracker.models.staff;

import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "staffs")
public class Staff extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Department department;
    private User user;
    private Team team;

    @ManyToOne
    @JoinColumn(
            name = "department_id"
    )
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(
            name = "team_id"
    )
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Staff)) return false;
        Staff that = (Staff) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
