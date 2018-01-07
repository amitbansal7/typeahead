package com.amitbansal7.typeahead.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.amitbansal7.typeahead.model.Node;

@Repository
public interface NodeRepository extends MongoRepository<Node, String>{
	
	//ok
	public boolean existsByPrefix(String id);
	
	
	@Query(value="{\"prefix\":?0, \"suggestions.word\":?1}")
	public boolean existsByWord(String prefix, String word);
	
	public Node findByPrefix(String prefix); 
	
	@Query(value="{\"prefix\":?0},{$addToSet:{suggestions:{\"word\":?1,\"freq\":1\"}}}")
	public void updateByPrefix(String prefix, String word);
	
//	@Query(value="{\"prefix\":?0,\"suggestions.word\":?1},{$inc:{\"suggestions.$.freq\":1}}")
//	public void updateByPrefix(String prefix, String word);
	
	
	
}
