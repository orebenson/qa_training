-- Sakila questions:
use sakila;
--     1. List all actors.
select * from actor;
--     2. Find the surname of the actor with the forename 'John'.
select last_name from actor where first_name = 'John';
--     3. Find all actors with surname 'Neeson'.
select * from actor where last_name = 'Neeson';
--     4. Find all actors with ID numbers divisible by 10.
select * from actor where actor_id / 10 = 0;
--     5. What is the description of the movie with an ID of 100?
select description from film where film_id = 100;
--     6. Find every R-rated movie.
select * from film where rating = 'R';
--     7. Find every non-R-rated movie.
select * from film where rating <> 'R';
--     8. Find the ten shortest movies.
select * from film order by length asc limit 10;
--     9. Find the movies with the longest runtime, without using LIMIT.
select * from film order by length desc;
--     10. Find all movies that have deleted scenes.
select * from film where special_features like '%Deleted Scenes%';
--     11. Using HAVING, reverse-alphabetically list the last names that are not repeated.
select last_name from actor group by last_name having count(last_name)=1 order by last_name desc;
--     12. Using HAVING, list the last names that appear more than once, from highest to lowest frequency.
select last_name from actor group by last_name having count(last_name) > 1 order by count(last_name) desc;
--     13. Which actor has appeared in the most films?
select actor.first_name, actor.last_name, a.num_of_films from actor join (select actor_id, count(actor_id) as num_of_films from film_actor group by actor_id) a on actor.actor_id = a.actor_id order by a.num_of_films desc limit 1;
--     14. When is 'Academy Dinosaur' due?
select rental.return_date from rental join inventory on rental.inventory_id = inventory.inventory_id join film on inventory.film_id = film.film_id where film.title = 'Academy Dinosaur' order by rental.return_date desc limit 1;
--     15. What is the average runtime of all films?
select avg(length) from film;
--     16. List the average runtime for every film category.
select category.name, avg(film.length) as average_length from film join film_category on film.film_id = film_category.film_id join category on category.category_id = film_category.category_id group by category.name;
--     17. List all movies featuring a robot.
select film.title from film where film.title like '%robot%' or film.description like '%robot%';
--     18. How many movies were released in 2010?
select count(film_id) as num_of_films, release_year from film where release_year = 2010 group by release_year;
--     19. Find the titles of all the horror movies.
select film.title from film join film_category on film_category.film_id = film.film_id join category on category.category_id = film_category.category_id where category.name like '%horror%';
--     20. List the full name of the staff member with the ID of 2.
select first_name, last_name from staff where staff_id = 2;
--     21. List all the movies that Fred Costner has appeared in.
select film.title from film join film_actor on film.film_id = film_actor.film_id join actor on actor.actor_id = film_actor.actor_id where actor.first_name = 'Fred' and actor.last_name = 'Costner';
--     22. How many distinct countries are there?
select distinct count(country) from country;
--     23. List the name of every language in reverse-alphabetical order.
select distinct name from language order by name desc;
--     24. List the full names of every actor whose surname ends with '-son' in alphabetical order by their forename.
select actor.first_name, actor.last_name from actor where actor.last_name like '%son' order by actor.first_name;
--     25. Which category contains the most films?
select c.name, count(fc.film_id) as count from category c join film_category fc on c.category_id = fc.category_id group by fc.category_id order by count desc limit 1;

-- World questions:
use world;
--    1. Using COUNT, get the number of cities in the USA.
select count(ID) as num_of_cities from city where city.CountryCode = 'USA';
--     2. Find out the population and life expectancy for people in Argentina.
select Population, LifeExpectancy, Name from country where Name = 'Argentina';
--     3. Using IS NOT NULL, ORDER BY, and LIMIT, which country has the highest life expectancy?
select Name from country where LifeExpectancy is not null order by LifeExpectancy desc limit 1;
--     4. Using JOIN ... ON, find the capital city of Spain.
select country.Capital from country join city on country.Code = city.CountryCode where city.name like '%Spain%';
--     5. Using JOIN ... ON, list all the languages spoken in the Southeast Asia region.
select countrylanguage.Language from countrylanguage join country on countrylanguage.CountryCode = country.Code where country.Region = 'Southeast Asia';
--     6. Using a single query, list 25 cities around the world that start with the letter F.
select Name from city where Name like 'F%' limit 25;
--     7. Using COUNT and JOIN ... ON, get the number of cities in China.
select count(city.ID) as num_of_cities from city join country on city.CountryCode = country.Code where country.Name = 'China';
--     8. Using IS NOT NULL, ORDER BY, and LIMIT, which country has the lowest population? Discard non-zero populations.
select country.Name, city.Population from country join city on city.CountryCode = country.Code where city.Population is not null order by city.Population limit 1;
--     9. Using aggregate functions, return the number of countries the database contains.
select count(distinct CountryCode) as distrinct_countries from (select CountryCode from city union select Code as CountryCode from country union select CountryCode from city) as combined;
--     10. What are the top ten largest countries by area?
select Name from country order by SurfaceArea desc limit 10;
--     11. List the five largest cities by population in Japan.
select city.Name, city.Population from city join country on city.CountryCode = country.Code where country.Name = 'Japan' order by city.Population desc limit 5;
--     12. List the names and country codes of every country with Elizabeth II as its Head of State. You will need to fix the mistake first!
update country set country.HeadOfState = 'Elizabeth II' where country.HeadOfState like '%Elisabeth%';
select Name, Code from country where HeadOfState like '%Elizabeth II%';
--     13. List the top ten countries with the smallest population-to-area ratio. Discard any countries with a ratio of 0.
select (Population / SurfaceArea) as ratio from country where (Population / SurfaceArea) > 0 order by (Population / SurfaceArea) limit 10;
--     14. List every unique world language.
select distinct Language from countryLanguage;
--     15. List the names and GNP of the world's top 10 richest countries.
select Name, GNP from country order by GNP desc limit 10;
--     16. List the names of, and number of languages spoken by, the top ten most multilingual countries.
select country.Name, count(countrylanguage.Language) from country join countrylanguage on country.Code = countrylanguage.CountryCode group by country.Name order by count(countrylanguage.Language) desc limit 10;
--     17. List every country where over 50% of its population can speak German.
select country.Name, countrylanguage.Percentage from country join countrylanguage on country.Code = countrylanguage.CountryCode where countrylanguage.Language = 'German' and countrylanguage.Percentage > 50;
--     18. Which country has the worst life expectancy? Discard zero or null values.
select Name, LifeExpectancy from country where LifeExpectancy is not null and LifeExpectancy > 0 order by LifeExpectancy limit 1;
--     19. List the top three most common government forms.
select distinct GovernmentForm, count(GovernmentForm) from country group by GovernmentForm order by count(GovernmentForm) desc limit 3;
--     20. How many countries have gained independence since records began?
select count(*) from country where IndepYear is not null;

-- Movielens questions:
use movielens;
--     1. List the titles and release dates of movies released between 1983-1993 in reverse chronological order.
select title, release_date from movies where release_date > 1982 and release_date < 1994 order by release_date desc;
--     2. Without using LIMIT, list the titles of the movies with the lowest average rating.
--     3. List the unique records for Sci-Fi movies where male 24-year-old students have given 5-star ratings.
--     4. List the unique titles of each of the movies released on the most popular release day.
--     5. Find the total number of movies in each genre; list the results in ascending numeric order.