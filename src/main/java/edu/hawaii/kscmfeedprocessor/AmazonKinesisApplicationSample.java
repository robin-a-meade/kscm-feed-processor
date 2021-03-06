/*
 * Original work Copyright 2012-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Modified work Copyright 2017 University of Hawaii
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

/*
 * Original work:
 * https://github.com/aws/aws-sdk-java/blob/1.11.118/src/samples/AmazonKinesis/AmazonKinesisApplicationSample.java
 */

package edu.hawaii.kscmfeedprocessor;

import java.net.InetAddress;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Sample Amazon Kinesis Application.
 */
@Component
public final class AmazonKinesisApplicationSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
     * Before running the code:
     *      Fill in your AWS access credentials in the provided credentials
     *      file template, and be sure to move the file to the default location
     *      (~/.aws/credentials) where the sample code will load the
     *      credentials from.
     *      https://console.aws.amazon.com/iam/home?#security_credential
     *
     * WARNING:
     *      To avoid accidental leakage of your credentials, DO NOT keep
     *      the credentials file in your source directory.
     */

    @Value("${app.profile}")
    private String profile;

    @Value("${app.kinesis_application_stream_name}")
    private String SAMPLE_APPLICATION_STREAM_NAME;

    @Value("${app.kinesis_application_name}")
    private String SAMPLE_APPLICATION_NAME;

    @Value("${app.kinesis_application_region_name}")
    private String APPLICATION_REGION_NAME;

    @Autowired
    IRecordProcessorFactory recordProcessorFactory;


    // Initial position in the stream when the application starts up for the first time.
    // Position can be one of LATEST (most recent data) or TRIM_HORIZON (oldest available data)
    private InitialPositionInStream SAMPLE_APPLICATION_INITIAL_POSITION_IN_STREAM =
            InitialPositionInStream.LATEST;

    private AWSCredentialsProvider credentialsProvider;

    private void init() {
        logger.info("init");
        logger.info("Profile: {}", profile);

        // Ensure the JVM will refresh the cached IP values of AWS resources (e.g. service endpoints).
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        credentialsProvider = new ProfileCredentialsProvider(profile);
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
                    + "Please make sure that your credentials file is at the correct "
                    + "location (~/.aws/credentials), and is in valid format.", e);
        }
    }

    public void run(String... args) throws Exception {
        init();

        if (args.length == 1 && "delete-resources".equals(args[0])) {
            deleteResources();
            return;
        }

        String workerId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + UUID.randomUUID();
        KinesisClientLibConfiguration kinesisClientLibConfiguration =
                new KinesisClientLibConfiguration(SAMPLE_APPLICATION_NAME,
                        SAMPLE_APPLICATION_STREAM_NAME,
                        credentialsProvider,
                        workerId);

        kinesisClientLibConfiguration.withRegionName(APPLICATION_REGION_NAME);

        kinesisClientLibConfiguration.withInitialPositionInStream(SAMPLE_APPLICATION_INITIAL_POSITION_IN_STREAM);

        //IRecordProcessorFactory recordProcessorFactory = new AmazonKinesisApplicationRecordProcessorFactory();
        Worker worker = new Worker(recordProcessorFactory, kinesisClientLibConfiguration);

        logger.info("Running {} to process stream {} as worker {}...",
                SAMPLE_APPLICATION_NAME,
                SAMPLE_APPLICATION_STREAM_NAME,
                workerId);

        int exitCode = 0;
        try {
            worker.run();
        } catch (Throwable t) {
            System.err.println("Caught throwable while processing data.");
            t.printStackTrace();
            exitCode = 1;
        }
        System.exit(exitCode);
    }

    public void deleteResources() {
        AWSCredentials credentials = credentialsProvider.getCredentials();

        // Delete the stream
        AmazonKinesis kinesis = new AmazonKinesisClient(credentials);
        System.out.printf("Deleting the Amazon Kinesis stream used by the sample. Stream Name = %s.\n",
                SAMPLE_APPLICATION_STREAM_NAME);
        try {
            kinesis.deleteStream(SAMPLE_APPLICATION_STREAM_NAME);
        } catch (ResourceNotFoundException ex) {
            // The stream doesn't exist.
        }

        // Delete the table
        AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient(credentialsProvider.getCredentials());
        System.out.printf("Deleting the Amazon DynamoDB table used by the Amazon Kinesis Client Library. Table Name = %s.\n",
                SAMPLE_APPLICATION_NAME);
        try {
            dynamoDB.deleteTable(SAMPLE_APPLICATION_NAME);
        } catch (com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException ex) {
            // The table doesn't exist.
        }
    }
}
