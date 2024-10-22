-- create database herodb;

use herodb;

-- table declarations

drop table if exists heros;
drop table if exists teams;
drop table if exists product;

create table product (
	id int auto_increment primary key,
	name varchar(500) not null,
	colour varchar(500),
	standard_cost float,
	list_price float,
	date_first_available date
);

create table teams (
	id int auto_increment primary key,
	name varchar(500) not null,
	objective varchar(500) not null
);

create table heros (
	id int auto_increment primary key,
	name varchar(50) not null,
	alias varchar(50) not null,
	ability varchar(500),
	team_id int,
	product_id int,
	foreign key (product_id) references product (id),
	foreign key (team_id) references teams (id)
);

-- insert statements

insert into product (name, colour, standard_cost, list_price, date_first_available)
values
	('Star Widget', 'Red', 10.0, 15.0, '2024-01-01'),
	('Rocket Widget', 'Blue', 12.0, 18.0, '2024-01-01'),
	('Flying Widget', 'Green', 11.0, 16.0, '2024-01-01'),
	('Spinning Widget', 'Yellow', 9.0, 14.0, '2024-01-01'),
	('Rainbow Widget', 'Rainbow', 13.0, 20.0, '2024-01-01'),
	('Flying Unicorn', 'Pink', 14.0, 22.0, '2024-01-01'),
	('Rainbow Unicorn', 'Purple', 15.0, 25.0, '2024-01-01'),
	('Golden Unicorn', 'Gold', 20.0, 30.0, '2024-01-01'),
	('Sky Widget', 'Sky Blue', 8.0, 12.0, '2024-01-01'),
	('Horse Widget', 'Brown', 7.0, 10.0, '2024-01-01');


insert into teams (name, objective)
values
	('JLA', 'Protect the world'),
	('JSA', 'Defeat the Nazis'),
	('Birds of Prey', 'Fight crime without men'),
	('Task Force X', 'Follow Waller\'s orders or die'),
	('Teen Titans', 'Teach young superheroes to be their best');

insert into heros (name, alias, ability, team_id, product_id) 
values
	('Bruce Wayne', 'Batman', 'Martial Arts', 1, 1),
	('Clark Kent', 'Superman', 'Flight', 1, 2),
	('Jay Garrick', 'The Flash', 'Super Speed', 2, 2),
	('Alan Scott', 'Green Lantern', 'Ring Creation', 2, 4),
	('Helena Bertinelli', 'The Huntress', 'Crossbow Archery', 3, 2),
	('Dr. Harleen Quinzel', 'Harley Quinn', 'Hammer Fighting', 3, 1),
	('Floyd Lawton', 'Deadshot', 'Marksmanship', 4, 4),
	('Cecil Adams', 'Count Vertigo', 'Induce Dizziness', 4, 5),
	('Damian Wayne', 'Robin', 'Swordsmanship', 5, 3),
	('Dick Grayson', 'Nightwing', 'Acrobatics', 5, 2);

-- select statments

select 
	h.name, 
	h.alias, 
    h.ability, 
    t.name, 
    t.objective 
from heros h 
join teams t on h.team_id = t.id;

select 
    heros.name as hero_name,
    heros.alias,
    heros.ability,
    teams.name as team_name,
    teams.objective,
    product.name as product_name,
    product.colour,
    product.standard_cost,
    product.list_price,
    product.date_first_available
from heros
join teams on heros.team_id = teams.id
join product on heros.product_id = product.id;





