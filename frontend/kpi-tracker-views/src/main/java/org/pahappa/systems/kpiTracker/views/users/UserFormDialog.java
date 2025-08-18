package org.pahappa.systems.kpiTracker.views.users;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.SystemUserService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.systemUsers.SystemUser;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

/**
 *
 */
@ManagedBean(name = "userFormDialog", eager = true)
@Getter
@Setter
@SessionScoped
public class UserFormDialog extends DialogForm<User> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserFormDialog.class.getSimpleName());
    private UserService userService;
    private SystemUserService systemUserService;
    private DepartmentService departmentService;
    private List<Department> departments;
    private Department selectedDepartment;

    private List<Gender> listOfGenders;
    private List<Role> databaseRoles;
    private Set<Role> userRoles;
    private boolean edit;

    @PostConstruct
    public void init() {
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.listOfGenders = Arrays.asList(Gender.values());
        this.databaseRoles = userService.getRoles();
        this.systemUserService = ApplicationContextProvider.getBean(SystemUserService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        loadDepartments();
    }

    public UserFormDialog() {
        super(HyperLinks.USER_FORM_DIALOG, 700, 450);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        // 1. Attach roles
        super.model.setRoles(userRoles);

        // 2. Save and get managed User
        User savedUser = this.userService.saveUser(super.model);

        // 3. Create SystemUser linked to managed User
        SystemUser systemUser = new SystemUser();
        systemUser.setUser(savedUser);
        systemUser.setDepartment(selectedDepartment);

        // 4. Save SystemUser
        this.systemUserService.saveInstance(systemUser);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new User();
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if(super.model != null)
            setEdit(true);
        this.userRoles = new HashSet<>(userService.getRoles(super.model, 0, 0));
    }

    public void activateSelectedUser(User user) throws ValidationFailedException {
        try {
            user.setRecordStatus(RecordStatus.ACTIVE);
            userService.saveUser(user);
            UiUtils.showMessageBox("Action successful", "User has been activated.");
        } catch (ValidationFailedException ex) {
            UiUtils.ComposeFailure("Action failed", ex.getLocalizedMessage());
            Logger.getLogger(UsersView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadDepartments(){
        this.departments = this.departmentService.getAllInstances();
    }
    public void rejectSignUp(User user) throws ValidationFailedException {
        try {
            user.setRecordStatus(RecordStatus.PERMANENTLY_DELETED);
            userService.saveUser(user);
            UiUtils.showMessageBox("Action successful", "User has been rejected.");
        } catch (ValidationFailedException ex) {
            UiUtils.ComposeFailure("Action failed", ex.getLocalizedMessage());
            Logger.getLogger(UsersView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
