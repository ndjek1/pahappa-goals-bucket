package org.pahappa.systems.kpiTracker.views.users;

import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.RecordStatus;
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

    public RegistrationView() {
    }

    @PostConstruct
    public void init() {
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.listOfGenders = Arrays.asList(Gender.values());
        this.user = new User();
    }


    public void persist() throws ValidationFailedException {
        // You might want to add password confirmation logic here before saving
        Role role  = userService.
                getRoleByRoleName(Role.DEFAULT_WEB_ACCESS_ROLE);
        this.user.addRole(role);
        this.user.setRecordStatus(RecordStatus.ACTIVE_LOCKED);
        this.userService.saveUser(user);
        this.user = new User();
    }

    public void save() throws Exception {
        try {
            persist();
            this.user = new User();
            MessageComposer.info("Action Successful", "Your account has been created. We will notify you when the admin validates it.");
        } catch (Exception e) {
            MessageComposer.error("Action Failure", e.getMessage());
        }
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
}