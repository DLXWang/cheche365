package com.cheche365.cheche.scheduletask.service.prometheus;

import io.prometheus.client.Collector;
import java.util.ArrayList;
import java.util.List;

public class ExporterRegister {

    private List<Collector> collectors;

    public ExporterRegister(List<Collector> collectors) {
        for (Collector collector : collectors) {
            collector.register();
        }
        this.collectors = collectors;
    }

    public List<Collector> getCollectors() {
        return collectors;
    }




}
