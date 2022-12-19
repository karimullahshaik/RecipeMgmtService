# Recipe Management Application
Recipe Web Service is specifically designed and implemented as part of ABN AMRO BE Assessment. Recipe Web Service contains ReST APIs in order to Create, Get, Update and Delete recipe from the database and render the requested details as JSON response to end user. 
The response from ReST APIs can be further integrated with front end view for better presentation.

### System Design
Recipe Web Service is microservice based layered architectured RESTful Web Service. This service can be deployed independently on premise / cloud and can also be containerized to execute as docker containers. There are 4 layers from top to bottom:
- API Layer
  - Top layer, which is main interface available for intgeration and interaction with front-end or end user to consume APIs
  - Contains secured API end points implementation
  - [Springboot-starter-security](https://spring.io/guides/gs/securing-web/) Module along with JWT is used to implement authentication for APIs 
  - [Springboot-starter-web](https://spring.io/guides/gs/rest-service/) module used as a framework to implement ReSTful api end points  
- Service Layer
  - This layer sits in between API layer and Data access layer with some utility functionality
  - Mainly responsible for interacting with Data Access Layer and transferring the recipes data as required by top and below layers
  - It's just another module added to decouple business logic of recipes data transfer and mapping from/to API layer
  - Further, service layer can be enhanced to support advanved features like Caching, Interacting with external Authorization Service etc
- Data Access Layer
  - Responsible to provide Object Relationship Mapping (ORM) between higher level recipe Java objects and persistence layer tables
  - [Springboot-starter-data-JPA](https://spring.io/guides/gs/accessing-data-jpa/) module is used to implement mappings between objects and tables
  - This layer contains recipe entity classes and JPA repositories which implement lower level functionality of storing/retrieving recipes data  
- Persistence Layer
  - Bottom most layer, responsible for physically storing the recipes data onto database table
  - Just one physical table - `recipes` is used to store the recipes data for the service
  - [MySQL]((https://www.mysql.com/) is configured to be used as database service
  - For development and testing purposes, the Embedded H2 Database provided by Spring Boot framework is also utilized 

### Webservice API Flow
![Recipe Webservice API Flow](https://github.com/karimullahshaik/RecipeMgmtService/blob/develop/recipe-web-service-flow.png)

### Supported Features
Feature | Software Module Used
------------ | -------------
ReSTful API | [Springboot](https://spring.io/projects/spring-boot)
API Authentication | [Spring Security](https://spring.io/projects/spring-security) with JWT Token
Object Relationship Mapping | [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
Exception Handling | [Controller Advice and ExceptionHandler](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc)
Logging | [SLF4J](http://www.slf4j.org/manual.html) Logger
Unit Tests | Junit 5 with [AssertJ](https://assertj.github.io/doc/)

### Prerequisites
* [JDK 1.8](https://www.oracle.com/in/java/technologies/javase/javase-jdk8-downloads.html)
* [Apache Maven](https://maven.apache.org/)
* [MySQL](https://www.mysql.com/)
* [Git](https://git-scm.com/)

### Steps to build Web Service
* Download code zip / `git clone https://github.com/karimullahshaik/RecipeMgmtService`
* Move to `RecipeMgmtService` and run maven build command `mvn clean package`
* To build by skipping unit tests run maven command `mvn clean package -DskipTests`
* On successfull build completion, one should have web service jar in `target` directory named as `RecipeMgmtService-1.0.jar`

### Steps to execute Web Service
* **Execution on Development profile with Embedded H2 Database**
  - In Development Mode, by default web service uses [Embedded H2 database](https://spring.io/guides/gs/accessing-data-jpa/) for persisting and retrieving recipes details.
  - Command to execute: 
   ```
        java -jar target/RecipeMgmtService-1.0.jar --spring.profiles.active=dev --logging.level.root=INFO
   ```
  - On successfull start, one should notice log message on console `Tomcat started on port(s): 9000 (http)` and have web service listening for web requests at port 9000
* **Execution on Development profile with MySQL Database**
  - In Development mode, one can also execute web service against local [MySQL Service](https://www.mysql.com/) for persisting and retrieving recipes details.
  - Specify required [MySQL Service](https://spring.io/guides/gs/accessing-data-mysql/) configuraiton parameters in `application.properties` file as given below:
    -  server.port=9000
    -  spring.profiles.active=dev
    -  spring.jpa.hibernate.ddl-auto=update
    -  spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/db_name
    -  spring.datasource.username=mysql-username
    -  spring.datasource.password=mysql-user-password
    -  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  - Web service needs database table with name `RECIPES` to be present in configured MySQL Database. Use below given table schema to create one before execution
    - CREATE TABLE recipes(id INT PRIMARY KEY, name VARCHAR(50), type VARCHAR(4),cdatetime TIMESTAMP, capacity INT, ingredients TEXT, instructions TEXT);  
  - Make sure MySQL Service is running on locallhost and listening at default port 3306
  - Command to execute with `mysql` profile: 
  ```
  java -jar target/RecipeMgmtService-1.0.jar --spring.config.location=src/main/resources/application-mysql.properties --logging.level.root=INFO
  ```
  - On successful start, one should notice log message on console `Tomcat started on port(s): 9000 (http)` and have web service listening for web requests at port 9000
* **Execution on Production Profile with MySQL Database**
  - In order to execute on Production, set the required configuration parameters in application.properties file
    -  server.port=required-port-number
    -  spring.profiles.active=prod
    -  spring.jpa.hibernate.ddl-auto=update
    -  spring.datasource.url=jdbc:mysql://${MYSQL_HOST:hostName}:port-Number/db_name
    -  spring.datasource.username=mysql-username
    -  spring.datasource.password=mysql-user-password
    -  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  - Web service needs database table with name `RECIPES` to be present in configured MySQL Database. Use below given table schema to create one before execution
    - CREATE TABLE recipes(id INT PRIMARY KEY, name VARCHAR(50), type VARCHAR(4),cdatetime TIMESTAMP, capacity INT, ingredients TEXT, instructions TEXT);    
  - Command to execute with custom application.properties file: 
  ```
  java -jar target/RecipeMgmtService-1.0.jar --spring.config.location=application.properties
  ```
  - On successful start, one should have web service listening for web requests at specified port in `application.properties` file interacting with configured production grade MySQL Service for persistence and retrieval of recipes details.      

### Web Service ReST API End Points
Recipe Webservice comes with ReST API Ends points for authentication, creating a new recipe, retrieveing an existing recipe, retrieving all existing recieps as list, updating an an existing recipe and deleting an existing recipe. Below table lists and describes on the implemented ReST APIs:
**Note: With all given below api end points request, make sure to include header `Content-Type as application/json`**
API End Point | Method | Purpose | Request | Response
------------ | ------------- | ------------- | ------------ | ------------- 
/api/authenticate | POST | Authenticate and get JWT Token | User Model with user name and password | JWT Token on Success, 403 Forbidden on failure
/api/recipe | POST | Create a new recipe | Recipe Model and valid JWT Token as bearer token as auth header| Recipe Model with 201 Created on Success, 400 Bad request on failure
/api/recipe/{id} | GET | Get an existing recipe | Recipe id as path parameter and valid JWT Token as bearer token as auth header | Recipe Model with 200 OK on Success, 401 Not Found on failure
/api/recipes | GET | Gel all existing recipes as list | Valid JWT Token as bearer token as auth header | Recipes as list with 200 OK on success, 401 Not Found on failure
/api/recipe | PUT | Update an existing recipe | Updated Recipe Model and valid JWT Token as bearer token as auth header | Recipe Model with 200 OK on Success, 401 Not Found on failure
/api/recipe/{id} | DELETE | Delete an existing recipe | Recipe id as path parameter and valid JWT Token as bearer token as auth header | Deletion message with 200 OK on success, 401 Not Found on failure

### Web Service ReST End Points Usage and Sample Response
In order to consume Recipe Webservice ReST API End points, one has to first authenticate and get JWT Token in order to place subsequent client requests. There are 2 object models needed to be aware of - one for Authentication and the other one for Recipe contents. Below given are details and examples on needed models:
- **User Model**
  - JSON Schema
    ```
    {
	    "userName": "Username as configured for recipe web service",
	    "password": "JWT Secret Key as configured for recipe web service"
    }
    ```
  - JSON Example
    ```
    {
	     "userName": "abnamro",
	     "password": "recipeKey"
    }
    ```
- **Recipe Model**
  - JSON Schema
    ```
    {
	      "id": "recipeId as integer value",
	      "name": "recipeName as string",
	      "type": "recipeType - ng/vg/eg as string",
	      "servingCapacity": "number of people the dish to be served as integer value",
	      "creationDateTime": "creation date time as Date or null",
	      "ingredients": "list of ingredients objects with name and quantity as fields or null",
	      "instructtions": "step by step procedure to prepare recipe as text or null"
    }
    ```
  - JSON Example 
    ```
    {
	      "id": 101,
	      "name": "Sweet Bun",
	      "type": "vg",
	      "servingCapacity": 5,
	      "creationDateTime": null,
	      "ingredients": [{
		                      "name": "all purpose floor",
		                      "quantity": "500 gms"
	                      }, {
		                      "name": "yeast",
		                      "quantity": "5 gms"
	                      }, {
		                      "name": "sugar",
		                      "quantity": "10 gms"
	                      }, {
		                      "name": "milk",
		                      "quantity": "100 ml"
	                      }],
	       "instructtions": "1.Activate the yeast by with milk and sugar \n 2.Take all purpose floor in mixing bowl and mix with sugar and milk \n 3.Mix yeast with floor in bowl\n 4.Wait for few minutes to raise and then put inti oven for about 20-25 minutes at 180 degree temperatur"
      }
    ```
- ReST API Calls and responses
  - POST request to `/api/authenticate` end point with above given user model :
  ```
  	{
    		"userName": "abnamro",
    		"password": "Bearer header.payload.signature"
		}
  ```
  - POST request to `/api/recipe` end point with sample recipe model and JWT Token as auth header returned in call to `/api/authenticate` :
  ```
  	{
    		"id": 102,
    		"name": "White Forest Pastry",
    		"type": "eg",
    		"servingCapacity": 10,
    		"ingredientsList": [
        				{
            					"name": "eggs",
            					"quantity": "10 nos"
        				},
        				{
            					"name": "floor",
            					"quantity": "500 gms"
        				},
        				{
            					"name": "vipping",
            					"quantity": "250 gms"
        				}
    				],
    		"creationDateTime": "2021-07-01T15:21:34.561+00:00",
    		"instructions": "Step by Step procedure to prepare Butter Sponge Cake:\nStep-1:\nStep-2:\nStep-3:\nStep-4:\nStep-5:",
    		"cdateTimeString": "01-07-2021 20:51:34"
	}
  ```
  - GET request to `/api/recipe/{id}` end point with path parameter as recipe id: `/api/recipe/102` end point :
  ```
  	{
    		"id": 102,
    		"name": "White Forest Pastry",
    		"type": "eg",
    		"servingCapacity": 10,
    		"ingredientsList": [
        				{
            					"name": "eggs",
            					"quantity": "10 nos"
        				},
        				{
            					"name": "floor",
            					"quantity": "500 gms"
        				},
        				{
            					"name": "vipping",
            					"quantity": "250 gms"
        				}
    				],
    		"creationDateTime": "2021-07-01T15:21:34.561+00:00",
    		"instructions": "Step by Step procedure to prepare Butter Sponge Cake:\nStep-1:\nStep-2:\nStep-3:\nStep-4:\nStep-5:",
    		"cdateTimeString": "01-07-2021 20:51:34"
	}
  ```
  - GET request to `/api/recipes` end point :
  ```
  	[
    		{
        		"id": 101,
        		"name": "Sweet Bun",
        		"type": "vg",
        		"servingCapacity": 5,
        		"ingredientsList": [],
        		"creationDateTime": "2021-07-01T15:19:27.809+00:00",
        		"instructions": null,
        		"cdateTimeString": "01-07-2021 20:49:27"
    		},
    		{
        		"id": 102,
        		"name": "White Forest Pastry",
        		"type": "eg",
        		"servingCapacity": 10,
        		"ingredientsList": [
            					{
                					"name": "eggs",
                					"quantity": "10 nos"
            					},
            					{
                					"name": "floor",
                					"quantity": "500 gms"
            					},
            					{
                					"name": "vipping",
                					"quantity": "250 gms"
            					}
        				],
        		"creationDateTime": "2021-07-01T15:21:34.561+00:00",
        		"instructions": "Step by Step procedure to prepare Butter Sponge Cake:\nStep-1:\nStep-2:\nStep-3:\nStep-4:\nStep-5:",
        		"cdateTimeString": "01-07-2021 20:51:34"
    		}
	]
  ```
  - PUT request to `/api/recipe` end point with updated model with recide id 102 and name renamed to `Sponge Cake` and servingCapacity to 4 :
  ```
  		{
    			"id": 102,
    			"name": "Sponge Cake",
    			"type": "eg",
    			"servingCapacity": 4,
    			"ingredientsList": [
        					{
            						"name": "eggs",
            						"quantity": "10 nos"
        					},
        					{
            						"name": "floor",
            						"quantity": "500 gms"
        					},
        					{
            						"name": "vipping",
            						"quantity": "250 gms"
        					}
    					],
    			"creationDateTime": "2021-06-30T15:10:59.642+00:00",
    			"instructions": "Step by Step procedure to prepare Butter Sponge Cake:\nStep-1:\nStep-2:\nStep-3:\nStep-4:\nStep-5:",
    			"cdateTimeString": "30-06-2021 20:40:59"
		}
  ```
  - GET request to `/api/recipe/{id}` end point with path parameter as recipe id: `/api/recipe/102`, one should notice updated fields in previous request reflected, i.e. recipe name and serving capacity:
  ```
  		{
    			"id": 102,
    			"name": "Sponge Cake",
    			"type": "eg",
    			"servingCapacity": 4,
    			"ingredientsList": [
        					{
            						"name": "eggs",
            						"quantity": "10 nos"
        					},
        					{
            						"name": "floor",
            						"quantity": "500 gms"
        					},
        					{
            						"name": "vipping",
            						"quantity": "250 gms"
        					}
    					],
    			"creationDateTime": "2021-06-30T15:10:59.642+00:00",
    			"instructions": "Step by Step procedure to prepare Butter Sponge Cake:\nStep-1:\nStep-2:\nStep-3:\nStep-4:\nStep-5:",
    			"cdateTimeString": "30-06-2021 20:40:59"
		}
  ```
  - DELETE request to `/api/recipe/{id}` end point with path parameter as recipe id: `/api/recipe/101`, one should notice response message and status code as 200 OK :
  ```
  		Requested recipe deleted from DB
  ```
  - GET request to `/api/recipe/{id}` end point with path parameter as recipe id: `/api/recipe/101`, one should notice response message and status code as 401 Not Found :
  ```
  		{
    			"status": 404,
    			"message": "404 NOT_FOUND \"Requested recipe not found in DB\"",
    			"dateTime": "2021-07-01T21:08:53.797"
		}
  ```
  - GET request to `/api/recipes` end point should return only one recipe with id 102:
  ```
  	[
    		{
        		"id": 102,
        		"name": "Sponge Cake",
        		"type": "eg",
        		"servingCapacity": 4,
        		"ingredientsList": [
            					{
                					"name": "eggs",
                					"quantity": "10 nos"
            					},
            					{
                					"name": "floor",
                					"quantity": "500 gms"
            					},
            					{
                					"name": "vipping",
                					"quantity": "250 gms"
            					}
        		],
        		"creationDateTime": "2021-06-30T15:10:59.642+00:00",
        		"instructions": "Step by Step procedure to prepare Butter Sponge Cake:\nStep-1:\nStep-2:\nStep-3:\nStep-4:\nStep-5:",
        		"cdateTimeString": "30-06-2021 20:40:59"
    		}
	]
  ```
  
### Future Enhancements
- Integrate Web Service with Authorization server for authentication and authorization
- Design and Implementation of Multi Factor Authentication feature
- Design and build simple,beautiful front end view
- Integration between Backend API with Front End view
- Data Normalization in Persistence layer if recipes data grows exponentially

