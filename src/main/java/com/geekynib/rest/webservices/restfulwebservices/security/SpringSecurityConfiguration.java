package com.geekynib.rest.webservices.restfulwebservices.security;

//this import is for basic
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*
 * Here we creating a Configuartion class for customising Spring Security
 * Here method is ovverride which is called while authentication
 */
@Configuration
public class SpringSecurityConfiguration {
 
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Steps to add in Filter Chain
		 // 1.All requests should be authenticated
		   http.authorizeHttpRequests(
				   auth->auth.anyRequest().authenticated()
				   );
		   
		
		// 2.If a request is not authenticated,web page is shown
		 http.httpBasic(withDefaults());
		   
		   // 3.CSRF->POST,PUT
		 //disabling csrf
		 http.csrf().disable();
		 
		
		return http.build();
	}
}
