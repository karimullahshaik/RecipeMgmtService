package com.abnamor.recipes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Class to represent User and Credentials
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	private String userName;
	private String password;
}
