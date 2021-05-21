package com.microservice.movieinfoservice.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.microservice.movieinfoservice.models.Movie;
import com.microservice.movieinfoservice.models.MovieSummary;

@RestController
@RequestMapping("/movies")
public class MovieResource {
	
	@Value("${api.key}")
	private String apiKey;
	
	@Autowired
	private RestTemplate restTemplate;

	/*
	 * @RequestMapping("/{movieId}") public Movie getMovie(@PathVariable("movieId")
	 * String movieId) { return new Movie(movieId, "Test Name"); }
	 */
	
	@RequestMapping("/{movieId}")
	public Movie getMovieInfo(@PathVariable("movieId") String movieId) {
		
		String url = "https://api.themoviedb.org/3/movie/" +movieId + "?api_key=" + apiKey ;
		System.out.println(" URL ==> "+ url);
		
		MovieSummary movieSummary = restTemplate.getForObject(url, MovieSummary.class);
		
		return new Movie(movieId, movieSummary.getTitle());
	}

}
