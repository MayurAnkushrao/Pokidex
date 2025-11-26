package com.example.pokedex_backend.service;

import com.example.pokedex_backend.dto.PokemonDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PokemonService {

	private final WebClient webClient;

	@Value("${pokeapi.base-url}")
	private String pokeApiBase;

	public PokemonService(WebClient.Builder builder, @Value("${pokeapi.base-url}") String baseUrl) {
		this.webClient = builder.baseUrl(baseUrl).build();
	}

	// Cached method: key is pokemon name (lowercase)
	@Cacheable(value = "pokemon", key = "#name.toLowerCase()")
	public PokemonDto getPokemonByName(String name) {
		String normalized = name.trim().toLowerCase();

		JsonNode vendor = webClient.get().uri(uriBuilder -> uriBuilder.path("/pokemon/{name}").build(normalized))
				.retrieve()
				.onStatus(status -> status.is4xxClientError(),
						resp -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pokemon not found")))
				.onStatus(status -> status.is5xxServerError(),
						resp -> Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Vendor error")))
				.bodyToMono(JsonNode.class).block();

		if (vendor == null)
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty vendor response");

		PokemonDto dto = new PokemonDto();
		dto.setName(vendor.path("name").asText());
		dto.setId(vendor.path("id").asInt());
		dto.setHeight(vendor.path("height").asInt());
		dto.setWeight(vendor.path("weight").asInt());

		List<String> types = StreamSupport.stream(vendor.path("types").spliterator(), false)
				.map(node -> node.path("type").path("name").asText()).collect(Collectors.toList());
		dto.setTypes(types);

		String front = vendor.path("sprites").path("front_default").asText(null);
		String artwork = vendor.path("sprites").path("other").path("official-artwork").path("front_default")
				.asText(null);
		dto.setSpriteFront(front);
		dto.setArtwork(artwork);

		vendor.path("stats").forEach(s -> {
			String statName = s.path("stat").path("name").asText();
			int val = s.path("base_stat").asInt();
			dto.getStats().put(statName, val);
		});

		List<String> abilities = StreamSupport.stream(vendor.path("abilities").spliterator(), false)
				.map(a -> a.path("ability").path("name").asText()).collect(Collectors.toList());
		dto.setAbilities(abilities);

		List<String> moves = StreamSupport.stream(vendor.path("moves").spliterator(), false)
				.map(m -> m.path("move").path("name").asText()).limit(15).collect(Collectors.toList());
		dto.setMoves(moves);

		dto.setCached(true);
		dto.setFetchedAt(Instant.now().toString());
		return dto;
	}

}
