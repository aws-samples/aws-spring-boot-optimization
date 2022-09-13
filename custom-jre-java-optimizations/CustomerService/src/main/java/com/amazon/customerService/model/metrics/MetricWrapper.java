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

package com.amazon.customerService.model.metrics;

import java.time.Duration;
import java.time.Instant;

public class MetricWrapper {

    private ContainerMetric containerMetric;
    private TaskMetric taskMetric;
    private Duration springBootStartDuration;
    private Instant springBootReadyTime;
    private Integer version;

    public MetricWrapper() {
    }

    public MetricWrapper(ContainerMetric containerMetric, TaskMetric taskMetric, Duration springBootStartTime, Instant springBootReadyTime, Integer version) {
        this.containerMetric = containerMetric;
        this.taskMetric = taskMetric;
        this.springBootStartDuration = springBootStartTime;
        this.springBootReadyTime = springBootReadyTime;
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public ContainerMetric getContainerMetric() {
        return containerMetric;
    }

    public void setContainerMetric(ContainerMetric containerMetric) {
        this.containerMetric = containerMetric;
    }

    public TaskMetric getTaskMetric() {
        return taskMetric;
    }

    public void setTaskMetric(TaskMetric taskMetric) {
        this.taskMetric = taskMetric;
    }

    public Instant getSpringBootReadyTime() {
        return springBootReadyTime;
    }

    public void setSpringBootReadyTime(Instant springBootReadyTime) {
        this.springBootReadyTime = springBootReadyTime;
    }

    public Duration getSpringBootStartDuration() {
        return springBootStartDuration;
    }

    public void setSpringBootStartDuration(Duration springBootStartDuration) {
        this.springBootStartDuration = springBootStartDuration;
    }
}
