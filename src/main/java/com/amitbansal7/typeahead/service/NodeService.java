package com.amitbansal7.typeahead.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.amitbansal7.typeahead.model.Node;
import com.amitbansal7.typeahead.repository.NodeRepository;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

@Service
public class NodeService {

	@Autowired
	private NodeRepository nodeRepository;
	
	private final int MAXCOUNT = 10;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private final String COLLECTION ="TrieNode";
	
	public List<String> suggest(String word){
		return nodeRepository.findByPrefix(word).getSuggestions();
	}
	
	public void insert(String word) {
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
			Node e = nodeRepository.findByPrefix(tempPrefix);
			int count = e.getSuggestionsCount();
			
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
	}
}
