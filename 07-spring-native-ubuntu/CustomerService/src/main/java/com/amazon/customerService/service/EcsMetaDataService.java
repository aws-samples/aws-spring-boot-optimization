/*
 * Copyright 2010-2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazon.customerService.service;

import com.amazon.customerService.model.metrics.ContainerMetric;
import com.amazon.customerService.model.metrics.MetricWrapper;
import com.amazon.customerService.model.metrics.TaskMetric;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.DescribeTasksRequest;
import software.amazon.awssdk.services.ecs.model.DescribeTasksResponse;
import software.amazon.awssdk.services.ecs.model.Task;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class EcsMetaDataService {

    public MetricWrapper getMetaData() {

        MetricWrapper metricWrapper = new MetricWrapper();

        try {
            var metaDataUrl = System.getenv("ECS_CONTAINER_METADATA_URI_V4");

            if (metaDataUrl == null) {
                log.debug("metaDataUrl is null");
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(URI.create(metaDataUrl + "/task").toURL());

            JsonPointer pointer = JsonPointer.compile("/TaskARN");
            String taskArn = node.at(pointer).asText();

            pointer = JsonPointer.compile("/Cluster");
            String cluster = node.at(pointer).asText();

            ContainerCredentialsProvider provider = ContainerCredentialsProvider.builder().build();
            EcsClient ecsClient = EcsClient.builder().credentialsProvider(provider).build();

            DescribeTasksRequest request = DescribeTasksRequest.builder()
                    .tasks(taskArn).cluster(cluster).build();
            DescribeTasksResponse response = ecsClient.describeTasks(request);

            Instant pullStartedAtInstant = null, pullStoppedAtInstant = null, createdAtInstant = null, startedAtInstant = null;

            if (response.hasTasks()) {
                List<Task> taskList = response.tasks();
                Task task = taskList.get(0);

                pullStartedAtInstant = task.pullStartedAt();
                pullStoppedAtInstant = task.pullStoppedAt();
                createdAtInstant = task.createdAt();
                startedAtInstant = task.startedAt();
            }

            TaskMetric taskMetric = new TaskMetric(pullStartedAtInstant, pullStoppedAtInstant, taskArn);
            ContainerMetric containerMetric = new ContainerMetric(createdAtInstant, startedAtInstant);

            metricWrapper.setTaskMetric(taskMetric);
            metricWrapper.setContainerMetric(containerMetric);
        }

        catch (IOException exc) {
            log.error("An error occurred: ", exc);
        }

        return metricWrapper;
    }
}
