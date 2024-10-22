-- create database librarydb; 

use librarydb;

drop table if exists loans;
drop table if exists library_books;
drop table if exists book_location;
drop table if exists books;
drop table if exists genres;
drop table if exists library;
drop table if exists members;

-- declare tables

create table genres (
	id int auto_increment primary key,
	name varchar(50) not null
);

create table library (
	id int auto_increment primary key,
	name varchar(500) not null,
	location varchar(500) not null
);

create table books (
	id int auto_increment primary key,
	title varchar(500) not null,
	author varchar(500) not null,
	num_of_pages int,
	genre_id int not null,
	foreign key (genre_id) references genres(id)
);

create table library_books (
	library_id int not null,
	book_id int not null,
	foreign key (library_id) references library(id),
	foreign key (book_id) references books(id)
);

create table book_location (
    id int auto_increment primary key,
    section_no int,
    shelf_height int,
    shelf_no int,
    book_id int not null,
    foreign key (book_id) references books(id)
);

create table members (
	id int auto_increment primary key,
	name varchar(50) not null,
	dob date,
	date_joined date
);
 
create table loans (
	id int auto_increment primary key,
	member_id int not null,
	book_id int not null,
	start_date date,
	due_date date,
	FOREIGN KEY (member_id) references members(id),
	FOREIGN KEY (book_id) references books(id)
);

-- insert statements

insert into genres (name) 
values 
	('fiction'),
	('non-fiction'),
	('science fiction'),
	('fantasy');

insert into books (title, author, num_of_pages, genre_id)
values 
	('1984', 'george orwell', 328, 1),
	('sapiens', 'yuval noah harari', 443, 2),
	('dune', 'frank herbert', 412, 3),
	('the hobbit', 'j.r.r. tolkien', 310, 4);

insert into library (name, location) 
values 
	('central library', 'bristol'),
	('uwe library', 'bristol'),
	('cardiff central library', 'cardiff');

insert into library_books (library_id, book_id) 
values 
	(1, 1),
	(1, 2),
	(2, 3),
	(3, 4);

insert into book_location (section_no, shelf_height, shelf_no, book_id) 
values
	(1, 2, 3, 1),
	(1, 2, 4, 2),
	(2, 3, 1, 3),
	(2, 3, 2, 4);
    

insert into members (name, dob, date_joined) 
values 
    ('Alice Johnson', '1990-05-15', '2023-01-10'),
    ('Bob Smith', '1985-08-22', '2023-02-20'),
    ('Charlie Brown', '2000-12-01', '2023-03-15'),
    ('Diana Prince', '1995-07-30', '2023-04-25');

insert into loans (member_id, book_id, start_date, due_date) 
values 
    (1, 1, '2023-05-01', '2023-05-15'),
    (2, 2, '2023-05-05', '2023-05-19'),
    (3, 3, '2023-05-10', '2023-05-24'),
    (4, 4, '2023-05-15', '2023-05-29');

-- select statments

-- all books along with their genres and the libraries they are available in

select b.title, b.author, g.name as genre, l.name as library_name, l.location
from books b
join genres g on b.genre_id = g.id
join library_books lb on b.id = lb.book_id
join library l on lb.library_id = l.id;

-- all books currently on loan

select b.title, b.author, m.name as member_name, l.start_date, l.due_date
from loans l
join books b on l.book_id = b.id
join members m on l.member_id = m.id;

-- location details of books in the library

select b.title, b.author, bl.section_no, bl.shelf_height, bl.shelf_no
from books b
join book_location bl on b.id = bl.book_id;

-- all members who have borrowed books

select m.name as member_name, b.title, b.author
from members m
join loans l on m.id = l.member_id
join books b on l.book_id = b.id;




