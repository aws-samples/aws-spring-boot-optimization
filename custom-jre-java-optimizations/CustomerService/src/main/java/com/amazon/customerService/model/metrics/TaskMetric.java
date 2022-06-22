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
import java.util.Objects;

public class TaskMetric {
    private Instant pullStartedAt;
    private Instant pullStoppedAt;
    private String taskArn;

    public TaskMetric() {}

    public TaskMetric(Instant pullStartedAt, Instant pullStoppedAt, String taskArn) {
        this.pullStartedAt = pullStartedAt;
        this.pullStoppedAt = pullStoppedAt;
        this.taskArn = taskArn;
    }

    public Instant getPullStartedAt() {
        return pullStartedAt;
    }

    public void setPullStartedAt(Instant pullStartedAt) {
        this.pullStartedAt = pullStartedAt;
    }

    public Instant getPullStoppedAt() {
        return pullStoppedAt;
    }

    public void setPullStoppedAt(Instant pullStoppedAt) {
        this.pullStoppedAt = pullStoppedAt;
    }

    public Duration calculateDuration() {
        if (pullStartedAt == null || pullStoppedAt == null)
            return null;

        Duration duration = Duration.between(pullStartedAt, pullStoppedAt);

        return duration;
    }

    public String getTaskArn() {
        return taskArn;
    }

    public void setTaskArn(String taskArn) {
        this.taskArn = taskArn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskMetric that = (TaskMetric) o;
        return taskArn.equals(that.taskArn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskArn);
    }

    @Override
    public String toString() {
        return "TaskMetric{" +
                "pullStartedAt=" + pullStartedAt +
                ", pullStoppedAt=" + pullStoppedAt +
                ", taskArn='" + taskArn + '\'' +
                '}';
    }
}
