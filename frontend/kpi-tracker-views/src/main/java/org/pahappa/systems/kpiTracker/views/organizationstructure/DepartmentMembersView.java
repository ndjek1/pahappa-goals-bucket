//package org.pahappa.systems.kpiTracker.views.organizationstructure;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
//import org.sers.webutils.model.security.User;
//import org.sers.webutils.server.core.service.UserService;
//import org.sers.webutils.server.core.utils.ApplicationContextProvider;
//
//import javax.faces.bean.ManagedBean;
//import javax.annotation.PostConstruct;
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
//    private List<User> departmentMembers;
//
//    @PostConstruct
//    public void init() {
//        userService = ApplicationContextProvider.getBean(UserService.class);
//    }
//
//    public void show(Department department) {
//        this.selectedDepartment = department;
//        if (department != null) {
////            this.departmentMembers = selectedDepartment.getDepartmentMembers();
//        } else {
//            this.departmentMembers = null;
//        }
//    }
//
//}
