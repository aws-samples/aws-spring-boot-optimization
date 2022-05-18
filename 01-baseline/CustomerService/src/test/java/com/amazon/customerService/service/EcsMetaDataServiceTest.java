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
import com.amazon.customerService.model.metrics.TaskMetric;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("local")

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations="classpath:application-local.properties")

public class EcsMetaDataServiceTest {

    @Test
    @Order(1)
    public void testParse() {

        try {
            Path workingDir = Path.of("", "src/test/resources");
            Path file = workingDir.resolve("task_response.json");
            System.out.println("Parsing file: " + file);

            EcsMetaDataService ecsMetaDataService = new EcsMetaDataService();
            TaskMetric taskMetric = ecsMetaDataService.parseTaskMetaData(file.toUri().toURL());

            file = workingDir.resolve("container_response.json");
            System.out.println("Parsing file: " + file);

            ContainerMetric containerMetric = ecsMetaDataService.parseContainerMetaData(file.toUri().toURL());

            String pullStartedAt = ZonedDateTime.ofInstant(taskMetric.getPullStartedAt(), ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
            String pullStoppedAt = ZonedDateTime.ofInstant(taskMetric.getPullStoppedAt(), ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);

            Assert.assertEquals(pullStartedAt, "2020-10-08T20:47:16.053330955Z");
            Assert.assertEquals(pullStoppedAt, "2020-10-08T20:47:19.592684631Z");

        }

        catch (Exception exc) {
            exc.printStackTrace();
            Assert.fail();
        }
    }

}
