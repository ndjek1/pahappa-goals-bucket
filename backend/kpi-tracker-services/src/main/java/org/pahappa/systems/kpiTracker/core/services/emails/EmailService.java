package org.pahappa.systems.kpiTracker.core.services.emails;


import org.sers.webutils.model.exception.OperationFailedException;

public interface EmailService {

    /**
     * Sends an email.
     * @param to Recipient's email address.
     * @param subject The subject of the email.
     * @param body The content of the email.
     * @throws OperationFailedException if sending the email fails.
     */

    void sendEmail(String to, String subject, String body) throws OperationFailedException;
    void reinitialize();
}
