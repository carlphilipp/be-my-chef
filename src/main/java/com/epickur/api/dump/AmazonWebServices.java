package com.epickur.api.dump;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.epickur.api.utils.Utils;

/**
 * Class that communicates with Amazon S3 server. It connects and sends the DB dump to it.
 * 
 * @author cph
 * @version 1.0
 *
 */
public class AmazonWebServices {
	/** Logger */
	private static final Logger LOG = LogManager.getLogger(AmazonWebServices.class.getSimpleName());
	/** Maximum amount of dump we keep on S3 */
	private static final int MAX_DUMP_KEPT = 20;
	/** Amazon S3 client */
	private AmazonS3 s3client;
	/** Amazon Bucket name */
	private String bucketName;

	public AmazonWebServices() {
		Properties prop = Utils.getEpickurProperties();
		String accessKeyId = prop.getProperty("aws.access.KeyId");
		String secretKey = prop.getProperty("aws.secretKey");
		this.bucketName = prop.getProperty("aws.bucket");
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKey);
		this.s3client = new AmazonS3Client(awsCreds);
	}

	/**
	 * @param filePath
	 *            The file path
	 */
	public void uploadFile(final String filePath) {
		LOG.info("Uploading file on AWS...");
		File file = new File(filePath);
		try {
			s3client.putObject(new PutObjectRequest(bucketName, file.getName(), file));
			LOG.info("Upload done");
		} catch (AmazonServiceException ase) {
			StringBuilder stb = new StringBuilder();
			stb.append("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
			stb.append("\nError Message:    " + ase.getMessage());
			stb.append("\nHTTP Status Code: " + ase.getStatusCode());
			stb.append("\nAWS Error Code:   " + ase.getErrorCode());
			stb.append("\nError Type:       " + ase.getErrorType());
			stb.append("\nRequest ID:       " + ase.getRequestId());
			LOG.error(stb.toString(), ase);
		} catch (AmazonClientException ace) {
			String res = "Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.";
			LOG.error(res + " : " + ace.getLocalizedMessage(), ace);
		}
	}

	public void deleteOldFile() {
		LOG.info("Deleting old file in AWS...");
		ObjectListing listing = this.s3client.listObjects(bucketName);
		List<S3ObjectSummary> summaries = listing.getObjectSummaries();
		// Get absolutly all items
		while (listing.isTruncated()) {
			listing = this.s3client.listNextBatchOfObjects(listing);
			summaries.addAll(listing.getObjectSummaries());
		}
		S3ObjectSummary entry = null;
		DateTime dateTimeEntry = null;
		if (summaries.size() > MAX_DUMP_KEPT) {
			for (S3ObjectSummary summary : summaries) {
				DateTime dateTime = new DateTime(summary.getLastModified());
				if (dateTimeEntry == null || dateTime.isBefore(dateTimeEntry)) {
					entry = summary;
					dateTimeEntry = dateTime;
				}
			}
			LOG.info("Deleting: " + entry.getKey() + ": " + entry.getLastModified());
			this.s3client.deleteObject(bucketName, entry.getKey());
		}
		LOG.info("Delete done");
	}

	public static void main(String[] args) {
		AmazonWebServices aws = new AmazonWebServices();
		aws.deleteOldFile();
	}

}
