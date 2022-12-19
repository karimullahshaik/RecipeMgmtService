package com.abnamro.recipes.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

//Class to authenticate and authroize JWT Token
@Component
@Slf4j
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	private final String HEADER = "Authorization";
	private final String PREFIX = "Bearer ";
	
	@Value("${recipe.user.name}")
	private String userName;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
																throws ServletException, IOException 
	{
		log.debug("Performing validations in doFilterInternal");
		try {
			if (checkJWTToken(request, response)) {
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userName, null,null);
				SecurityContextHolder.getContext().setAuthentication(auth);
				log.debug("SecurityContextHolder set with proper auth");
			} else {
				String errMsg = "JWT Token check failed, clearing SecurityContextHolder";
				log.error(errMsg);
				SecurityContextHolder.clearContext();
			}
			chain.doFilter(request, response);
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			log.error("Exception caught during security filter chain validation");
			ExceptionUtils.getStackTrace(e);
			return;
		}
	}	

	private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX)) {
			log.error("Invalid JWT Token Provided, JWT Token check failed!!!");
			return false;
		}
		log.debug("Given JWT Token is valid, JWT Token check success");
		return true;
	}

}
