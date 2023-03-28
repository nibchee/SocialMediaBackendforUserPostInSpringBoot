package com.geekynib.rest.webservices.restfulwebservices.user;

import java.net.URI;

import java.util.List;
import java.util.Optional;

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

import com.geekynib.rest.webservices.restfulwebservices.jpa.PostRepository;
import com.geekynib.rest.webservices.restfulwebservices.jpa.UserRepository;

import jakarta.validation.Valid;

@RestController
public class UserJPAResource {

	// Here Using User Repository
//	@Autowired
	//private UserDaoService service;

	// Constructor Injection

	private UserRepository userRepository;

	private PostRepository postRepository;

	public UserJPAResource(UserRepository userRepository,PostRepository postRepository) {
		//this.service = service;
		this.userRepository = userRepository;
		this.postRepository=postRepository;
	}

	// GET/users
	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers() {
		return userRepository.findAll();
		// return service.findAll();
	}

	// HATEOAS
	/// http://loclhost:8080/user
	// Entity Model
	// WebMVCLinkBuilder- Wrap the reponse in entity model
	// GET /user
	@GetMapping("/jpa/users/{id}")
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
		// but still if it returns null then nothing on ui
		// User user = service.findOne(id);
		Optional<User> user = userRepository.findById(id);

		if (user.isEmpty()) {
			throw new UserNotFoundException("id" + id);
		}
		EntityModel<User> entityModel = EntityModel.of(user.get());
		// To add hyperlinks
		// here adding hyperlink for allUsers
		WebMvcLinkBuilder link = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
		// adding link key
		entityModel.add(link.withRel("all-users"));
		return entityModel;
	}

	// POST/users
	@PostMapping("/jpa/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		//User savedUser = service.save(user);
		User savedUser = userRepository.save(user);
		
		// here location saves uri of current location & replaces values of id &
		// returned back in body as location
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}") // replace with id
				.buildAndExpand(savedUser.getId()).toUri();
		// location -users/4
		return ResponseEntity.created(location).build();
	}

	// /Delete/User
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
		userRepository.deleteById(id);
		//service.deleteById(id);
	}
	
	//-----Post Specific Methods---------
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrievePostForUser(@PathVariable int id) {
	//Getting user
	Optional<User> user=userRepository.findById(id);
	if(user.isEmpty()) {
		throw new UserNotFoundException("id: "+id);
	}
	
	//getting all posts
	return user.get().getPosts();
	}
	

	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<Post> createPostForUser(@PathVariable int id,@Valid @RequestBody Post post) {
	//Getting user
	Optional<User> user=userRepository  .findById(id);
	if(user.isEmpty()) {
		throw new UserNotFoundException("id: "+id);
	}
	
	post.setUser(user.get());
	
	Post savedPost=postRepository.save(post);
	
	
	// here location saves uri of current location & replaces values of id &
	// returned back in body as location
	URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}") // replace with id
			.buildAndExpand(savedPost.getId()).toUri();
	// location -users/4
	return ResponseEntity.created(location).build();

	}
	

}
