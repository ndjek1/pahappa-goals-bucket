//package org.pahappa.systems.kpiTracker.views.organizationstructure;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
//import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
//import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
//import org.sers.webutils.model.security.User;
//import org.sers.webutils.server.core.service.UserService;
//import org.sers.webutils.server.core.utils.ApplicationContextProvider;
//
//import javax.annotation.PostConstruct;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
//import java.io.Serializable;
//import java.util.List;
//
//@ManagedBean(name = "departmentMembersView")
//@SessionScoped
//@Getter
//@Setter
//public class DepartmentMembersView implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    private UserService userService;
//    private Department selectedDepartment;
//    private List<User> members;
//
//
//    private boolean membersDialogVisible;
//
//
//    @PostConstruct
//    public void init() {
//        userService = ApplicationContextProvider.getBean(UserService.class);
//    }
//
//    public void show(Department department) {
//        this.selectedDepartment = department;
//        if (department != null) {
//            this.members = userService.getMembersByDepartment(department);
//        } else {
//            this.members = null;
//        }
//    }
//
//}