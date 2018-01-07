package com.amitbansal7.typeahead.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amitbansal7.typeahead.model.Node;
import com.amitbansal7.typeahead.repository.NodeRepository;

@Controller
public class NodeController {

//	private Node root;
	
	@Autowired
	private NodeRepository nodeRepository;
	
	@RequestMapping(value="/", method=RequestMethod.POST)
	public String inisert(@RequestParam("qry") String word) {
		
		for(int i=1;i<=word.length();i++) {
			String tempPrefix = word.substring(0, i);
			
			if(nodeRepository.existsByPrefix(tempPrefix) == false) {
				nodeRepository.save(new Node(tempPrefix));
			}
			
//			if(nodeRepository.existsByWord(tempPrefix, word) == false) {
//				nodeRepository.updateByPrefix(tempPrefix, word);
//			}
//			else {
//				continue;
//			}
		}
		return "home";
	}
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home() {
		 return "home";
	}
	
	@RequestMapping(value="/s/{word}", method=RequestMethod.GET)
	public void suggest(@PathVariable String word, Model model) {
		 model.addAttribute("suggestions",nodeRepository.findByPrefix(word).getSuggestions());
	}
//	
//	public List<String> suggest(String word) {
//		Node crawl = root;
//		List<String> res = new ArrayList<>();
//		for (char c : word.toCharArray()) {
//			if (crawl.children[c - firstChar] == null)
//				return res;
//
//			crawl = crawl.children[c - firstChar];
//		}
//
//		return crawl.getSuggestions();
//	}
//
//	//Inserts a query in Trie
//	public void insert(String word) {
//		Node crawl = this.root;
//		char firstChar = ' ';
//
//		for (char c : word.toCharArray()) {
//			if (crawl.children[c - firstChar] == null)
//				crawl.children[c - firstChar] = new Node();
//
//			crawl = crawl.children[c - firstChar];
//
//			crawl.addWord(word);
//		}
//	}
	
//	@RequestMapping(value="/",method=RequestMethod.GET )
//	public Node getOne() {
//		nodeRepository.save(new Node());
//		Node n =  nodeRepository.findAll().get(0);
//		String word = "abc";
//		for (char c : word.toCharArray()) {
//			if (n.children[c - 'a'] == null)
//				n.children[c - 'a'] = new Node();
//
//			n = n.children[c - 'a'];
//
//			n.addWord(word);
//		}
//		return nodeRepository.findAll().get(0);
//	}
}
