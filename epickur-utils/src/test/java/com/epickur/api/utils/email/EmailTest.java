package com.epickur.api.utils.email;

import com.epickur.api.utils.config.EmailConfigTest;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmailConfigTest.class)
public class EmailTest {

	@Value("${admins}")
	private String[] admins;

	@Autowired
	private Email email;

	@Test
	public void testSendRealEmailToAdmins() {
		email.configure("[BMC] Email test", "Test email executed at " + new DateTime(), admins);
		email.send();
	}
}
