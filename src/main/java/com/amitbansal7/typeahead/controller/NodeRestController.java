package com.amitbansal7.typeahead.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amitbansal7.typeahead.service.NodeService;

@RestController
public class NodeRestController {

	@Autowired
	private NodeService nodeService;
	
	@RequestMapping(value="/{word}", method=RequestMethod.GET)
	public List<String> suggest(@PathVariable String word) {
		return nodeService.suggest(word);
	}
	
	@RequestMapping(value="/{word}", method=RequestMethod.POST)
	public void insert(@PathVariable String word) {
		nodeService.insert(word);
	}
}
