package org.pahappa.systems.kpiTracker.views.users;

import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.SystemUserService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.systemUsers.SystemUser;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


@ManagedBean(name = "registrationView")
@SessionScoped
public class RegistrationView implements Serializable { // Implement Serializable

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserFormDialog.class.getSimpleName());
    private transient UserService userService; // transient is good practice for injected services
    private List<Gender> listOfGenders;
    private User user;
    private SystemUserService systemUserService;
    private DepartmentService departmentService;
    private List<Department> departments;
    private Department selectedDepartment;

    public RegistrationView() {
    }

    @PostConstruct
    public void init() {
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.listOfGenders = Arrays.asList(Gender.values());
        this.systemUserService = ApplicationContextProvider.getBean(SystemUserService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.user = new User();
        loadDepartments();
    }

    public User persist() throws ValidationFailedException {
        Role role  = userService.getRoleByRoleName(Role.DEFAULT_WEB_ACCESS_ROLE);
        this.user.addRole(role);
        this.user.setRecordStatus(RecordStatus.ACTIVE_LOCKED);
        return this.userService.saveUser(user);   // return managed entity
    }


    public void save() throws Exception {
        try {
            User savedUser = persist();
            saveSystemUser(savedUser);
            this.user = new User();      
            MessageComposer.info("Action Successful", "Your account has been created. We will notify you when the admin validates it.");
        } catch (Exception e) {
            MessageComposer.error("Action Failure", e.getMessage());
        }
    }


    public void loadDepartments(){
        this.departments = this.departmentService.getAllInstances();
    }

    public void saveSystemUser(User managedUser) throws ValidationFailedException, OperationFailedException {
        SystemUser systemUser = new SystemUser();
        systemUser.setUser(managedUser);   // ✅ ensure managed user is set
        systemUser.setDepartment(selectedDepartment);
        this.systemUserService.saveInstance(systemUser);
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Gender> getListOfGenders() {
        return listOfGenders;
    }

    public void setListOfGenders(List<Gender> listOfGenders) {
        this.listOfGenders = listOfGenders;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public Department getSelectedDepartment() {
        return selectedDepartment;
    }

    public void setSelectedDepartment(Department selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
    }
}