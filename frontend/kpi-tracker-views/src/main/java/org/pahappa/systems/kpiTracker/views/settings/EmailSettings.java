package org.pahappa.systems.kpiTracker.views.settings;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.core.services.emails.EmailService;
import org.pahappa.systems.kpiTracker.models.MailSetting;

import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "systemSettingsView")
@ViewScoped
@Getter
@Setter
public class EmailSettings implements Serializable {




    private MailSettingService mailSettingService;
    private MailSetting editingMailSetting = new MailSetting();

    @PostConstruct
    public void init() {

        this.mailSettingService = ApplicationContextProvider.getBean(MailSettingService.class);

        loadMailSettings();
    }







    // === Mail Settings ===
    public void loadMailSettings() {
        this.editingMailSetting = this.mailSettingService.getMailSetting();
        if (this.editingMailSetting == null) {
            this.editingMailSetting = new MailSetting();
        }
    }
    public void prepareNewMailSetting() {
        this.editingMailSetting = new MailSetting();
    }

    public void saveMailSettings() throws ValidationFailedException, OperationFailedException {
        if(this.editingMailSetting != null) {
            this.mailSettingService.saveMailSetting(editingMailSetting);
            EmailService emailService = ApplicationContextProvider.getBean(EmailService.class);
            emailService.reinitialize();
            loadMailSettings();
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Mail Settings Saved",
                            "Mail settings updated successfully.")
            );
        }


    }
}
