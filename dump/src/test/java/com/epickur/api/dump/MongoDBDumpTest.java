package com.epickur.api.dump;

import com.epickur.api.config.MongoDumpConfigTest;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.MockitoAnnotations.initMocks;

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
		initMocks(this);
		mongoDBDump.setBackupFolder(backupFolder);
		mongoDBDump.setDumpFile(dumpFile);
		mongoDBDump.setDumpDirectory(dumpDirectory);
		mongoDBDump.setRuntime(runtime);
	}

	@Test
	public void testExportMongo() throws IOException, InterruptedException {
		// Given
		given(runtime.exec(mongoDBDump.buildDumpCommand())).willReturn(process);
		given(process.getInputStream()).willReturn(inputStream);
		given(process.getErrorStream()).willReturn(inputStream);

		// When
		boolean actual = mongoDBDump.exportMongo();

		// Then
		assertTrue(actual);
		then(backupFolder).should().exists();
		then(backupFolder).should().mkdir();
		then(runtime).should().exec(mongoDBDump.buildDumpCommand());
		then(process).should().waitFor();
	}

	@Test
	public void testExportMongoException() throws IOException, InterruptedException {
		// Given
		given(runtime.exec(mongoDBDump.buildDumpCommand())).willReturn(process);
		given(process.getInputStream()).willReturn(null);

		// When
		boolean actual = mongoDBDump.exportMongo();

		// Then
		assertFalse(actual);
		then(backupFolder).should().exists();
		then(backupFolder).should().mkdir();
		then(process).should().waitFor();
	}

	@Test
	public void testBuildDumpCommand() {
		// Given
		String mongod = "mongod";
		String ip = "localhost";
		Integer port = 27017;
		String database = "epickur";
		String username = "login";
		String password = "password";
		String expected = mongod + " -d " + database + " -h " + ip + ":" + port + " -u " + username + " -p" + password + " -o " + BACKUP_PATH;

		// When
		String actual = mongoDBDump.buildDumpCommand();

		// Then
		assertEquals(expected, actual);
	}

	@Test
	public void testGetCurrentFullPathName() throws UnknownHostException {
		// Given
		String expected = BACKUP_PATH + FILE_SEPARATOR + mongoDBDump.getCurrentNameFile();

		// When
		String actual = mongoDBDump.getCurrentFullPathName();

		// Then
		assertEquals(expected, actual);
	}

	@Test
	public void testDeleteDumpFile() throws UnknownHostException {
		// Given
		given(dumpFile.delete()).willReturn(true);

		// When
		mongoDBDump.deleteDumpFile();

		// Then
		then(dumpFile).should().delete();
	}

	@Test
	public void testDeleteDumpFileNotWorked() throws UnknownHostException {
		// Given
		given(dumpFile.delete()).willReturn(false);

		// When
		mongoDBDump.deleteDumpFile();

		// Then
		then(dumpFile).should().delete();
	}

	@Test
	public void testGetListFiles() throws UnknownHostException {
		// Given
		given(dumpDirectory.listFiles()).willReturn(listOfFiles);
		listOfFiles[0] = fileFound;
		given(fileFound.isFile()).willReturn(true);
		given(fileFound.getAbsolutePath()).willReturn("/path");

		// When
		List<String> actual = mongoDBDump.getListFiles();

		// Then
		assertNotNull(actual);
		assertThat(actual, hasSize(1));
		then(dumpDirectory).should().listFiles();
		then(fileFound).should().isFile();
		then(fileFound).should().getAbsolutePath();

	}
}
