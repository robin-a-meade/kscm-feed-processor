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
 * https://github.com/aws/aws-sdk-java/blob/1.11.118/src/samples/AmazonKinesis/AmazonKinesisApplicationRecordProcessorFactory.java
 */

package edu.hawaii.kscm;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;

/**
 * Used to create new record processors.
 */
public class AmazonKinesisApplicationRecordProcessorFactory implements IRecordProcessorFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public IRecordProcessor createProcessor() {
        return new AmazonKinesisApplicationSampleRecordProcessor();
    }
}
