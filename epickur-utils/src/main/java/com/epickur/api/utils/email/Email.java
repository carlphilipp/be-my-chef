/**
 * Copyright 2013 Carl-Philipp Harmant
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epickur.api.utils.email;

import com.cribbstechnologies.clients.mandrill.exception.RequestFailedException;
import com.cribbstechnologies.clients.mandrill.model.MandrillHtmlMessage;
import com.cribbstechnologies.clients.mandrill.model.MandrillMessageRequest;
import com.cribbstechnologies.clients.mandrill.model.MandrillRecipient;
import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.cribbstechnologies.clients.mandrill.request.MandrillRESTRequest;
import com.cribbstechnologies.clients.mandrill.util.MandrillConfiguration;
import com.epickur.api.config.EpickurProperties;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class is used to send emails
 *
 * @author cph
 */
@Component
public class Email {

	/**
	 * The logger
	 */
	private static final Logger LOG = LogManager.getLogger(Email.class.getSimpleName());

	/**
	 * The request
	 */
	private MandrillRESTRequest request;
	private String subject;
	/**
	 * The message in HTML format
	 */
	private String message;
	/**
	 * The list of sender
	 */
	private String[] sendTo;
	@Autowired
	private EmailTemplate emailTemplate;
	@Autowired
	public EpickurProperties properties;
	@Autowired
	private Utils utils;
	@Autowired
	private MandrillMessagesRequest messagesRequest;
	@Autowired
	private MandrillConfiguration mandrillConfiguration;

	/**
	 * @param emailSubjectTxt The subject
	 * @param emailMsgTxt     The content of the message in HTML
	 * @param sendTo          The list of email to send to
	 */
	protected void configure(final String emailSubjectTxt, final String emailMsgTxt, final String[] sendTo) {
		this.subject = emailSubjectTxt;
		this.message = emailMsgTxt;
		this.sendTo = sendTo;
		this.request = new MandrillRESTRequest();
		final ObjectMapper mapper = new ObjectMapper();
		this.request.setConfig(mandrillConfiguration);
		this.request.setObjectMapper(mapper);
		this.messagesRequest.setRequest(request);
	}

	/**
	 * Actual send the email
	 */
	protected void send() {
		final HttpClient httpClient = HttpClientBuilder.create().build();
		request.setHttpClient(httpClient);
		final MandrillMessageRequest mmr = new MandrillMessageRequest();
		final MandrillHtmlMessage mess = new MandrillHtmlMessage();
		final Map<String, String> headers = new HashMap<>();
		mess.setFrom_email(properties.getMandrillFrom());
		mess.setFrom_name(properties.getMandrillFromUsername());
		mess.setHeaders(headers);
		mess.setHtml(this.message);
		mess.setSubject(this.subject);
		final MandrillRecipient[] recipients = new MandrillRecipient[sendTo.length];
		for (int i = 0; i < sendTo.length; i++) {
			recipients[i] = new MandrillRecipient(null, sendTo[i]);
		}
		mess.setTo(recipients);
		mess.setTrack_clicks(true);
		final String[] tags = new String[] { "bmc", "bemychef", "be my chef" };
		mess.setTags(tags);
		mmr.setMessage(mess);
		if (properties.getSend()) {
			try {
				messagesRequest.sendMessage(mmr);
			} catch (RequestFailedException e) {
				LOG.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * Send emails
	 *
	 * @param emailType The Email Type
	 * @param data      The data
	 * @param sendTo    An array of email
	 */
	protected void sendMail(final EmailType emailType, final Map<String, String> data, final String[] sendTo) {
		final Map<String, String> template = emailTemplate.getTemplate(emailType);
		if (!template.isEmpty()) {
			String subject = template.get("subject");
			String content = template.get("content");
			for (final Entry<String, String> entry : data.entrySet()) {
				subject = StringUtils.replace(subject, entry.getKey(), entry.getValue());
				content = StringUtils.replace(content, entry.getKey(), entry.getValue());
			}
			configure(subject, content, sendTo);
			send();
		} else {
			LOG.error("Error while trying to access the email templates for: " + emailType);
		}
	}
}