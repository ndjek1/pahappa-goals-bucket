package org.pahappa.systems.kpiTracker.core.services.impl;

import org.sers.webutils.server.core.utils.ApplicationSettingsUtils;
import org.sers.webutils.server.core.utils.DefaultApplicationSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomApplicationSettings extends DefaultApplicationSettings {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final NumberFormat DOUBLE_FORMATTER = new DecimalFormat("##,###,###,###.0000");
	private static final NumberFormat INTEGER_FORMATER = new DecimalFormat("##,###,###,###");
	private static final boolean EXECUTE_BACKGROUND_TASKS = true;
	public static final String BG_JOBS_CHECKSUM = "Continuum";


	@PostConstruct
	public void init() {
		ApplicationSettingsUtils.Util.createApplicationSettings(this);
		super.setExecuteBackgroundJobs(EXECUTE_BACKGROUND_TASKS);
		super.setBgJobsCheckSum(BG_JOBS_CHECKSUM);
		log.info(this.getClass().getName() + " - Initialized default application settings");
	}


	@Override
	public String getOrganizationName() {
		return "Pahappa Ltd";
	}
	@Override
	public int getMaximumRecordsPerPage() {
		return 10;
	}
	@Override
	public List<String> getErrorMessageRecipients() {
		return Arrays.asList(new String[] {"fred@pahappa.com" });
	}


	@Override
	public String getDefaultMailSenderAddress() {
		//EmailServiceImpl already handles the "from" address
		return "no-reply@kpitracker.com";
	}
	@Override
	public String getDefaultMailSenderPassword() {
		//Not used by AWS SES
		return null;
	}
	@Override
	public String getDefaultMailSenderSmtpHost() {
		//Not used by AWS SES
		return null;
	}
	@Override
	public String getDefaultMailSenderSmtpPort() {
		//Not used by AWS SES
		return null;
	}
	@Override
	public String getDefaultClientFeedbackMail() {
		return "xyz@gmail.com";
	}
	@Override
	public String getDefaultSuperUserEmail() {
		return "leonard@pahappa.com";
	}
	@Override
	public String getDefaultSuperUserPhoneNumber() {
		return "256700000000";
	}
	@Override
	public NumberFormat getDoubleNumberFormater() {
		return DOUBLE_FORMATTER;
	}
	@Override
	public NumberFormat getIntegerNumberFormater() {
		return INTEGER_FORMATER;
	}
	@Override
	public String getPasswordChangeToken() {
		return super.passwordChangeToken;
	}
}
