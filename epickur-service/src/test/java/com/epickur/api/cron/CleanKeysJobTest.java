package com.epickur.api.cron;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.Utils;

@RunWith(MockitoJUnitRunner.class)
public class CleanKeysJobTest {

	public static Integer SESSION_TIMEOUT;
	@Mock
	private JobExecutionContext context;
	@Mock
	private KeyDAO keyDao;
	@InjectMocks
	private CleanKeysJob keyJob;

	@BeforeClass
	public static void beforeSetUp() {
		Properties prop = Utils.getEpickurProperties();
		SESSION_TIMEOUT = Integer.valueOf(prop.getProperty("session.timeout"));
	}

	@Test
	public void testExecuteValid() throws JobExecutionException, EpickurException {
		List<Key> keys = new ArrayList<Key>();
		Key key = EntityGenerator.generateRandomAdminKey();
		DateTime now = new DateTime();
		key.setCreatedAt(now);
		keys.add(key);
		when(keyDao.readAll()).thenReturn(keys);

		keyJob.execute(context);

		verify(keyDao, times(1)).readAll();
		verify(keyDao, never()).delete(anyObject());
	}

	@Test
	public void testExecuteNotValid() throws JobExecutionException, EpickurException {
		List<Key> keys = new ArrayList<Key>();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setId(new ObjectId());
		DateTime now = new DateTime();
		now = now.plusDays(SESSION_TIMEOUT + 10);
		key.setCreatedAt(now);
		keys.add(key);
		when(keyDao.readAll()).thenReturn(keys);

		keyJob.execute(context);

		verify(keyDao, times(1)).readAll();
		verify(keyDao, times(1)).delete(key.getId().toHexString());
	}

	@Test
	public void testExecuteEpickurException() throws JobExecutionException, EpickurException {
		when(keyDao.readAll()).thenThrow(new EpickurException());
		try {
			keyJob.execute(context);
		} finally {
			verify(keyDao, times(1)).readAll();
			verify(keyDao, never()).delete(anyObject());
		}
	}
}
