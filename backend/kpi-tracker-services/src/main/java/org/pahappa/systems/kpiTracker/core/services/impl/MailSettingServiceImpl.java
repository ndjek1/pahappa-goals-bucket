package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.sers.webutils.server.core.utils.MailUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("mailSettingService")
@Transactional
public class MailSettingServiceImpl extends BaseDAOImpl<MailSetting> implements MailSettingService{

	@Override
	public MailSetting saveMailSetting(MailSetting mailSetting) throws ValidationFailedException {
		if(mailSetting == null)
			throw new ValidationFailedException("MailSetting object cannot be null");

		if(StringUtils.isBlank(mailSetting.getFromEmail()))
			throw new ValidationFailedException("Missing From Email address");

		if(StringUtils.isBlank(mailSetting.getAwsRegion()))
			throw new ValidationFailedException("Missing AWS Region");

		if(StringUtils.isBlank(mailSetting.getAccessKeyId()))
			throw new ValidationFailedException("Missing AWS Access Key ID");

		if(StringUtils.isBlank(mailSetting.getSecretAccessKey()))
			throw new ValidationFailedException("Missing AWS Secret Access Key");

		return super.save(mailSetting);
	}

	@Override
	public MailSetting getMailSetting() {
		//fetching the first record because we should have one active mail configuration; for sending out of emails.
		Search search = new Search();
		search.setFirstResult(0);
		search.setMaxResults(1);
		List<MailSetting> settings = super.search(search);

		if(settings!=null && !settings.isEmpty()){
			return settings.get(0);
		}

		return null;
	}

}
