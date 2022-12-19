-- Table to store recipe details
CREATE TABLE recipes(id INT PRIMARY KEY, name VARCHAR(50), type VARCHAR(4),cdatetime TIMESTAMP, capacity INT,
                      ingredients TEXT, instructions TEXT);
