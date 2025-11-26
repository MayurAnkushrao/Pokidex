package com.example.pokedex_backend.dto;

import java.util.*;

public class PokemonDto {
    private String name;
    private int id;
    private int height;
    private int weight;
    private List<String> types = new ArrayList<>();
    private String spriteFront;
    private String artwork;
    private Map<String, Integer> stats = new LinkedHashMap<>();
    private List<String> abilities = new ArrayList<>();
    private List<String> moves = new ArrayList<>();
    private boolean cached;
    private String fetchedAt;

   

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public List<String> getTypes() {
		return types;
	}
	public void setTypes(List<String> types) {
		this.types = types;
	}
	public String getSpriteFront() {
		return spriteFront;
	}
	public void setSpriteFront(String spriteFront) {
		this.spriteFront = spriteFront;
	}
	public String getArtwork() {
		return artwork;
	}
	public void setArtwork(String artwork) {
		this.artwork = artwork;
	}
	public Map<String, Integer> getStats() {
		return stats;
	}
	public void setStats(Map<String, Integer> stats) {
		this.stats = stats;
	}
	public List<String> getAbilities() {
		return abilities;
	}
	public void setAbilities(List<String> abilities) {
		this.abilities = abilities;
	}
	public List<String> getMoves() {
		return moves;
	}
	public void setMoves(List<String> moves) {
		this.moves = moves;
	}
	public boolean isCached() {
		return cached;
	}
	public void setCached(boolean cached) {
		this.cached = cached;
	}
	public String getFetchedAt() {
		return fetchedAt;
	}
	public void setFetchedAt(String fetchedAt) {
		this.fetchedAt = fetchedAt;
	}

    
}
