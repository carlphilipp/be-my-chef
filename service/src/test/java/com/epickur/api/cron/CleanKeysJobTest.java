package com.epickur.api.cron;

import com.epickur.api.config.EpickurProperties;
import com.epickur.api.config.ServiceConfigTest;
import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.Utils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServiceConfigTest.class)
public class CleanKeysJobTest {

	@Autowired
	public EpickurProperties epickurProperties;
	@Mock
	private JobExecutionContext context;
	@Mock
	private KeyDAO keyDao;
	@Mock
	private Utils utils;
	@InjectMocks
	private CleanKeysJob keyJob;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void testExecuteValid() throws JobExecutionException, EpickurException {
		// Given
		List<Key> keys = new ArrayList<>();
		Key key = EntityGenerator.generateRandomAdminKey();
		DateTime now = new DateTime();
		key.setCreatedAt(now);
		keys.add(key);
		given(keyDao.readAll()).willReturn(keys);
		given(utils.isValid(key)).willReturn(true);

		// When
		keyJob.execute();

		// Then
		then(keyDao).should().readAll();
		then(keyDao).should(never()).delete(anyString());
	}

	@Test
	public void testExecuteNotValid() throws JobExecutionException, EpickurException {
		// Given
		List<Key> keys = new ArrayList<>();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setId(new ObjectId());
		DateTime now = new DateTime();
		now = now.plusDays(epickurProperties.getSessionTimeout() + 10);
		key.setCreatedAt(now);
		keys.add(key);
		given(keyDao.readAll()).willReturn(keys);

		// When
		keyJob.execute();

		// Then
		then(keyDao).should().readAll();
		then(keyDao).should().delete(key.getId().toHexString());
	}

	@Test
	public void testExecuteEpickurException() throws JobExecutionException, EpickurException {
		// Given
		given(keyDao.readAll()).willThrow(new EpickurException());
		try {
			// When
			keyJob.execute();
		} finally {
			// Then
			then(keyDao).should().readAll();
			then(keyDao).should(never()).delete(anyString());
		}
	}
}
