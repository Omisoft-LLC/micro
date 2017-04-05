package com.omisoft.server.common.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Slf4jReporter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by dido on 10.06.16.
 */
@Slf4j
public class MetricsService {

  private static final String GRAPHITE_SERVER = "graphite.omisoft.eu";

  public static void initMetrics(String appName) {
    log.info("INITING METRICS");
    MetricRegistry registry =
        SharedMetricRegistries.getOrCreate("metricsRegistry");
    final JmxReporter jmxreporter = JmxReporter.forRegistry(registry).build();
    jmxreporter.start();

    final Slf4jReporter logreporter = Slf4jReporter.forRegistry(registry)
        .outputTo(LoggerFactory.getLogger("com.omisoft.metrics." + appName))
        .convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
    logreporter.start(5, TimeUnit.MINUTES);


  }

}
