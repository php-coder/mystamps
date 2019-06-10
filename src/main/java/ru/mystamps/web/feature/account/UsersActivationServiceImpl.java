/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.feature.account;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.common.LocaleUtils;
import ru.mystamps.web.feature.site.MailService;
import ru.mystamps.web.support.togglz.Features;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class UsersActivationServiceImpl implements UsersActivationService {
	
	private final Logger log;
	private final UsersActivationDao usersActivationDao;
	private final MailService mailService;
	
	@Override
	@Transactional
	public void add(RegisterAccountDto dto, Locale lang) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getEmail() != null, "Email must be non null");
		
		AddUsersActivationDbDto activation = new AddUsersActivationDbDto();
		
		activation.setActivationKey(generateActivationKey());
		activation.setEmail(dto.getEmail());
		activation.setLang(LocaleUtils.getLanguageOrDefault(lang, "en"));
		activation.setCreatedAt(new Date());
		usersActivationDao.add(activation);
		
		log.info("Users activation has been created ({})", activation);
		
		if (Features.SEND_ACTIVATION_MAIL.isActive()) {
			mailService.sendActivationKeyToUser(new SendUsersActivationDto(activation));
		}
	}
	
	@Override
	@Transactional
	public void remove(String activationKey) {
		Validate.isTrue(activationKey != null, "Activation key must be non null");
		
		usersActivationDao.removeByActivationKey(activationKey);
		
		log.info("Users activation '{}' has been deleted", activationKey);
	}
	
	@Override
	@Transactional(readOnly = true)
	public UsersActivationDto findByActivationKey(String activationKey) {
		Validate.isTrue(activationKey != null, "Activation key must be non null");
		
		return usersActivationDao.findByActivationKey(activationKey);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UsersActivationFullDto> findOlderThan(int days) {
		Validate.isTrue(days > 0, "Days must be greater than zero");
		
		Date expiredSince = DateUtils.addDays(new Date(), -days);
		
		return usersActivationDao.findOlderThan(expiredSince);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByActivationKey(String activationKey) {
		Validate.isTrue(activationKey != null, "Activation key must be non null");
		
		return usersActivationDao.countByActivationKey(activationKey);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countCreatedSince(Date date) {
		Validate.isTrue(date != null, "Date must be non null");
		
		return usersActivationDao.countCreatedSince(date);
	}
	
	/**
	 * Generates activation key.
	 * @return string which contains numbers and letters in lower case
	 *         in 10 characters length
	 **/
	private static String generateActivationKey() {
		int actKeyLength = AccountValidation.ACT_KEY_LENGTH;
		return RandomStringUtils.randomAlphanumeric(actKeyLength).toLowerCase(Locale.ENGLISH);
	}
	
}
