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

package com.amazon.customerService;

import com.amazon.customerService.config.AppConfig;
import com.amazon.customerService.model.metrics.MetricWrapper;
import com.amazon.customerService.service.EcsMetaDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@SpringBootApplication
public class CustomerServiceApplication {

    private static Instant startTime;
    private static Instant endTime;

    public static void main(String[] args) {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        startTime = Instant.ofEpochMilli(bean.getStartTime());
        SpringApplication.run(CustomerServiceApplication.class, args);
        Duration springBootStartTime = Duration.between(startTime, endTime);

        EcsMetaDataService ecsMetaDataService = new EcsMetaDataService();
        MetricWrapper metricWrapper = ecsMetaDataService.getMetaData();

        if (metricWrapper != null) {
            metricWrapper.setVersion(AppConfig.APPLICATION_VERSION);
            metricWrapper.setSpringBootStartDuration(springBootStartTime);
            metricWrapper.setSpringBootReadyTime(endTime);
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

            try {
                String metricsJson = mapper.writeValueAsString(metricWrapper);
                log.info("Metrics: " + metricsJson);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startApp() {
        endTime = Instant.now();
    }
}
