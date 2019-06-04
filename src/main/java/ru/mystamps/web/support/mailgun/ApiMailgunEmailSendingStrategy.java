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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.mystamps.web.service.MailgunEmail;
import ru.mystamps.web.service.MailgunEmailSendingStrategy;
import ru.mystamps.web.service.exception.EmailSendingException;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

// CheckStyle: ignore LineLength for next 10 lines
/**
 * Sending e-mails with Mailgun service (via HTTP API).
 *
 * @see <a href="https://documentation.mailgun.com/en/latest/api-intro.html">API: Introduction</a>
 * @see <a href="https://documentation.mailgun.com/en/latest/api-sending.html">API: Sending</a>
 * @see <a href="https://documentation.mailgun.com/en/latest/user_manual.html#sending-via-api">API: Manual</a>
 */
public class ApiMailgunEmailSendingStrategy implements MailgunEmailSendingStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiMailgunEmailSendingStrategy.class);
	
	private final String endpoint;
	private final RestTemplate restTemplate;
	
	public ApiMailgunEmailSendingStrategy(
		RestTemplateBuilder restTemplateBuilder,
		String endpoint,
		String user,
		String password) {
		
		this.endpoint = endpoint;
		
		this.restTemplate = restTemplateBuilder
			.basicAuthorization(user, password)
			.build();
	}
	
	/*
	  This method is a roughly equivalent to the following curl command:
	
	  $ curl -s -v \
	      https://api.mailgun.net/v3/my-stamps.ru/messages \
	      --user "api:$API_KEY" \
	      -F from='My Stamps <dont-reply%my-stamps.ru>' \
	      -F to=example%example.com \
	      -F subject=Test \
	      -F text=Hello \
	      -F o:tag=test \
	      -F o:testmode=true
	
	  The response example:
	
	  < HTTP/1.1 100 Continue
	  < HTTP/1.1 200 OK
	  < Access-Control-Allow-Headers: Content-Type, x-requested-with
	  < Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
	  < Access-Control-Allow-Origin: *
	  < Access-Control-Max-Age: 600
	  < Content-Disposition: inline
	  < Content-Type: application/json
	  < Date: Tue, 07 May 2019 19:42:10 GMT
	  < Server: nginx
	  < Strict-Transport-Security: max-age=60; includeSubDomains
	  < X-Ratelimit-Limit: 1000000
	  < X-Ratelimit-Remaining: 999999
	  < X-Ratelimit-Reset: 1557258140503
	  < X-Recipient-Limit: 1000000
	  < X-Recipient-Remaining: 999999
	  < X-Recipient-Reset: 1557258140500
	  < Content-Length: 136
	  < Connection: keep-alive
	  <
	  {
	    "id": "<20190507194211.0.B96DD98C0E6AAA9A@my-stamps.ru>",
	    "message": "Queued. Thank you."
	  }
	*/
	@Override
	public void send(MailgunEmail email) {
		
		try {
			InternetAddress from =
				new InternetAddress(email.senderAddress(), email.senderName(), "UTF-8");
			
			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
			parts.add("from", from.toString());
			parts.add("to", email.recipientAddress());
			parts.add("subject", email.subject());
			parts.add("text", email.text());
			parts.add("o:tag", email.tag());
			parts.add("o:testmode", String.valueOf(email.testMode()));
			
			HttpHeaders headers = new HttpHeaders();
			headers.set(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);
			headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
			
			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(parts, headers);
			
			ResponseEntity<String> response = restTemplate.exchange(
				endpoint,
				HttpMethod.POST,
				request,
				String.class
			);
			
			LOG.info("Mailgun response code: {}", response.getStatusCode());
			LOG.debug("Mailgun response headers: {}", response.getHeaders());
			LOG.info("Mailgun response body: {}", StringUtils.remove(response.getBody(), '\n'));
			
		} catch (UnsupportedEncodingException | RestClientException ex) {
			throw new EmailSendingException("Can't send mail to " + email.recipientAddress(), ex);
		}
	}
	
}
