package com.epickur.api.utils.email;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.epickur.api.utils.Utils;

public class EmailTest {
	
	private Email email;
	
	private String[] admins;
	
	@Before
	public void setUp() {
		MandrillMessagesRequest messagesRequest = new MandrillMessagesRequest();
		email = new Email(messagesRequest, true);
		String adminsProp = Utils.getEpickurProperties().getProperty("admins");
		admins = adminsProp.split(",");
	}
	
	@Test
	public void testSendRealEmailToAdmins(){
		email.configure("[BMC] Email test", "Test email executed at " + new DateTime(), admins);
		email.send();
	}
}
