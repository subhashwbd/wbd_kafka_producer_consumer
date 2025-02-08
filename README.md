# Kafka Producer-Consumer Demo

## Overview
This repository contains a simple Kafka producer-consumer implementation designed to demonstrate and test Confluent cluster linking functionality, particularly focusing on consumer group offset replication behavior.

For detailed documentation about cluster linking and its impact on consumer group offsets, please refer to our [internal documentation](https://wbdstreaming.atlassian.net/wiki/spaces/ICE/pages/edit-v2/1747288075).

## Prerequisites

### Cluster Setup
1. Ensure cluster linking is properly configured:
   - Source and destination clusters are operational
   - Topic and consumer group offset configurations are correctly set
   - Verify that topic data synchronization is working (destination topics should be automatically created)

### Access Requirements
1. Valid API Key and Secret
2. Service account or user account with:
   - Read/write permissions for the source region topic
   - Read permissions for the consumer group

## Test Scenarios

### Scenario 1: Consumer Group Offset Sync
This scenario demonstrates the basic offset synchronization functionality.

#### Steps:
1. Build and run the application:
   ```bash
   mvn clean install
   # Run as Spring Boot application
   ```

2. Access the Swagger UI:
   - URL: [http://localhost:8080/doc.html#/home](http://localhost:8080/doc.html#/home)

3. Publish messages:
   - Use the bulk publish endpoint
   - Ensure correct topic name is specified

4. Observe behavior:
   - Consumer application starts processing messages
   - Consumer group offsets are stored in source region
   - Cluster link replicates both topic data and offset metadata
   - Verify replication through Confluent UI

5. Test destination region:
   - Start consumer application with destination region profile
   - Use identical consumer group name
   - Observe consumer group rebalancing
   - Verify no duplicate message processing

### Scenario 2: Active Consumers in Both Regions
This scenario demonstrates the issues with running active consumers simultaneously in both regions.

#### Prerequisites:
- Consumer applications running in both regions
- Same consumer group name configured for both consumers

#### Steps:
1. Start the application as described above

2. Access Swagger UI and publish messages

3. Observe behavior:
   - Consumers in both regions will process messages
   - Demonstrates how topic data mirroring occurs before offset sync
   - Helps visualize the delay between data and offset replication

## Application Setup

### Running the Application
1. Clone the repository
2. Configure application properties
3. Build with Maven:
   ```bash
   mvn clean install
   ```
4. Run the Spring Boot application
5. Access Swagger UI at [http://localhost:8080/doc.html#/home](http://localhost:8080/doc.html#/home)

### Configuration
- Configure appropriate profiles for source and destination regions
- Ensure consumer group names match when testing offset replication
- Set appropriate security credentials in application properties

## Common Issues
- Ensure proper permissions are set up before testing
- Verify cluster link status if offset replication isn't working
- Check consumer group names match exactly across regions
