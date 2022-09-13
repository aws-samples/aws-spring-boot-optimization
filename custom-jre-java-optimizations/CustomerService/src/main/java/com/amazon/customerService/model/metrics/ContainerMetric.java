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

public class ContainerMetric {
    private Instant createdAt;
    private Instant startedAt;

    public ContainerMetric() {
    }

    public ContainerMetric(Instant createdAt, Instant startedAt) {
        this.createdAt = createdAt;
        this.startedAt = startedAt;
    }

    public Duration calculateDuration() {
        if (startedAt == null || createdAt == null)
            return null;

        Duration duration = Duration.between(createdAt, startedAt);

        return duration;
    }

    @Override
    public String toString() {
        return "ContainerMetric{" +
                "createdAt=" + createdAt +
                ", startedAt=" + startedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerMetric that = (ContainerMetric) o;
        return createdAt.equals(that.createdAt) && startedAt.equals(that.startedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, startedAt);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }
}
