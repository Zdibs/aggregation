package com.aggregation.aggregation.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class AggregationResource {

    private Map<String, String> pricing;

    private Map<String, String> track;

    private Map<String, List<String>> shipments;
}
