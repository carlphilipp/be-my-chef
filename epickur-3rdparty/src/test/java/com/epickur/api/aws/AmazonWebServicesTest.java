package com.epickur.api.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.epickur.api.config.AmazonConfigTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AmazonConfigTest.class)
public class AmazonWebServicesTest {

	private final String filePath = "/path";
	@Mock
	private BasicAWSCredentials awsCreds;
	@Mock
	private ObjectListing listingMock;
	@Mock
	private List<S3ObjectSummary> summariesMock;
	@Mock
	private Iterator<S3ObjectSummary> iteratorMock;
	@Mock
	private S3ObjectSummary summaryMock;
	@Autowired
	private AmazonS3 s3clientMock;
	@Autowired
	private AmazonWebServices amazonWS;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
		reset(s3clientMock);
	}

	@Test
	public void testUploadFile() {
		amazonWS.uploadFile(filePath);

		verify(s3clientMock).putObject(any(PutObjectRequest.class));
	}

	@Test
	public void testUploadFileAmazonServiceException() {
		when(s3clientMock.putObject(any(PutObjectRequest.class))).thenThrow(new AmazonServiceException(""));

		amazonWS.uploadFile(filePath);

		verify(s3clientMock).putObject(any(PutObjectRequest.class));
	}

	@Test
	public void testUploadFileAmazonClientException() {
		when(s3clientMock.putObject(any(PutObjectRequest.class))).thenThrow(new AmazonClientException(""));

		amazonWS.uploadFile(filePath);

		verify(s3clientMock).putObject(any(PutObjectRequest.class));
	}

	@Test
	public void testDeleteOldFile() {
		when(s3clientMock.listObjects(isA(String.class))).thenReturn(listingMock);
		when(listingMock.isTruncated()).thenReturn(true, false);
		when(listingMock.getObjectSummaries()).thenReturn(summariesMock);
		when(s3clientMock.listNextBatchOfObjects(listingMock)).thenReturn(listingMock);
		when(summariesMock.size()).thenReturn(30);
		when(summariesMock.iterator()).thenReturn(iteratorMock);
		when(iteratorMock.hasNext()).thenReturn(true, false);
		when(iteratorMock.next()).thenReturn(summaryMock);
		when(summaryMock.getLastModified()).thenReturn(new Date());
		when(summaryMock.getKey()).thenReturn("key");
		when(summaryMock.getLastModified()).thenReturn(new Date());

		amazonWS.deleteOldFile();

		verify(s3clientMock).deleteObject(isA(String.class), isA(String.class));
		verify(summariesMock).addAll(summariesMock);
		verify(s3clientMock).listNextBatchOfObjects(listingMock);
	}
}
