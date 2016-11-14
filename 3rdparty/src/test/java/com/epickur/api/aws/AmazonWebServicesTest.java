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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AmazonConfigTest.class)
public class AmazonWebServicesTest {

	private final String filePath = "/path";
	@Mock
	private BasicAWSCredentials basicAWSCredentials;
	@Mock
	private ObjectListing objectListing;
	@Mock
	private List<S3ObjectSummary> s3ObjectSummaries;
	@Mock
	private Iterator<S3ObjectSummary> s3ObjectSummaryIterator;
	@Mock
	private S3ObjectSummary s3ObjectSummary;
	@Autowired
	private AmazonS3 amazonS3;
	@Autowired
	private AmazonWebServices amazonWebServices;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
		reset(amazonS3);
	}

	@Test
	public void testUploadFile() {
		// When
		amazonWebServices.uploadFile(filePath);

		// Then
		then(amazonS3).should().putObject(any(PutObjectRequest.class));
	}

	@Test
	public void testUploadFileAmazonServiceException() {
		// Given
		given(amazonS3.putObject(any(PutObjectRequest.class))).willThrow(new AmazonServiceException(""));

		// When
		amazonWebServices.uploadFile(filePath);

		// Then
		then(amazonS3).should().putObject(any(PutObjectRequest.class));
	}

	@Test
	public void testUploadFileAmazonClientException() {
		// Given
		given(amazonS3.putObject(any(PutObjectRequest.class))).willThrow(new AmazonClientException(""));

		// When
		amazonWebServices.uploadFile(filePath);

		// Then
		then(amazonS3).should().putObject(any(PutObjectRequest.class));
	}

	@Test
	public void testDeleteOldFile() {
		// Given
		given(amazonS3.listObjects(anyString())).willReturn(objectListing);
		given(objectListing.isTruncated()).willReturn(true, false);
		given(objectListing.getObjectSummaries()).willReturn(s3ObjectSummaries);
		given(amazonS3.listNextBatchOfObjects(objectListing)).willReturn(objectListing);
		given(s3ObjectSummaries.size()).willReturn(30);
		given(s3ObjectSummaries.iterator()).willReturn(s3ObjectSummaryIterator);
		given(s3ObjectSummaryIterator.hasNext()).willReturn(true, false);
		given(s3ObjectSummaryIterator.next()).willReturn(s3ObjectSummary);
		given(s3ObjectSummary.getLastModified()).willReturn(new Date());
		given(s3ObjectSummary.getKey()).willReturn("key");
		given(s3ObjectSummary.getLastModified()).willReturn(new Date());

		// When
		amazonWebServices.deleteOldFile();

		// Then
		then(amazonS3).should().deleteObject(anyString(), anyString());
		then(s3ObjectSummaries).should().addAll(s3ObjectSummaries);
		then(amazonS3).should().listNextBatchOfObjects(objectListing);
	}
}
