/**
 * Copyright 2013 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epickur.api.utils.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cribbstechnologies.clients.mandrill.exception.RequestFailedException;
import com.cribbstechnologies.clients.mandrill.model.MandrillHtmlMessage;
import com.cribbstechnologies.clients.mandrill.model.MandrillMessageRequest;
import com.cribbstechnologies.clients.mandrill.model.MandrillRecipient;
import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.cribbstechnologies.clients.mandrill.request.MandrillRESTRequest;
import com.cribbstechnologies.clients.mandrill.util.MandrillConfiguration;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is used to send emails
 * 
 * @author cph
 * 
 */
public final class Email {

	/** The logger **/
	private static final Logger LOG = LogManager.getLogger(Email.class.getSimpleName());
	/** The request **/
	private MandrillRESTRequest request;
	/** The message request **/
	private MandrillMessagesRequest messagesRequest;
	/** The email address **/
	private String fromEmail;
	/** The name of the sender **/
	private String fromName;
	/** The subject **/
	private String subject;
	/** The message in HTML format **/
	private String message;
	/** The list of sender **/
	private String[] sendTo;

	/**
	 * @param emailSubjectTxt
	 *            The subject
	 * @param emailMsgTxt
	 *            The content of the message in HTML
	 * @param sendTo
	 *            The list of email to send to
	 */
	private Email(final String emailSubjectTxt, final String emailMsgTxt, final String[] sendTo) {
		this.subject = emailSubjectTxt;
		this.message = emailMsgTxt;
		this.sendTo = sendTo;
		Properties props = Utils.getEpickurProperties();
		this.fromEmail = props.getProperty("email.mandrill.from");
		this.fromName = props.getProperty("email.mandrill.from.username");
		this.request = new MandrillRESTRequest();
		this.messagesRequest = new MandrillMessagesRequest();
		ObjectMapper mapper = new ObjectMapper();
		MandrillConfiguration config = new MandrillConfiguration();
		config.setApiKey(props.getProperty("email.mandrill.key"));
		config.setApiVersion(props.getProperty("email.mandrill.version"));
		config.setBaseURL(props.getProperty("email.mandrill.url"));
		this.request.setConfig(config);
		this.request.setObjectMapper(mapper);
		this.messagesRequest.setRequest(request);
	}

	/**
	 * Actual send the email
	 */
	private void send() {
		HttpClient httpClient = HttpClientBuilder.create().build();
		request.setHttpClient(httpClient);
		MandrillMessageRequest mmr = new MandrillMessageRequest();
		MandrillHtmlMessage mess = new MandrillHtmlMessage();
		Map<String, String> headers = new HashMap<String, String>();
		mess.setFrom_email(fromEmail);
		mess.setFrom_name(fromName);
		mess.setHeaders(headers);
		mess.setHtml(this.message);
		mess.setSubject(this.subject);
		MandrillRecipient[] recipients = new MandrillRecipient[sendTo.length];
		for (int i = 0; i < sendTo.length; i++) {
			recipients[i] = new MandrillRecipient(null, sendTo[i]);
		}
		mess.setTo(recipients);
		mess.setTrack_clicks(true);
		String[] tags = new String[] { "bmc", "bemychef", "be my chef" };
		mess.setTags(tags);
		mmr.setMessage(mess);
		try {
			messagesRequest.sendMessage(mmr);
		} catch (RequestFailedException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Static access to send a mail
	 * 
	 * @param emailSubjectTxt
	 *            the email subject
	 * @param emailMsgTxt
	 *            the email content
	 * @param sendTo
	 *            the recipients
	 */
	protected static void sendMail(final String emailSubjectTxt, final String emailMsgTxt, final String[] sendTo) {
		new Email(emailSubjectTxt, emailMsgTxt, sendTo).send();
	}

	/**
	 * Send emails
	 * 
	 * @param emailType
	 *            The Email Type
	 * @param data
	 *            The data
	 * @param sendTo
	 *            An array of email
	 */
	protected static void sendMail(final EmailType emailType, final Map<String, String> data, final String[] sendTo) {
		EmailTemplate emailTemplate = EmailTemplate.getInstance();
		Map<String, String> template = emailTemplate.getTemplate(emailType);
		if (!template.isEmpty()) {
			String subject = template.get("subject");
			String content = template.get("content");
			for (Entry<String, String> entry : data.entrySet()) {
				subject = StringUtils.replace(subject, entry.getKey(), entry.getValue());
				content = StringUtils.replace(content, entry.getKey(), entry.getValue());
			}
			sendMail(subject, content, sendTo);
		} else {
			LOG.error("Error while trying to access the email templates for: " + emailType);
		}
	}

/*	public static void main(String[] args) throws IOException {
		Map<String, String> emailData = EmailTemplate.convertToDataRegistration("carl","SJDSJAHDOIHWOHDLKJLKDWJLK");
		Email.sendMail(EmailType.REGISTRATION_USER, emailData, new String[] { "cp.harmant@gmail.com" });
	}*/
}