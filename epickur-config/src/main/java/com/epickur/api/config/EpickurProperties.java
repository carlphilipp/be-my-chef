package com.epickur.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EpickurProperties {

	// Base
	@Value("${name}")
	private String name;
	@Value("${address}")
	private String address;
	@Value("${folder}")
	private String folder;
	@Value("${admins}")
	private String[] admins;

	// Epickur Web site
	@Value("${epickur.web.address}")
	private String webAddress;

	// Mandrill
	@Value("${email.mandrill.key}")
	private String mandrillKey;
	@Value("${email.mandrill.from}")
	private String mandrillFrom;
	@Value("${email.mandrill.from.username}")
	private String mandrillFromUsername;
	@Value("${email.mandrill.version}")
	private String mandrillVersion;
	@Value("${email.mandrill.url}")
	private String mandrillUrl;
	@Value("${email.send}")
	private Boolean send;

	// MongoDB
	@Value("${mongo.address}")
	private String mongoAddress;
	@Value("${mongo.port}")
	private Integer mongoPort;
	@Value("${mongo.db.name}")
	private String mongoDbName;
	@Value("${mongo.user.login}")
	private String mongoLogin;
	@Value("${mongo.user.password}")
	private String mongoPassword;

	// Mongo Backup
	@Value("${mongod.path}")
	private String mongodPath;
	@Value("${mongo.backup.path}")
	private String mongoBackupPath;

	// Schedule
	@Value("${cron.cleankeys.interval}")
	private Integer cleanKeysInterval;
	@Value("${cron.order.timelimit}")
	private Integer orderTimeLimit;

	// Session
	@Value("${session.timeout}")
	private Integer sessionTimeout;

	// Stripe
	@Value("${stripe.key}")
	private String stripeKey;

	// Here
	@Value("${here.app.id}")
	private String hereAppId;
	@Value("${here.app.code}")
	private String hereAppCode;
	@Value("${here.api.resource}")
	private String hereApiResource;
	@Value("${here.api.version}")
	private String hereApiVersion;

	// Amazon web services
	@Value("${aws.access.KeyId}")
	private String awsAccessKeyId;
	@Value("${aws.secretKey}")
	private String awsSecretKey;
	@Value("${aws.bucket}")
	private String awsBucket;

}
