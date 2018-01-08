package com.amitbansal7.typeahead.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amitbansal7.typeahead.service.NodeService;


@Controller
public class NodeController {

	
	@Autowired
	private NodeService nodeService;

	
	@RequestMapping(value="/", method=RequestMethod.POST)
	public String insert(@RequestParam("qry") String word) {	
		nodeService.insert(word);
		return "home";
	}
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home() {
		 return "home";
	}
	
	@RequestMapping(value="/s/{word}", method=RequestMethod.GET)
	public void suggest(@PathVariable String word, Model model) {
		 model.addAttribute("suggestions",nodeService.suggest(word));
	}
}
