package com.amitbansal7.typeahead.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="TrieNode")
public class Node {

	@Id
	private String id;
	@Indexed()
	private String prefix;
	private List<Suggestion> suggestions;
	private static final Integer MAXSIZE = 10;


	public Node(String prefix) {
		this.prefix = prefix;
		suggestions = new ArrayList<>();
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public int getSuggestionsCount() {
		return suggestions.size();
	}

	public List<String> getSuggestions(){
		Collections.sort(suggestions);
		List<String> res = new ArrayList<>();
		for(Suggestion s: suggestions){
			res.add(s.getWord());
		}

		return res;
	}

	public void addWord(String word) {
		Boolean found = false;
		for(Suggestion suggestion:suggestions) {
			if(suggestion.getWord().equals(word)) {
				suggestion.incrementFreq();
				found = true;
				break;
			}
		}
		if(found == false)
			suggestions.add(new Suggestion(word));

		Collections.sort(suggestions);
		if(suggestions.size() > MAXSIZE)
			suggestions.remove(suggestions.size()-1);
	}
}
