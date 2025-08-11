package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.model.exception.ValidationFailedException;

/**
 * Responsible for CRUD operations on {@link MailSetting}
 *
 */
public interface MailSettingService {
	/**
	 * Adds a {@link MailSetting} to the database.
	 * 
	 * @param mailSetting
	 * @throws ValidationFailedException if the following attributes are blank:
	 *               senderAddress, senderPassword, senderSmtpHost, senderSmtpPort
	 */
	MailSetting saveMailSetting(MailSetting mailSetting) throws ValidationFailedException;

	/**
	 * Gets mail settings
	 * 
	 * @return
	 */
	MailSetting getMailSetting();
}
