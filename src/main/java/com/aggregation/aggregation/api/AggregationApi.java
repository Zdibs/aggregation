package com.aggregation.aggregation.api;

import java.util.List;

import com.aggregation.aggregation.AggregationUseCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AggregationApi {

	private final AggregationUseCaseService aggregationUseCaseService;

	@GetMapping("/aggregation")
	public AggregationResource aggregation(@RequestParam(value = "pricing") List<String> countryCodes, @RequestParam(value = "track") List<String> trackNumbers, @RequestParam(value = "shipments") List<String> shipments) {



		// TODO Could validate ISO countries Locale.getISOCountries()

		return aggregationUseCaseService.aggregate(countryCodes, trackNumbers, shipments);
	}
}
