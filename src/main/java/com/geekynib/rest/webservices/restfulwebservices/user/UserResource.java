package com.geekynib.rest.webservices.restfulwebservices.user;

import java.net.URI;

import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
public class UserResource {

//	@Autowired
	private UserDaoService service;

	// Constructor Injection
	public UserResource(UserDaoService service) {
		this.service = service;
	}

	// GET/users
	@GetMapping("/users")
	public List<User> retrieveAllUsers() {
		return service.findAll();
	}

	//HATEOAS 
	///http://loclhost:8080/user
	//Entity Model
	//WebMVCLinkBuilder- Wrap the reponse in entity model
	// GET /user
	@GetMapping("/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable int id) {
		// Traditional Way
//    	 List<User> usersList=service.findAll();
//    	 User user=null;
//    	 for(User u:usersList) {
//    		 if(u.getId()==id) {
//    			 user=u;
//    			 break;
//    		 }
//    	 }

		// using Functional Programming
		// but stillif it returns null then nothing on ui
		User user = service.findOne(id);
		if (user == null) {
			throw new UserNotFoundException("id" + id);
		}
		EntityModel<User> entityModel=EntityModel.of(user);
		//To add hyperlinks
	//here adding hyperlink for allUsers
		WebMvcLinkBuilder link=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
	//adding link key
		entityModel.add(link.withRel("all-users"));
		return entityModel;
	}

	// POST/users
	@PostMapping("/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		User savedUser = service.save(user);
		// here location saves uri of current location & replaces values of id &
		// returned back in body as location
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}") // replace with id
				.buildAndExpand(savedUser.getId()).toUri();
		// location -users/4
		return ResponseEntity.created(location).build();
	}

	// /Delete/User
	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable int id) {
		service.deleteById(id);
	}
}
