package org.pahappa.systems.kpiTracker.core.services.emails.impl;


import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.core.services.emails.EmailService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.model.exception.OperationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import javax.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service("emailService")
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class.getName());

    @Autowired
    private MailSettingService mailSettingService;

    private SesClient sesClient;
    private String fromEmail; // Storing the "fromEmail" after fetching settings



    /**
     * Initializing the AWS SES (simple email service) client on startup by fetching credentials from the
     * database via the MailSettingService.
     */

    @PostConstruct
    public void reinitialize() {
        try {

            MailSetting settings = mailSettingService.getMailSetting();

            if(settings!=null){
                this.fromEmail = settings.getFromEmail(); //Setting the fromEmail for later use

                // the StringCryptoConverter automatically decrypts these values
                String accessKeyId = settings.getAccessKeyId();
                String secretAccessKey = settings.getSecretAccessKey();

                AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);


                this.sesClient = SesClient.builder()
                        .region(Region.of(settings.getAwsRegion()))
                        .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .build();

                LOGGER.info("AWS SES Client initialized successfully.");
                testSESConnection();
            } else {
                this.sesClient = null;
                LOGGER.severe("FATAL: No mail settings found in the database.");
            }
        } catch (Exception e){
            this.sesClient = null; //Ensure client is null on failure
            LOGGER.log(Level.SEVERE, "FATAL: AWS SES Client failed to initialize. Emails will not be sent.", e);
        }
    }

    private void testSESConnection() {
        try {
            GetSendQuotaResponse quota = sesClient.getSendQuota();
            LOGGER.info("SES connection test successful. Max 24hr send: " + quota.max24HourSend());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "SES connection test failed", e);
        }
    }


    /**
     * Sends the emails using AWS Simple Email Service
     *
     * @param to Recipient's email address.
     * @param subject The subject of the email.
     * @param body The content of the email.
     * @throws OperationFailedException if the email service is not available or fails to send the email
     */

    @Override
    public void sendEmail(String to, String subject, String body) throws OperationFailedException {
        if (this.sesClient == null){
            LOGGER.severe("Email not sent because SEService is unavailable. Check initialization logs..");
            throw new OperationFailedException("Email service isn't configured: Please contact the system administrator");
        }
        try{
            //create the send email request object
            Destination destination = Destination.builder()
                    .toAddresses(to)
                    .build();

            Content subjectContent = Content.builder().data(subject).charset("UTF-8").build();
            Content bodyContent = Content.builder().data(body).charset("UTF-8").build();
            Body emailBody = Body.builder().text(bodyContent).build();

            Message message = Message.builder()
                    .subject(subjectContent)
                    .body(emailBody)
                    .build();

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(this.fromEmail)
                    .destination(destination)
                    .message(message)
                    .build();

            //send the email
            sesClient.sendEmail(request);
            LOGGER.log(Level.INFO, "Successfully sent email to: {0}", to);

        } catch(Exception ex){
            LOGGER.log(Level.SEVERE, "Failed to send email to "+ to, ex);
            throw new OperationFailedException("The system could not send the email: "+ ex.getMessage());
        }
    }
}

