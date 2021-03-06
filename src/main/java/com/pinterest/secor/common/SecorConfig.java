/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinterest.secor.common;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import com.netflix.config.ConfigurationManager;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.naming.OperationNotSupportedException;

/**
 * One-stop shop for Secor configuration options.
 *
 * @author Pawel Garbacki (pawel@pinterest.com)
 */
public class SecorConfig {
    private final PropertiesConfiguration mProperties;

    private static final ThreadLocal<SecorConfig> mSecorConfig = new ThreadLocal<SecorConfig>() {

        @Override
        protected SecorConfig initialValue() {
            // Load the default configuration file first
            Properties systemProperties = System.getProperties();
            String configProperty = systemProperties.getProperty("config");

            PropertiesConfiguration properties;
            try {
                properties = new PropertiesConfiguration(configProperty);
                // Load the properties from 1p-<this app>.properties file, note that this will override the same
                // properties in those secor.*.properties files. It should always loaded LAST.
                load1pProps(properties);
            } catch (ConfigurationException e) {
                throw new RuntimeException("Error loading configuration from " + configProperty);
            }

            for (final Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
                properties.setProperty(entry.getKey().toString(), entry.getValue());
            }

            return new SecorConfig(properties);
        }
    };

    public static SecorConfig load() throws ConfigurationException {
        return mSecorConfig.get();
    }
    
    private static void load1pProps(PropertiesConfiguration properties) {
    	    properties.setProperty("aws.access.key", ConfigurationManager.getConfigInstance().getString("aws.access.key"));
    	    properties.setProperty("aws.secret.key", ConfigurationManager.getConfigInstance().getString("aws.secret.key"));
    	    properties.setProperty("secor.s3.bucket", ConfigurationManager.getConfigInstance().getString("secor.s3.bucket"));
    	    properties.setProperty("kafka.seed.broker.host", 
    	    		StringUtils.join(ConfigurationManager.getConfigInstance().getStringArray("kafka.seed.broker.host"), ','));
    	    properties.setProperty("zookeeper.quorum", 
    	    		StringUtils.join(ConfigurationManager.getConfigInstance().getStringArray("zookeeper.quorum"), ','));
    	    
    }

    /**
     * Exposed for testability
     * 
     * @param properties
     */
    public SecorConfig(PropertiesConfiguration properties) {
        mProperties = properties;
    }

    public String getKafkaSeedBrokerHost() {
        return getString("kafka.seed.broker.host");
    }

    public int getKafkaSeedBrokerPort() {
        return getInt("kafka.seed.broker.port");
    }

    public String getKafkaZookeeperPath() {
        return getString("kafka.zookeeper.path");
    }

    public String getZookeeperQuorum() {
        return StringUtils.join(getStringArray("zookeeper.quorum"), ',');
    }

    public int getConsumerTimeoutMs() {
        return getInt("kafka.consumer.timeout.ms");
    }

    public String getPartitionAssignmentStrategy() {
        return getString("kafka.partition.assignment.strategy");
    }

    public String getRebalanceMaxRetries() {
        return getString("kafka.rebalance.max.retries");
    }

    public String getRebalanceBackoffMs() {
        return getString("kafka.rebalance.backoff.ms");
    }

    public String getFetchMessageMaxBytes() {
        return getString("kafka.fetch.message.max.bytes");
    }

    public String getSocketReceieveBufferBytes() {
        return getString("kafka.socket.receive.buffer.bytes");
    }

    public String getFetchMinBytes() {
        return getString("kafka.fetch.min.bytes");
    }

    public String getFetchWaitMaxMs() {
        return getString("kafka.fetch.wait.max.ms");
    }

    public int getGeneration() {
        return getInt("secor.generation");
    }

    public int getConsumerThreads() {
        return getInt("secor.consumer.threads");
    }

    public long getMaxFileSizeBytes() {
        return getLong("secor.max.file.size.bytes");
    }

    public long getMaxFileAgeSeconds() {
        return getLong("secor.max.file.age.seconds");
    }

    public long getOffsetsPerPartition() {
        return getLong("secor.offsets.per.partition");
    }

    public int getMessagesPerSecond() {
        return getInt("secor.messages.per.second");
    }

    public String getS3FileSystem() { return getString("secor.s3.filesystem"); }

    public boolean getSeperateContainersForTopics() {
    	return getString("secor.swift.containers.for.each.topic").toLowerCase().equals("true");
    }
    
    public String getSwiftContainer() {
        return getString("secor.swift.container");
    }

    public String getSwiftPath() {
        return getString("secor.swift.path");
    }
    
    public String getS3Bucket() {
        return getString("secor.s3.bucket");
    }

    public String getS3Path() {
        return getString("secor.s3.path");
    }

    public String getS3Prefix() {
        return getS3FileSystem() + "://" + getS3Bucket() + "/" + getS3Path();
    }
    public String getLocalPath() {
        return getString("secor.local.path");
    }

    public String getKafkaTopicFilter() {
        return getString("secor.kafka.topic_filter");
    }

    public String getKafkaGroup() {
        return getString("secor.kafka.group");
    }

    public int getZookeeperSessionTimeoutMs() {
        return getInt("zookeeper.session.timeout.ms");
    }

