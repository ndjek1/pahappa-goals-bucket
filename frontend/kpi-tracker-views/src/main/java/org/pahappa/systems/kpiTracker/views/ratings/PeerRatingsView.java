package org.pahappa.systems.kpiTracker.views.ratings;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ratings.PeerRatingService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "peerRatingsView")
@Getter
@Setter
@ViewScoped
public class PeerRatingsView implements Serializable {
    private static final long serialVersionUID = 1L;
    private PeerRatingService peerRatingService;
    private StaffService staffService;
    private Team currentTeam;
    private User loggedInUser;
    private Staff loggedInStaff;

    private List<Staff> staffList;

    @PostConstruct
    public void init() {
        this.peerRatingService = ApplicationContextProvider.getBean(PeerRatingService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.loggedInUser = SharedAppData.getLoggedInUser();
        this.staffList = new ArrayList<>();
        loadTeam();
        loadData();
    }



    public void loadTeam() {
        if (this.loggedInUser != null ) {
            this.currentTeam = findStaff(loggedInUser).getTeam();
        }
    }

    public void loadData(){
        this.staffList = new ArrayList<>(); // 👈 reinitialize each time
        if(this.currentTeam != null){
            this.staffList.addAll(findStaffByTeam(this.currentTeam));
        }
    }


    public Staff findStaff(User user){
        if (user == null) {
            return null; // or throw a custom exception if this should never happen
        }
        return staffService.searchUniqueByPropertyEqual("user.id", user.getId());
    }



    public List<Staff> findStaffByTeam(Team team){
        Search search = new Search(Staff.class);
        search.addFilterAnd(
                Filter.equal("recordStatus",RecordStatus.ACTIVE),
                Filter.equal("team.id",team.getId())
        );
        return staffService.getInstances(search,0,0);
    }
}
