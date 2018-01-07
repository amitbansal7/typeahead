package com.amitbansal7.typeahead.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amitbansal7.typeahead.repository.NodeRepository;

@RestController
public class NodeRestController {

	@Autowired
	private NodeRepository nodeRepository;
	
	@RequestMapping(value="/{word}", method=RequestMethod.GET)
	public List<String> home(@PathVariable String word) {
//		 model.addAttribute("suggestions",nodeRepository.findByPrefix(word).getSuggestions());
		 return nodeRepository.findByPrefix(word).getSuggestions();
	}
}
