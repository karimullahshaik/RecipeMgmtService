package com.abnamro.recipes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
    	log.debug("Configuring HttpSecurity Parameters...");
    	httpSecurity.csrf().disable()
    				.cors().disable()
    				.authorizeRequests()
    				.antMatchers("/").permitAll()
    				.antMatchers("/error").permitAll()
    				.antMatchers(HttpMethod.POST, "/api/authenticate").permitAll()
    				.anyRequest().authenticated()
    				.and()
    				.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
    				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
    
}
