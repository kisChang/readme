package io.kischang.readme.mail.tests;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.james.metrics.api.TimeMetric;

import com.google.common.base.Stopwatch;

public class RecordingTimeMetric implements TimeMetric {
    static class DefaultExecutionResult implements ExecutionResult {
        private final Duration elasped;

        DefaultExecutionResult(Duration elasped) {
            this.elasped = elasped;
        }

        @Override
        public Duration elasped() {
            return elasped;
        }

        @Override
        public ExecutionResult logWhenExceedP99(Duration thresholdInNanoSeconds) {
            return this;
        }
    }

    private final String name;
    private final Stopwatch stopwatch = Stopwatch.createStarted();
    private final Consumer<Duration> publishCallback;

    RecordingTimeMetric(String name, Consumer<Duration> publishCallback) {
        this.name = name;
        this.publishCallback = publishCallback;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ExecutionResult stopAndPublish() {
        Duration elapsed = Duration.ofNanos(stopwatch.elapsed(TimeUnit.NANOSECONDS));
        publishCallback.accept(elapsed);
        return new DefaultExecutionResult(elapsed);
    }
}