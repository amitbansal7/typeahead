package com.amitbansal7.typeahead;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.amitbansal7.typeahead"})
@SpringBootApplication
public class TypeaheadApplication {

	public static void main(String[] args) {
		SpringApplication.run(TypeaheadApplication.class, args);
	}
}
