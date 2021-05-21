package com.microservice.moviecatalogservice.resource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservice.moviecatalogservice.model.CatalogItem;
import com.microservice.moviecatalogservice.model.Movie;
import com.microservice.moviecatalogservice.model.Rating;
import com.microservice.moviecatalogservice.model.UserRating;
import com.microservice.moviecatalogservice.services.MovieInfo;
import com.microservice.moviecatalogservice.services.UserRatingInfo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	MovieInfo movieInfo;
	
	@Autowired
	UserRatingInfo userRatingInfo;
	
	
	// this can be used to programatically use different instances of the service
	@Autowired
	private DiscoveryClient discoveryClient;

	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
		
		// just go through the discoveryClient
		goThroughDiscoveryClient();
		
		// UserRating userRating = restTemplate.getForObject("http://localhost:9093/ratingsdata/users/" + userId, UserRating.class);
		
		// this rating-data-service is not a actual host or DNS, rather it is a service name registered to Discovery-service
		UserRating userRating = userRatingInfo.getUserRating(userId);

		return userRating.getRatings().stream().map(rating -> {
			// Movie movie = restTemplate.getForObject("http://localhost:9091/movies/" + rating.getMovieId(), Movie.class);
			System.out.println("rating.getMovieId() ==> "+ rating.getMovieId());

			// this movie-info-service is not a actual host or DNS, rather it is a service name registered to Discovery-service			
			return movieInfo.getCatalogItem(rating);
		}).collect(Collectors.toList());
	}


	public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId) {
		return Arrays.asList(
				new CatalogItem("No Movie", "No Desc", 0)
			);
	}

	private void goThroughDiscoveryClient() {
		
		//List<ServiceInstance> instances = discoveryClient.getInstances("rating-data-service");
		//System.out.println(instances.get(0).getUri());
		
		List<String> services = discoveryClient.getServices();
		for (String service : services) {
			System.out.println(service);
		}
		
	}
}

//@Autowired
//private WebClient.Builder webClientBuilder;

//webclient implementation - which is Asynchronous
/*Movie movie = webClientBuilder.build()
		.get()
		.uri("http://localhost:9091/movies/" + rating.getMovieId())
		.retrieve()
		.bodyToMono(Movie.class)
		.block();
*/

/*
return Collections.singletonList( 
	new CatalogItem("transforme", "test", 4) 
);
*/