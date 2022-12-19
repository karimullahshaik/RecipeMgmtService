package com.abnamro.recipes.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.abnamor.recipes.dto.Ingredient;
import com.abnamor.recipes.dto.Recipe;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import lombok.extern.slf4j.Slf4j;

//Util Class to contain common utility methods
@Slf4j
public class Util {
	private static String PREFIX = "Bearer ";
	private static SignatureAlgorithm SA = SignatureAlgorithm.HS512; 
	private static String pattern = "dd-MM-yyyy HH:mm:ss";
	
	//Method to check given JWT Token validity
	public static Boolean checkJwtTokenValidity(String secretKey, String authHeader) {
		try {
			String tokenWithoutBearer = authHeader.replace(PREFIX,"");
			//Decode and extract chunks of JWT Token
			String[] chunks = tokenWithoutBearer.split("\\.");
			//String jwtHeader = new String(Base64.getDecoder().decode(chunks[0]));
			//String jwtPayload = new String(Base64.getDecoder().decode(chunks[1]));
			String jwtSignature = chunks[2];
			String jwtWithoutSignature = chunks[0] + "." + chunks[1];
			
			//log.debug("Jwt Header: "+jwtHeader);
			//log.debug("Jwt Payload: "+jwtPayload);
			
			//Validate authenticity of given JWT Token
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), SA.getJcaName());
			DefaultJwtSignatureValidator jwtValidator = new DefaultJwtSignatureValidator(SA, secretKeySpec);
			if(jwtValidator.isValid(jwtWithoutSignature, jwtSignature)) {
				log.debug("Given JWT Token is valid");
				return true;
			} else {
				log.error("Given JWT Token is invalid");
				return false;
			}
		} catch(Exception e) {
			log.error("Exception caught while decoding and verifying JWT Token Contents");
			ExceptionUtils.getStackTrace(e);
			return false;
		}
	}
	
	//Method to self generate JWT Token for authentication and authorization purposes
	public static String generateJWTToken(String userName, String secretKey) {
		log.debug("Generating JWT Token with provided User Credentials");
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList("ROLE_USER");

		String token = Jwts
				.builder()
				.setId("recipeJWT")
				.setSubject(userName)
				.claim("authorities",
						grantedAuthorities.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SA,secretKey.getBytes()).compact();
		log.debug("Generated Jwt Token of length: "+token.length());
		return PREFIX + token;

	}
	
	//Method to Get and return current date along with time in required format
	public static Optional<Date> getCurrentDateTime() {
		try {
			Date currentDateTime = Calendar.getInstance().getTime();
			log.debug("Current Date Time Value: "+currentDateTime.toString());
			return Optional.of(currentDateTime);
		}catch(Exception e) {
			log.error("Exception caught while getting current datetime");
			ExceptionUtils.getStackTrace(e);
			return Optional.empty();
		}
	}

	//Method to validate various fields present in given Recipe Entity
	public static Boolean checkRecipeValidity(Recipe recipe) {
		//Check for nullness
		if(recipe == null) {
			log.error("Given recipe instance is null");
			return false;
		} else if (recipe.getId() == null || recipe.getName() == null || 
				   recipe.getType() == null || recipe.getServingCapacity() == null) {
			log.error("One of non-null field is null in recipe instance");
			return false;
		} else
			return true;
	}
	
	//Convert given ingredients list to JsonString and return
	public static String convertToJSONString(List<Ingredient> ingList) {
		ObjectMapper mapper = new ObjectMapper(); 
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(ingList); 
		} catch(Exception e) {
			log.error("Exception caught while converting List to JSON String");
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return jsonString;
	}
	
	//Convert given JSON String to List of Ingredients
	public static List<Ingredient> convertJSONStringToIngredientsList(String jsonString){
		ObjectMapper mapper = new ObjectMapper();
		List<Ingredient> ingredientsList = null;
		//Convert JSON array to Array objects
		//Ingredient[] ingredients = mapper.readValue(jsonString, Ingredient[].class);
		try {
			//Convert JSON array to List of objects
			ingredientsList = Arrays.asList(mapper.readValue(jsonString, Ingredient[].class));
		} catch(Exception e) {
			log.error("Exception caught while converting JSON String to Ingredients List");
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return ingredientsList;
	}
	
	//Format given Date contents to 
	public static String formatDateTime(Date date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			String formattedDateTimeString = formatter.format(date);
			log.debug("Formatted DateTime: "+formattedDateTimeString);
			return formattedDateTimeString;
		}catch(Exception e) {
			log.error("Exception caught while formatting and parsing date time in "+pattern);
			ExceptionUtils.getStackTrace(e);
			return null;
		}
	}
}