    public int getZookeeperSyncTimeMs() {
        return getInt("zookeeper.sync.time.ms");
    }

    public String getMessageParserClass() {
        return getString("secor.message.parser.class");
    }

    public String getUploadManagerClass() {
        return getString("secor.upload.manager.class");
    }

    public int getTopicPartitionForgetSeconds() {
        return getInt("secor.topic_partition.forget.seconds");
    }

    public int getLocalLogDeleteAgeHours() {
        return getInt("secor.local.log.delete.age.hours");
    }

    public String getFileExtension() {
        return getString("secor.file.extension");
    }

    public int getOstrichPort() {
        return getInt("ostrich.port");
    }

    public String getCloudService() {
        return getString("cloud.service");
    }
    
    public String getAwsAccessKey() {
        return getString("aws.access.key");
    }

    public String getAwsSecretKey() {
        return getString("aws.secret.key");
    }

    public String getAwsEndpoint() {
        return getString("aws.endpoint");
    }

    public String getAwsRegion() {
        return getString("aws.region");
    }

    public String getAwsSseType() {
        return getString("aws.sse.type");
    }

    public String getAwsSseKmsKey() {
        return getString("aws.sse.kms.key");
    }

    public String getAwsSseCustomerKey() {
        return getString("aws.sse.customer.key");
    }

    public String getSwiftTenant() {
        return getString("swift.tenant");
    }
    
    public String getSwiftUsername() {
        return getString("swift.username");
    }
    
    public String getSwiftPassword() {
        return getString("swift.password");
    }    
    
    public String getSwiftAuthUrl() {
        return getString("swift.auth.url");
    }
    
    public String getSwiftPublic() {
    	return getString("swift.public");
    }
    
    public String getSwiftPort() {
    	return getString("swift.port");
    }
    
    public String getSwiftGetAuth() {
    	return getString("swift.use.get.auth");
    }
    
    public String getSwiftApiKey() {
    	return getString("swift.api.key");
    }
    
    public String getQuboleApiToken() {
        return getString("qubole.api.token");
    }

    public String getTsdbHostport() {
        return getString("tsdb.hostport");
    }

    public String getStatsDHostPort() {
        return getString("statsd.hostport");
    }

    public String getMonitoringBlacklistTopics() {
        return getString("monitoring.blacklist.topics");
    }

    public String getMonitoringPrefix() {
        return getString("monitoring.prefix");
    }

    public String getMessageTimestampName() {
        return getString("message.timestamp.name");
    }

    public int getMessageTimestampId() {
        return getInt("message.timestamp.id");
    }

    public String getMessageTimestampType() {
        return getString("message.timestamp.type");
    }

    public String getMessageTimestampInputPattern() {
        return getString("message.timestamp.input.pattern");
    }

    public int getFinalizerLookbackPeriods() {
        return getInt("secor.finalizer.lookback.periods", 10);
    }

    public String getHivePrefix() { 
        return getString("secor.hive.prefix"); 
    }

    public String getHiveTableName(String topic) {
        String key = "secor.hive.table.name." + topic;
        return mProperties.getString(key, null);
    }

    public String getCompressionCodec() {
        return getString("secor.compression.codec");
    }

    public int getMaxMessageSizeBytes() {
        return getInt("secor.max.message.size.bytes");
    }
    
    public String getFileReaderWriterFactory() {
    	return getString("secor.file.reader.writer.factory");
    }
    
    public String getPerfTestTopicPrefix() {
    	return getString("secor.kafka.perf_topic_prefix");
    }

    public String getZookeeperPath() {
        return getString("secor.zookeeper.path");
    }

    public String getGsCredentialsPath() {
        return getString("secor.gs.credentials.path");
    }

    public String getGsBucket() {
        return getString("secor.gs.bucket");
    }

    public String getGsPath() {
        return getString("secor.gs.path");
    }

    public int getGsConnectTimeoutInMs() {
        return getInt("secor.gs.connect.timeout.ms", 3 * 60000);
    }

    public int getGsReadTimeoutInMs() {
        return getInt("secor.gs.read.timeout.ms", 3 * 60000);
    }

    public int getFinalizerDelaySeconds() {
        return getInt("partitioner.finalizer.delay.seconds");
    }

    public TimeZone getTimeZone() {
        String timezone = getString("secor.parser.timezone");
        return Strings.isNullOrEmpty(timezone) ? TimeZone.getTimeZone("UTC") : TimeZone.getTimeZone(timezone);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        return mProperties.getBoolean(name, defaultValue);
    }

    private void checkProperty(String name) {
        if (!mProperties.containsKey(name)) {
            throw new RuntimeException("Failed to find required configuration option '" +
                                       name + "'.");
        }
    }

    private String getString(String name) {
        checkProperty(name);
        return mProperties.getString(name);
    }

    private int getInt(String name) {
        checkProperty(name);
        return mProperties.getInt(name);
    }

    private int getInt(String name, int defaultValue) {
        return mProperties.getInt(name, defaultValue);
    }

    private long getLong(String name) {
        return mProperties.getLong(name);
    }

    private String[] getStringArray(String name) {
        return mProperties.getStringArray(name);
    }
}
