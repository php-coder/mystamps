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
package ru.mystamps.web.support.mailgun;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mystamps.web.service.MailgunEmail;
import ru.mystamps.web.support.togglz.Features;

@RequiredArgsConstructor
public class FallbackMailgunEmailSendingStrategy implements MailgunEmailSendingStrategy {
	
	private static final Logger LOG =
		LoggerFactory.getLogger(FallbackMailgunEmailSendingStrategy.class);
	
	private final MailgunEmailSendingStrategy primaryStrategy;
	private final MailgunEmailSendingStrategy fallbackStrategy;
	
	@Override
	public void send(MailgunEmail email) {
		boolean needFallback = usePrimaryStrategy(email);
		if (needFallback) {
			useFallbackStrategy(email);
		}
	}
	
	private boolean usePrimaryStrategy(MailgunEmail email) {
		if (!Features.SEND_MAIL_VIA_HTTP_API.isActive()) {
			return true;
		}
		
		try {
			primaryStrategy.send(email);
			return false;
			
		} catch (RuntimeException ex) { // NOPMD: AvoidCatchingGenericException; try to catch-all
			LOG.warn(
				"Couldn't send email with the primary strategy, fallback to the secondary",
				extractCause(ex)
			);
			return true;
		}
	}
	
	private void useFallbackStrategy(MailgunEmail email) {
		fallbackStrategy.send(email);
	}
	
	private static Throwable extractCause(RuntimeException ex) {
		if (ex.getCause() != null) {
			return ex.getCause();
		}
		return ex;
	}
	
}
