package org.pahappa.systems.kpiTracker.views;

import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.core.services.emails.EmailService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.client.views.presenters.WebFormView;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "mailSettingForm")
@ViewScoped
@ViewPath(path = HyperLinks.MAIL_SETTING_FORM)
public class MailSettingForm extends WebFormView<MailSetting, MailSettingForm, Dashboard> {


	private static final long serialVersionUID = 1L;
	private MailSettingService mailSettingService;

	@Override
	public void beanInit() {

		this.mailSettingService = ApplicationContextProvider.getBean(MailSettingService.class);

		//loading existing settings when the bean is initialized
		super.model = this.mailSettingService.getMailSetting();

		//if the settings don't exist: a new object is to be created so that the form doesn't break
		if (super.model == null){
			super.model = new MailSetting();
		}
	}

	@Override
	public void pageLoadInit() {
		// --the logic is in the beanInit method--
	}

	@Override
	public void persist() throws Exception {
		this.mailSettingService.saveMailSetting(super.model);

		//Getting the email service bean and tell it to reload the settings
		EmailService emailService = ApplicationContextProvider.getBean(EmailService.class);
		emailService.reinitialize();

		UiUtils.showMessageBox("Mail settings updated", "Action Successful");
	}

	@Override
	public void resetModal() {
		super.resetModal();
		super.model = new MailSetting();
	}

	@Override
	public void setFormProperties() {
		super.setFormProperties();
	}

	@Override
	public String getViewUrl() {
		return this.getViewPath();
	}
}
