package org.pahappa.systems.kpiTracker.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pahappa.systems.kpiTracker.models.crypto.StringCryptoConverter;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;


/**
 * Settings for configuring an email service, specifically AWS SES.
 */

@Getter
@Setter
@ToString(exclude={"accessKeyId", "secretAccessKey"}) // preventing sensitive info from being printed in the logs
@Entity
@Table(name = "mail_settings")
public class MailSetting extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "from_email", nullable = false, length=100)
	private String fromEmail;

	@Column(name = "aws_region", nullable = false)
	private String awsRegion;

	/**
	 * the access key id (AWS) is encrypted; at rest in the database
	 */
	@Column(name = "access_key_id", nullable = false, length=255)
	@Convert(converter = StringCryptoConverter.class)
	private String accessKeyId;

	/**
	 * the secret access key (AWS) is encrypted; at rest in the database
	 */
	@Column(name="secret_access_key", nullable = false, length = 255)
	@Convert(converter = StringCryptoConverter.class)
	private String secretAccessKey;



	@Override
	public boolean equals(Object object) {
		return object instanceof MailSetting && (super.getId() != null) ? super.getId().equals(((MailSetting) object).getId())
				: (object == this);
	}

	@Override
	public int hashCode() {
		return super.getId() != null ? this.getClass().hashCode() + super.getId().hashCode() : super.hashCode();
	}
}
