use sakila;

show tables;
describe film;
select * from film;

select title, release_year, rental_rate, rating
from film
where title like '%A%';

select title
from film
where title like '%T%__%T';

select min(length) as shortest_movie, max(length) as longest_movie, count(length) as total_length
from film;

select title, release_year
from film
where film_id not in (select film_id from inventory);

select rental.rental_id, rental.rental_date, film.title, inventory.inventory_id
from rental
inner join inventory
on rental.inventory_id = inventory.inventory_id
inner join film
on inventory.film_id = film.film_id;

select * from address as a
left outer join customer as c on c.address_id = a.address_id;