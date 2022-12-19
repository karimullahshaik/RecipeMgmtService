package com.abnamro.recipes.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "Recipe")
public class RecipeEntity {
	@Id
	private Integer id;
	
	@Column(name = "NAME", unique = true)
	private String name;
	
	@Column(name = "TYPE")
	private String type;
	
	@Column(name = "CDATETIME", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDateTime;
	
	@Column(name = "CAPACITY")
	private Integer servingCapacity;
	
	@Column(name = "INGREDIENTS", nullable=true)
	private String ingredients;
	
	@Column(name = "INSTRUCTIONS", nullable=true)
	private String instructions;
}
