package com.epickur.api.dump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MongoDBDumpTest {

	private String mongod = "mongod";
	private String ip = "localhost";
	private String port = "8080";
	private String database = "epickur";
	private String username = "login";
	private String password = "password";
	private String backupPath = "/path";
	private static final String TARGZEXT = ".tar.gz";
	private String date = "11-04-2015";
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	@Mock
	private Runtime runtime;
	@Mock
	private Process process;
	@Mock
	private File backupFolderMock;
	@Mock
	private File dumpFileMock;
	@Mock
	private File dumpDirectoryMock;

	private File[] listOfFiles = new File[1];
	@Mock
	private File fileFound;

	private InputStream inputStream = IOUtils.toInputStream("some test data for my input stream");

	private MongoDBDump mongoDBDump;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mongoDBDump = new MongoDBDump(date, mongod, ip, port, database, username, password, backupPath, runtime);
		mongoDBDump.setBackupFolder(backupFolderMock);
		mongoDBDump.setDumpFile(dumpFileMock);
		mongoDBDump.setDumpDirectory(dumpDirectoryMock);
	}

	@Test
	public void testExportMongo() throws IOException, InterruptedException {
		when(runtime.exec(mongoDBDump.buildDumpCommand())).thenReturn(process);
		when(process.getInputStream()).thenReturn(inputStream);
		when(process.getErrorStream()).thenReturn(inputStream);

		boolean actual = mongoDBDump.exportMongo();

		verify(backupFolderMock, times(1)).exists();
		verify(backupFolderMock, times(1)).mkdir();
		verify(runtime, times(1)).exec(mongoDBDump.buildDumpCommand());
		verify(process, times(1)).waitFor();
		assertTrue(actual);
	}

	@Test
	public void testExportMongoException() throws IOException, InterruptedException {
		when(runtime.exec(mongoDBDump.buildDumpCommand())).thenReturn(process);
		when(process.getInputStream()).thenReturn(null);

		boolean actual = mongoDBDump.exportMongo();

		verify(backupFolderMock, times(1)).exists();
		verify(backupFolderMock, times(1)).mkdir();
		verify(runtime, times(1)).exec(mongoDBDump.buildDumpCommand());
		verify(process, times(1)).waitFor();
		assertFalse(actual);
	}

	@Test
	public void testBuildDumpCommand() {
		String expected = mongod + " -d " + database + " -h " + ip + ":" + port + " -u " + username + " -p" + password + " -o " + backupPath;
		String actual = mongoDBDump.buildDumpCommand();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetCurrentNameFile() throws UnknownHostException {
		String expected = "epickur_" + InetAddress.getLocalHost().getHostName() + "_" + date + TARGZEXT;
		String actual = mongoDBDump.getCurrentNameFile();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetCurrentFullPathName() throws UnknownHostException {
		String expected = backupPath + FILE_SEPARATOR + mongoDBDump.getCurrentNameFile();
		String actual = mongoDBDump.getCurrentFullPathName();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteDumpFile() throws UnknownHostException {
		when(dumpFileMock.delete()).thenReturn(true);
		
		mongoDBDump.deleteDumpFile();
		
		verify(dumpFileMock, times(1)).delete();
	}
	
	@Test
	public void testDeleteDumpFileNotWorked() throws UnknownHostException {
		when(dumpFileMock.delete()).thenReturn(false);
		
		mongoDBDump.deleteDumpFile();
		
		verify(dumpFileMock, times(1)).delete();
	}
	
	@Test
	public void testCleanDumpDirectory() throws UnknownHostException {
		when(dumpDirectoryMock.exists()).thenReturn(true);
		when(dumpDirectoryMock.isDirectory()).thenReturn(true);
		
		mongoDBDump.cleanDumpDirectory();
	}
	
	@Test
	public void testCleanDumpDirectoryFailed() throws UnknownHostException {
		when(dumpDirectoryMock.exists()).thenReturn(true);
		when(dumpDirectoryMock.isDirectory()).thenReturn(true);
		when(dumpDirectoryMock.listFiles()).thenReturn(null);
		
		mongoDBDump.cleanDumpDirectory();
	}
	
	@Test
	public void testGetListFiles() throws UnknownHostException {
		when(dumpDirectoryMock.listFiles()).thenReturn(listOfFiles);
		listOfFiles[0] = fileFound;
		when(fileFound.isFile()).thenReturn(true);
		when(fileFound.getAbsolutePath()).thenReturn("/path");
		
		List<String> actuals = mongoDBDump.getListFiles();
		
		verify(dumpDirectoryMock, times(1)).listFiles();
		verify(fileFound, times(1)).isFile();
		verify(fileFound, times(1)).getAbsolutePath();
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
	}

}
