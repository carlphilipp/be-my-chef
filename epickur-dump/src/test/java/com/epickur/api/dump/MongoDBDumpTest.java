package com.epickur.api.dump;

import com.epickur.api.config.MongoDumpConfigTest;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoDumpConfigTest.class)
public class MongoDBDumpTest {

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final String BACKUP_PATH = "/path";

	@Mock
	private Runtime runtime;
	@Mock
	private Process process;
	@Mock
	private File backupFolder;
	@Mock
	private File dumpFile;
	@Mock
	private File dumpDirectory;
	@Mock
	private File fileFound;
	@Autowired
	private MongoDBDump mongoDBDump;
	private File[] listOfFiles = new File[1];
	private InputStream inputStream = IOUtils.toInputStream("some test data for my input stream");

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mongoDBDump.setBackupFolder(backupFolder);
		mongoDBDump.setDumpFile(dumpFile);
		mongoDBDump.setDumpDirectory(dumpDirectory);
		mongoDBDump.setRuntime(runtime);
	}

	@Test
	public void testExportMongo() throws IOException, InterruptedException {
		when(runtime.exec(mongoDBDump.buildDumpCommand())).thenReturn(process);
		when(process.getInputStream()).thenReturn(inputStream);
		when(process.getErrorStream()).thenReturn(inputStream);

		boolean actual = mongoDBDump.exportMongo();

		verify(backupFolder).exists();
		verify(backupFolder).mkdir();
		verify(runtime).exec(mongoDBDump.buildDumpCommand());
		verify(process).waitFor();
		assertTrue(actual);
	}

	@Test
	public void testExportMongoException() throws IOException, InterruptedException {
		when(runtime.exec(mongoDBDump.buildDumpCommand())).thenReturn(process);
		when(process.getInputStream()).thenReturn(null);

		boolean actual = mongoDBDump.exportMongo();

		verify(backupFolder).exists();
		verify(backupFolder).mkdir();
		verify(process).waitFor();
		assertFalse(actual);
	}

	@Test
	public void testBuildDumpCommand() {
		String mongod = "mongod";
		String ip = "localhost";
		Integer port = 27017;
		String database = "epickur";
		String username = "login";
		String password = "password";

		String expected = mongod + " -d " + database + " -h " + ip + ":" + port + " -u " + username + " -p" + password + " -o " + BACKUP_PATH;
		String actual = mongoDBDump.buildDumpCommand();
		assertEquals(expected, actual);
	}

	@Test
	public void testGetCurrentFullPathName() throws UnknownHostException {
		String expected = BACKUP_PATH + FILE_SEPARATOR + mongoDBDump.getCurrentNameFile();
		String actual = mongoDBDump.getCurrentFullPathName();
		assertEquals(expected, actual);
	}

	@Test
	public void testDeleteDumpFile() throws UnknownHostException {
		when(dumpFile.delete()).thenReturn(true);

		mongoDBDump.deleteDumpFile();

		verify(dumpFile).delete();
	}

	@Test
	public void testDeleteDumpFileNotWorked() throws UnknownHostException {
		when(dumpFile.delete()).thenReturn(false);

		mongoDBDump.deleteDumpFile();

		verify(dumpFile).delete();
	}

//	@Test
//	public void testCleanDumpDirectory() throws UnknownHostException {
//		when(dumpDirectory.exists()).thenReturn(true);
//		when(dumpDirectory.isDirectory()).thenReturn(true);
//
//		mongoDBDump.cleanDumpDirectory();
//	}
//
//	@Test
//	public void testCleanDumpDirectoryFailed() throws UnknownHostException {
//		when(dumpDirectory.exists()).thenReturn(true);
//		when(dumpDirectory.isDirectory()).thenReturn(true);
//		when(dumpDirectory.listFiles()).thenReturn(null);
//
//		mongoDBDump.cleanDumpDirectory();
//	}

	@Test
	public void testGetListFiles() throws UnknownHostException {
		when(dumpDirectory.listFiles()).thenReturn(listOfFiles);
		listOfFiles[0] = fileFound;
		when(fileFound.isFile()).thenReturn(true);
		when(fileFound.getAbsolutePath()).thenReturn("/path");

		List<String> actuals = mongoDBDump.getListFiles();

		verify(dumpDirectory).listFiles();
		verify(fileFound).isFile();
		verify(fileFound).getAbsolutePath();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}
}
