package io.kischang.readme.mail.tests;

import static org.apache.james.metrics.api.TimeMetric.ExecutionResult.DEFAULT_100_MS_THRESHOLD;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.james.metrics.api.Metric;
import org.apache.james.metrics.api.MetricFactory;
import org.apache.james.metrics.api.TimeMetric;
import org.reactivestreams.Publisher;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import reactor.core.publisher.Flux;

public class RecordingMetricFactory implements MetricFactory {
    private final Multimap<String, Duration> executionTimes = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    @Override
    public Metric generate(String name) {
        return new RecordingMetric(atomicCounterFor(name));
    }

    private AtomicInteger atomicCounterFor(String name) {
        return counters.computeIfAbsent(name, currentName -> new AtomicInteger());
    }

    @Override
    public TimeMetric timer(String name) {
        return new RecordingTimeMetric(name, executionTime -> {
            synchronized (executionTimes) {
                executionTimes.put(name, executionTime);
            }
        });
    }

    @Override
    public <T> Publisher<T> decoratePublisherWithTimerMetric(String name, Publisher<T> publisher) {
        return Flux.using(() -> timer(name),
            any -> publisher,
            TimeMetric::stopAndPublish);
    }

    @Override
    public <T> Publisher<T> decoratePublisherWithTimerMetricLogP99(String name, Publisher<T> publisher) {
        return Flux.using(() -> timer(name),
            any -> publisher,
            timer -> timer.stopAndPublish().logWhenExceedP99(DEFAULT_100_MS_THRESHOLD));
    }

    public Collection<Duration> executionTimesFor(String name) {
        synchronized (executionTimes) {
            return executionTimes.get(name);
        }
    }

    public Multimap<String, Duration> executionTimesForPrefixName(String prefixName) {
        synchronized (executionTimes) {
            return Multimaps.filterKeys(executionTimes, key -> key.startsWith(prefixName));
        }
    }

    public int countFor(String name) {
        return atomicCounterFor(name).get();
    }

    public Map<String, Integer> countForPrefixName(String prefixName) {
        return counters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(prefixName))
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> e.getValue().get()));
    }
}