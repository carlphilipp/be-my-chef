package com.epickur.api.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.epickur.api.config.EpickurProperties;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Class that communicates with Amazon S3 server. It connects and sends the DB dump to it.
 *
 * @author cph
 * @version 1.0
 */
@AllArgsConstructor(onConstructor = @_(@Autowired))
@Log4j2
@Component
public final class AmazonWebServices {

	/**
	 * Maximum amount of dump we keep on S3
	 */
	private static final int MAX_DUMP_KEPT = 20;
	@NonNull
	public EpickurProperties properties;
	@NonNull
	private AmazonS3 s3client;

	/**
	 * @param filePath The file path
	 */
	public void uploadFile(final String filePath) {
		log.info("Uploading file on AWS...");
		final File file = new File(filePath);
		try {
			s3client.putObject(new PutObjectRequest(properties.getAwsBucket(), file.getName(), file));
			log.info("Upload done");
		} catch (AmazonServiceException ase) {
			final StringBuilder stb = new StringBuilder();
			stb.append("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
			stb.append("\nError Message:    ").append(ase.getMessage());
			stb.append("\nHTTP Status Code: ").append(ase.getStatusCode());
			stb.append("\nAWS Error Code:   ").append(ase.getErrorCode());
			stb.append("\nError Type:       ").append(ase.getErrorType());
			stb.append("\nRequest ID:       ").append(ase.getRequestId());
			log.error(stb.toString(), ase);
		} catch (AmazonClientException ace) {
			String res = "Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.";
			log.error("{}: {}", res, ace.getLocalizedMessage(), ace);
		}
	}

	/**
	 * Delete an old file
	 */
	public void deleteOldFile() {
		log.info("Deleting old file in AWS...");
		ObjectListing listing = s3client.listObjects(properties.getAwsBucket());
		final List<S3ObjectSummary> summaries = listing.getObjectSummaries();
		// Get absolutely all items
		while (listing.isTruncated()) {
			listing = s3client.listNextBatchOfObjects(listing);
			summaries.addAll(listing.getObjectSummaries());
		}

		if (summaries.size() > MAX_DUMP_KEPT) {
			S3ObjectSummary entry = null;
			DateTime dateTimeEntry = null;
			for (final S3ObjectSummary summary : summaries) {
				final DateTime dateTime = new DateTime(summary.getLastModified());
				if (dateTimeEntry == null || dateTime.isBefore(dateTimeEntry)) {
					entry = summary;
					dateTimeEntry = dateTime;
				}
			}
			if (entry != null && entry.getKey() != null) {
				log.info("Deleting: {}: {}", entry.getKey(), entry.getLastModified());
				s3client.deleteObject(properties.getAwsBucket(), entry.getKey());
			}
		}
		log.info("Delete done");
	}
}
