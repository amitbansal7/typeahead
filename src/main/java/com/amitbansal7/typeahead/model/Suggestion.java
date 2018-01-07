package com.amitbansal7.typeahead.model;

import org.springframework.data.mongodb.core.mapping.Document;

//This Class contains a word along with its frequency

@Document(collection="suggestion")
public class Suggestion implements Comparable<Suggestion> {

	private String word;
	private Integer freq;

	public Suggestion(String word) {
		this.word = word;
		freq = 1;
	}

	public void incrementFreq() {
		this.freq++;
	}

	public String getWord() {
		return word;
	}

	public Integer getFreq() {
		return freq;
	}

	@Override
	public int compareTo(Suggestion that) {
		return that.getFreq() - this.freq;
	}
}
