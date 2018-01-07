package com.amitbansal7.typeahead.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amitbansal7.typeahead.model.Node;
import com.amitbansal7.typeahead.repository.NodeRepository;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;


@Controller
public class NodeController {

//	private Node root;
	
	@Autowired
	private NodeRepository nodeRepository;
	
	private final int MAXCOUNT = 10;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private final String COLLECTION ="TrieNode";
	
	@RequestMapping(value="/", method=RequestMethod.POST)
	public String insert(@RequestParam("qry") String word) {	
		for(int i=1;i<=word.length();i++) {
			String tempPrefix = word.substring(0, i);
			
			if(nodeRepository.existsByPrefix(tempPrefix) == false) {
				nodeRepository.save(new Node(tempPrefix));
			}
			Criteria criteria = new Criteria().andOperator(Criteria.where("prefix").is(tempPrefix),Criteria.where("suggestions.word").is(word));
			if(mongoTemplate.exists(new Query(criteria), Node.class, COLLECTION) == false) {		
				BasicDBList updates = new BasicDBList();
				BasicDBObject jsonSug = new BasicDBObject("word",word);
				jsonSug.append("freq", 1);
				BasicDBObject sugg = new BasicDBObject("suggestions", jsonSug);
				BasicDBObject u = new BasicDBObject("$addToSet", sugg);
				BasicDBObject q = new BasicDBObject("prefix", tempPrefix);
				BasicDBObject oneQuery = new BasicDBObject();
				oneQuery.append("q", q);
				oneQuery.append("u", u);
				updates.add(oneQuery);
				BasicDBObject command = new BasicDBObject("update","TrieNode");
				command.append("updates", updates);
				mongoTemplate.executeCommand(command);
			}else {
				
				BasicDBList updates = new BasicDBList();
				BasicDBObject u = new BasicDBObject("$inc", new BasicDBObject("suggestions.$.freq",1));
				BasicDBObject q  = new BasicDBObject();
				q.append("prefix", tempPrefix);
				q.append("suggestions.word", word);
				BasicDBObject oneQuery = new BasicDBObject();
				oneQuery.append("q", q);
				oneQuery.append("u", u);
				updates.add(oneQuery);
				BasicDBObject command = new BasicDBObject("update", COLLECTION);
				command.append("updates", updates);
				mongoTemplate.executeCommand(command);
			}
			
			//sorting
			BasicDBList updates = new BasicDBList();
			BasicDBObject q  = new BasicDBObject("prefix",tempPrefix);
			BasicDBObject oneQuery = new BasicDBObject();
			oneQuery.append("q", q);
			BasicDBObject sortSuggestions = new BasicDBObject("$each", new BasicDBList());
			sortSuggestions.append("$sort", new BasicDBObject("freq",-1));
			BasicDBObject u = new BasicDBObject("$push", new BasicDBObject("suggestions",sortSuggestions));
			oneQuery.append("u", u);
			updates.add(oneQuery);
			BasicDBObject command = new BasicDBObject("update", COLLECTION);
			command.append("updates", updates);
			mongoTemplate.executeCommand(command);
			
			//find count
//			BasicDBObject qA  = new BasicDBObject("$match", new BasicDBObject("prefix",tempPrefix));
//			BasicDBObject commandA = new BasicDBObject("count", "TrieNode.suggestions");
//			
//			commandA.append("query", qA);
//			BasicDBObject query = new BasicDBObject("query")
//			MatchOperation res = mongoTemplate.executeCommand(commandA);
			
//			AggregationResults<DBObject> result = mongoTemplate.aggregate(
//					Aggregation.match(new Criteria().where("prefix").is(tempPrefix)), DBObject.class);
//			
//			Criteria SCriteria = Criteria.where("prefix").is("prefix");
//			MatchOperation matchOpr = Aggregation.match(SCriteria);
//			ProjectionOperation projOp = Aggregation.project("suggestions");
//			AggregationResults<DBObject> res = mongoTemplate.aggregate(Aggregation.newAggregation(
//					matchOpr,
//					projOp
//			    ), Node.class, DBObject.class);
//			
//			System.out.println(res.toString());
			
			int count = 0;
			//delete last if count is more than MAXCOUNT.
			if(count > MAXCOUNT) {
				BasicDBList updatesC = new BasicDBList();
				BasicDBObject qC  = new BasicDBObject("prefix",tempPrefix);
				BasicDBObject uC = new BasicDBObject("$pop", new BasicDBObject("suggestions",1));
				BasicDBObject oneQueryC = new BasicDBObject();
				oneQueryC.append("q", qC);
				oneQueryC.append("u", uC);
				updatesC.add(oneQueryC);
				BasicDBObject commandC = new BasicDBObject("update", COLLECTION);
				commandC.append("updates", updatesC);
				mongoTemplate.executeCommand(commandC);
			}
			
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
}
