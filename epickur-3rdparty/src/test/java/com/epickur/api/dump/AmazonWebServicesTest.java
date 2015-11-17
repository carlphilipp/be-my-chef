package com.epickur.api.dump;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Matchers.*;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AmazonWebServicesTest {
	
	private String filePath = "/path";
	@Mock
	private AmazonS3 s3clientMock;
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
	@InjectMocks
	private AmazonWebServices amazonWS;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testUploadFile() {
		amazonWS.uploadFile(filePath);
		
		verify(s3clientMock, times(1)).putObject(any(PutObjectRequest.class));
	}
	
	@Test
	public void testUploadFileAmazonServiceException() {
		when(s3clientMock.putObject(any(PutObjectRequest.class))).thenThrow(new AmazonServiceException(""));
		
		amazonWS.uploadFile(filePath);
		
		verify(s3clientMock, times(1)).putObject(any(PutObjectRequest.class));
	}
	
	@Test
	public void testUploadFileAmazonClientException() {
		when(s3clientMock.putObject(any(PutObjectRequest.class))).thenThrow(new AmazonClientException(""));
		
		amazonWS.uploadFile(filePath);
		
		verify(s3clientMock, times(1)).putObject(any(PutObjectRequest.class));
	}
	
	@Test
	public void testDeleteOldFile() {
		when(s3clientMock.listObjects(anyString())).thenReturn(listingMock);
		when(listingMock.isTruncated()).thenReturn(true, false);
		when(listingMock.getObjectSummaries()).thenReturn(summariesMock);
		when(s3clientMock.listNextBatchOfObjects(listingMock)).thenReturn(listingMock);
		when(summariesMock.size()).thenReturn(30);
		when(summariesMock.iterator()).thenReturn(iteratorMock);
		when(iteratorMock.hasNext()).thenReturn(true,false); 
		when(iteratorMock.next()).thenReturn(summaryMock);
		when(summaryMock.getLastModified()).thenReturn(new Date());
		when(summaryMock.getKey()).thenReturn("key");
		when(summaryMock.getLastModified()).thenReturn(new Date());
		
		amazonWS.deleteOldFile();
		
		verify(s3clientMock, times(1)).deleteObject(anyString(), anyString());
		verify(summariesMock, times(1)).addAll(summariesMock);
		verify(s3clientMock, times(1)).listNextBatchOfObjects(listingMock);
	}
}
