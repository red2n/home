
--psql -U postgres
--CREATE DATABASE homebase;
--\c homebase

DROP TABLE IF EXISTS COMPANY_PROFILE cascade;
DROP TABLE IF EXISTS USR_WORK_PROFILE cascade;
DROP TABLE IF EXISTS ANSWERS cascade;
DROP TABLE IF EXISTS QUESTIONS cascade;
DROP TABLE IF EXISTS QUESTIONS_LOOKUP cascade;
DROP TABLE IF EXISTS USERS cascade;
DROP TABLE IF EXISTS PERMISSIONS_LOOKUP cascade;
DROP TABLE IF EXISTS ROLES cascade;
DROP TABLE IF EXISTS USERSTATUS cascade;

--- Creating Custom Domain
--DOMAIN - email

CREATE DOMAIN EMAIL_ADDRESS AS TEXT CHECK (VALUE ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$');

DROP DOMAIN IF EXISTS EMAIL_ADDRESS;

-- Create Get next Instance
DROP TABLE IF EXISTS GET_NEXT_ID;
Create TABLE GET_NEXT_ID(NEXT_ID INT NOT NULL,
						 TBL_NAME VARCHAR(100) NOT NULL,
						 HOLDING_PROCESS VARCHAR(200) DEFAULT NULL);
INSERT INTO GET_NEXT_ID (NEXT_ID,TBL_NAME)
VALUES (1000,'USERS'),
 (1000,'ROLES'),
 (1000,'USERSTATUS'),
 (1000,'PERMISSIONS_LOOKUP'),
 (1000,'ACCESS_GROUP'),
 (1000,'COMPANY_PROFILE'),
 (1000,'USR_WORK_PROFILE'),
 (1000,'QUESTIONS_LOOKUP'),
 (1000,'TEST_TYPE'),
(1000,'TEST_LOOKUP'),
(1000,'QUESTIONS'),
(1000,'ANSWERS');
SELECT * from GET_NEXT_ID;

--Get Next ID

CREATE OR REPLACE FUNCTION get_next_id(tableName TEXT,userEmail EMAIL_ADDRESS, NEWID INTEGER DEFAULT 0)
RETURNS INTEGER
AS $$
DECLARE
    NEXTID INTEGER;
BEGIN
	BEGIN
		SELECT NEXT_ID INTO NEXTID FROM GET_NEXT_ID WHERE TBL_NAME ILIKE tableName AND HOLDING_PROCESS IS NULL;
		IF(NEXTID >0) THEN
			NEXTID = NEXTID+1;
			UPDATE GET_NEXT_ID SET NEXT_ID= NEXTID, HOLDING_PROCESS= (userEmail ||'_' ||CURRENT_TIMESTAMP) 
			WHERE TBL_NAME = tableName AND HOLDING_PROCESS IS NULL;
		ELSE
			SELECT NEXT_ID INTO NEXTID FROM GET_NEXT_ID 
			WHERE TBL_NAME ILIKE (tableName) 
			AND HOLDING_PROCESS IS NOT NULL
			AND HOLDING_PROCESS ILIKE (userEmail || '%');
			IF(NEXTID >0 AND NEWID>0) THEN
				UPDATE GET_NEXT_ID SET NEXT_ID=NEWID, HOLDING_PROCESS= NULL WHERE TBL_NAME = tableName;
			ELSEIF (NEXTID >0 AND NEWID = 0) THEN
				UPDATE GET_NEXT_ID SET HOLDING_PROCESS= NULL WHERE TBL_NAME = tableName;
			END IF;
			NEXTID = NULL;
		END IF;
	EXCEPTION
		 WHEN OTHERS THEN
		 	NEXTID= NULL;
			raise exception using message = 'S 167';
	END;
    RETURN NEXTID THEN;
END;
$$ LANGUAGE plpgsql;

SELECT * from get_next_id('USERS','navins@nav.com');


---Create Tables.
--ROLES
CREATE TABLE ROLES(ID SERIAL,
				   ROLE_ID INT PRIMARY KEY,
				   DESCRIPTION VARCHAR(10),
				   SHORT_DESC VARCHAR(3),
				   UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP,
				   CONSTRAINT FK_ROLES
				   UNIQUE (ROLE_ID));

TRUNCATE TABLE ROLES;
select * from get_next_id('ROLES','Navin');
INSERT INTO ROLES (ROLE_ID,DESCRIPTION,SHORT_DESC)
VALUES (NEXT_ID,'Admin','ADM'),
(NEXT_ID+=1,'User','USR'),
(NEXT_ID+=1,'Guest','GST'),
(NEXT_ID+=1,'Super User','SUR');


SELECT * FROM ROLES;

---USERSTATUS

CREATE TABLE USERSTATUS(ID SERIAL PRIMARY KEY,
				   DESCRIPTION VARCHAR(10),
				   UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP);

TRUNCATE TABLE USERSTATUS;
INSERT INTO USERSTATUS (DESCRIPTION)
VALUES ('Active'),
 ('In-Active'),
 ('Locked');
SELECT * FROM USERSTATUS;

--USERS
CREATE TABLE USERS
	(ID SERIAL PRIMARY KEY,
	 FIRST_NAME VARCHAR(100) NOT NULL,
	 LAST_NAME VARCHAR(100) NOT NULL,
	 EMAIL EMAIL_ADDRESS NOT NULL,
	 PHONE VARCHAR(15) NOT NULL,
	 USER_STATUS INT NOT NULL,
	 USR_PASS TEXT DEFAULT 'Password1!',
	 UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP,
	 CONSTRAINT FK_ROLLS
	 UNIQUE (EMAIL),
	 UNIQUE (PHONE),
	 FOREIGN KEY(USER_STATUS) REFERENCES USERSTATUS(ID) ON DELETE CASCADE);

TRUNCATE TABLE USERS;
INSERT INTO USERS (FIRST_NAME,LAST_NAME,EMAIL,PHONE,USER_STATUS)
VALUES ('navin','nkumar','ksnavinkumar.diary@gmail.com','+9751449866',1),
('Dhanvika','Navin','d@d.com','+9456449866',1),
('Suga','Priya','sugi.mib@gmail.com','+9751449858',2),
('Subramanian','rangasamy','R@R.com','+91994214881',3);

SELECT * FROM USERS;

-- Permission_Lookup
CREATE TABLE PERMISSIONS_LOOKUP(ID SERIAL,
								PERM_ID INT PRIMARY KEY,
								DESCRIPTION VARCHAR(20),
								SHORT_DESC VARCHAR(3),
								UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP,
							    CONSTRAINT FK_PERMISS
								UNIQUE (PERM_ID));

TRUNCATE TABLE PERMISSIONS_LOOKUP;
INSERT INTO PERMISSIONS_LOOKUP (PERM_ID,DESCRIPTION,SHORT_DESC)
VALUES (123731,'Create','CRE'), (200200,'Edit','EDT'),
(323931,'Preview And Comment','CMT'),(524131,'Delete','DEL');
SELECT * FROM PERMISSIONS_LOOKUP;

--- screens 
CREATE TABLE ACCESS_GROUP(ID SERIAL,
						  SCREEN_ID INT NOT NULL PRIMARY KEY,
						  PARENT_ID INT NOT NULL,
						  DESCRIPTION VARCHAR(50),
						  ROUTES  VARCHAR(50),
						  MAP_ROUTE VARCHAR(50),
						  UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP,
						  CONSTRAINT FK_SCREENS
						  UNIQUE (SCREEN_ID),
						  FOREIGN KEY(PARENT_ID) REFERENCES ACCESS_GROUP(SCREEN_ID));
INSERT INTO ACCESS_GROUP (SCREEN_ID,CHILD_SCREEN_ID,DESCRIPTION,ROUTES,MAP_ROUTE)
VALUES (100,0,'Home/Welcome Screen','//Home','//Home'),
(1000,0,'Dashboard','//dashboard','//dashboard'),
(1001,1000,'Quick View','//quick-view','//quick-view'),
(1002,1000,'Chart','//chart','//chart'),
(1003,0,'User','//users','//users'),
(1004,1003,'Create Screen','//create-users','//create-users'),
(1005,1003,'Map User Group','//map-user-roup','//map-user-roup'),
(1006,0,'Questions','//questions','//questions'),
(1007,1006,'Create Screen','//create-questions','//create-questions'),
(1008,0,'Companies','//companies','//companies'),
(1009,1008,'Create Companies','//create-companies','//create-companies'),
(1010,0,'Answers','//answers','//answers'),
(1011,1010,'Create Answers','//create-answers','//create-answers');

SELECT * FROM ACCESS_GROUP;


-- User Group
CREATE TABLE USER_GROUP(ID SERIAL,
						GROUP_ID NOT NULL PRIMARY KEY,
						SCREEN_ID INT NOT NULL
						COMPANY_NAME TEXT NOT NULL,
						ISPRODUCT_BASED BOOLEAN NOT NULL,
						UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP,
						CONSTRAINT FK_SCREENS
						UNIQUE (GROUP_ID))


--COMPANY_PROFILE
CREATE TABLE COMPANY_PROFILE(ID SERIAL PRIMARY KEY,
							 COMPANY_NAME TEXT NOT NULL,
							 ISPRODUCT_BASED BOOLEAN NOT NULL,
							UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP);


INSERT INTO COMPANY_PROFILE (COMPANY_NAME,ISPRODUCT_BASED)
VALUES ('Microsoft India', TRUE),
('SAP Labs India', TRUE),
('Oracle India', TRUE),
('Adobe India', TRUE),
('Cisco India', TRUE),
('IBM India', TRUE),
('Intel India', TRUE),
('GE India', TRUE),
('Siemens India', TRUE),
('Honeywell Technology Solutions', TRUE),
('Tata Consultancy Services (TCS)', FALSE),
('Infosys', FALSE),
('Wipro', FALSE),
('HCL Technologies', FALSE),
('Tech Mahindra', FALSE),
('Accenture', FALSE),
('L&T Infotech', FALSE),
('Mindtree', FALSE),
('Mphasis', FALSE),
('Capgemini', FALSE);

SELECT * FROM COMPANY_PROFILE;

---USR_WORK_PROFILE
CREATE TABLE USR_WORK_PROFILE(ID SERIAL PRIMARY KEY,
							  C_ID INT NOT NULL,
							  DURATION_FROM DATE default CURRENT_DATE - 30,
							  DURATION_TO DATE default CURRENT_DATE,
							  CURRENT_CTC INT NOT NULL,
							  EXPECTED_CTC INT NOT NULL,
							  UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP,
							  USR_ID INT NOT NULL,
							  CONSTRAINT FK_PROFILE
							  FOREIGN KEY(USR_ID) REFERENCES USERS(ID),
							  FOREIGN KEY(C_ID) REFERENCES COMPANY_PROFILE(ID) ON DELETE CASCADE);
		
INSERT INTO USR_WORK_PROFILE (C_ID,CURRENT_CTC,EXPECTED_CTC,USR_ID)
VALUES (1,10000,50000,1),
(3,600000,700000,2),
(5,11000,80000,3),
(7,986354,4513696,4);

SELECT * FROM USR_WORK_PROFILE;

--QUESTIONS TYPE LOOKUP
CREATE TABLE QUESTIONS_LOOKUP(ID SERIAL PRIMARY KEY,
							  DESCRIPTION VARCHAR(50) NOT NULL,
							  POINTS INT NOT NULL,
							  TIME INT default 300,
							  UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP);

TRUNCATE TABLE QUESTIONS_LOOKUP;

INSERT INTO QUESTIONS_LOOKUP(DESCRIPTION,POINTS)
VALUES ('Multiple Option',10),
('Demo Questions',1),
('Yes/No',11),
('True/False',12);

SELECT * FROM QUESTIONS_LOOKUP;

-- Topic_lookup
CREATE TABLE TEST_TYPE(ID SERIAL PRIMARY KEY,
					   DESCRIPTION VARCHAR(50) NOT NULL,
					   POINTS INT NOT NULL,
					   UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP);

INSERT INTO TEST_TYPE(DESCRIPTION,POINTS)
VALUES ('Cognitive Ability',20),
('Language',20),
('Personality & Culture',20),
('Programming Skills',20),
('Role Specific Skills',20),
('Situational Judgment',20);

SELECT * FROM TEST_TYPE;

--- TEST_LOOKUP
CREATE TABLE TEST_LOOKUP(ID SERIAL PRIMARY KEY,
						 TITLE VARCHAR(100) NOT NULL,
						 DESCRIPTION TEXT NOT NULL,
						 TYP_ID INT NOT NULL,
						 UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP,
						 CONSTRAINT FK_TEST_LOOKUP
						 FOREIGN KEY(TYP_ID) REFERENCES TEST_TYPE(ID));

--1
INSERT INTO TEST_LOOKUP(TITLE,DESCRIPTION,TYP_ID)
VALUES ('Mechanical reasoning',
		'This mechanical reasoning test evaluates candidates’ understanding of basic mechanical and physical concepts. This test helps you identify individuals with good mechanical reasoning skills who can apply mechanical principles to solve problems.',
	   1)
	   ,('Reading comprehension',
		'The reading comprehension test evaluates candidates’ ability to read a portion of text and comprehend its contents. This test helps you identify candidates who can process written information and draw appropriate conclusions using analytical thinking.',
	   1)
	   ,('Critical thinking',
		'This critical thinking test evaluates candidates’ skills in critical thinking through inductive and deductive reasoning problems. This test will help you identify candidates who can evaluate information and make sound judgments using analytical skills.',
	   1)
	   ,('Intermediate math',
		'The intermediate math test evaluates candidates’ ability to solve math equations and problems involving fractions, decimals, percentages, ratios, and time calculations. This test will help you identify candidates who can work well with numbers.',
	   1),
	   ('English (proficient/C2)',
		'This English (proficient/C2) test evaluates candidates English ability at CEFR level C2. It will help you hire people who can create international opportunities for your organization and improve your customer satisfaction, loyalty, revenue, and culture.',
	   2),
	   ('Japanese (proficient/C1)',
		'The Japanese C1 test evaluates candidates’ knowledge of the Japanese language at the C1 level of the CEFR framework. This test will help you hire employees who can participate in demanding professional and social conversations in Japanese.',
	   2)
	   ,('Swedish (proficient/C1)',
		'The Swedish (proficient/C1) test evaluates candidates’ knowledge of Swedish language at the C1 level of the CEFR framework. This test will help you hire employees who can participate in demanding professional and social conversations in Swedish.',
	   2),
	   ('French (intermediate/B1)',
		'The French (intermediate/B1) test evaluates a candidate’s French proficiency at the B1 level of the CEFR framework. This test will help you hire employees who can communicate in French on subjects commonly encountered at work and in everyday life.',
	   2),
	   ('Enneagram',
		'The French (intermediate/B1) test evaluates a candidate’s French proficiency at the B1 level of the CEFR framework. This test will help you hire employees who can communicate in French on subjects commonly encountered at work and in everyday life.',
	   3),
	   ('Big 5 (OCEAN)',
		'The big 5 personality test follows the Five-Factor Model, an empirical-based theory in psychology that evaluates five overarching dimensions of personality: openness, conscientiousness, extroversion, agreeableness, and emotional stability.',
	   3),
	   ('Motivation',
		'Rooted in Oldham & Hackman’s Job Characteristics Model, the job preferences test measures the extent to which your candidates expectations align with your job offer, based on a customized survey that you and the candidate both fill out.',
	   3),
	   ('Culture add',
		'This culture add test assesses how a candidate’s values and behaviors align with your organization values and the behaviors that would make your ideal hire successful in a specific role, based on a customized survey you fill out.',
	   3),
	   ('Kotlin (Coding): Working with Arrays',
		'This Kotlin Coding test evaluates candidates’ ability to work with arrays. The test gives candidates 30 minutes to create a short algorithm involving arrays while taking into consideration all the requirements provided.',
	   4),
	   ('React Native',
		'This React Native test evaluates candidates’ knowledge of the React Native framework. This test will help you hire React Native developers who can create, update, and maintain your React Native apps.',
	   4),
	   ('Java (coding): data structures',
		'This Java data structures test assesses a candidate ability to effectively manipulate core data structures in the Java programming language. In 30 minutes, they will work with the core Java API to implement typical and real-life scenarios.',
	   4),
	   ('Android Development using Java',
		'The Android Development test assesses a candidate’s practical programming skills in developing mobile Android applications. This test helps you identify developers who are good at creating applications and dealing with development challenges.',
	   4); 

SELECT * FROM TEST_LOOKUP;

--QUESTIONS
CREATE TABLE QUESTIONS(ID SERIAL PRIMARY KEY,
					   QUESTION TEXT NOT NULL,
					   OPTIONS TEXT NOT NULL,
					   ANSWER TEXT NOT NULL,
					   ID_LOOKUP INT NOT NULL,
					   TEST_LOOKUP_ID INT NOT NULL,
					   UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP,
					   CONSTRAINT FK_QUESTIONS_LOOKUP
					   FOREIGN KEY(ID_LOOKUP) REFERENCES QUESTIONS_LOOKUP(ID),
					   FOREIGN KEY(TEST_LOOKUP_ID) REFERENCES TEST_LOOKUP(ID) ON DELETE CASCADE);

TRUNCATE TABLE QUESTIONS;


INSERT INTO QUESTIONS (QUESTION,OPTIONS,ANSWER,ID_LOOKUP,TEST_LOOKUP_ID)
VALUES ('What is the difference between a stack and a queue?',
		'A) A stack is a Last-In-First-Out (LIFO) data structure, while a queue is a First-In-First-Out (FIFO) data structure.
B) A stack is a FIFO data structure, while a queue is a LIFO data structure.
C) Both stack and queue are LIFO data structures.','A',1,11),
('What is a closure in programming?','A) A function that has no return value.
B) A function that takes in no arguments.
C) A function that has access to variables in its enclosing scope, even after the scope has closed.','C',1,12)
,('What is the difference between compile-time and runtime errors?','A) Compile-time errors occur during the execution of a program, while runtime errors occur before the program is executed.
B) Compile-time errors occur before the program is executed, while runtime errors occur during the execution of a program.
C) Both compile-time and runtime errors occur during the execution of a program.','B',1,13),
('What is polymorphism in programming?','A) The ability of a program to execute multiple threads concurrently.
B) The ability of a program to execute multiple functions at the same time.
C) The ability of a program to use objects of different classes in the same way.','C',1,14)
,('What is a hash table?','A) A data structure that stores elements in a sorted order.
B) A data structure that stores elements in an unsorted order.
C) A data structure that stores elements using a key-value mapping.','C',1,15)
,('What is recursion in programming?','A) A function that takes in no arguments.
B) A function that calls itself.
C) A function that returns multiple values.','B',1,16)
,('What is the difference between a linked list and an array?','A) A linked list is a collection of elements stored in contiguous memory locations, while an array is a collection of elements stored in non-contiguous memory locations.
B) A linked list is a collection of elements stored in non-contiguous memory locations, while an array is a collection of elements stored in contiguous memory locations.
C) Both linked list and array are collections of elements stored in contiguous memory locations.','B',1,10)
,('What is object-oriented programming?','A) A programming paradigm based on the use of objects that have properties and methods.
B) A programming paradigm based on the use of functions that have no side effects.
C) A programming paradigm based on the use of global variables.','A',1,10)
,('What is a binary search?','A) A search algorithm that requires elements to be sorted and starts searching from the middle of the list.
B) A search algorithm that requires elements to be sorted and starts searching from the beginning of the list.
C) A search algorithm that requires elements to be sorted and starts searching from the end of the list.','A',1,5)
,('What is a compiler?','A) A program that translates source code into machine code.
B) A program that translates machine code into source code.
C) A program that translates high-level programming languages into assembly language.','A',1,8);

SELECT * FROM QUESTIONS;

-- Answers
CREATE TABLE ANSWERS(ID SERIAL PRIMARY KEY,
					 Q_ID INT NOT NULL,
					 USR_ID INT NOT NULL,
					 ISTAKEN BOOLEAN NOT NULL,
					 ANSR TEXT,
					 UPDATED_DATE_TIME TIMESTAMP default CURRENT_TIMESTAMP,
					 CONSTRAINT FK_QUESTIONS
					 FOREIGN KEY(Q_ID) REFERENCES QUESTIONS(ID),
					 FOREIGN KEY(USR_ID) REFERENCES USERS(ID) ON DELETE CASCADE);

TRUNCATE TABLE ANSWERS;

INSERT INTO ANSWERS (Q_ID,USR_ID,ISTAKEN)
VALUES (1,3,FALSE)
,(2,3,FALSE)
,(3,3,FALSE)
,(4,3,FALSE)
,(5,3,FALSE)
,(6,3,FALSE)
,(7,3,FALSE)
,(8,3,FALSE)
,(9,3,FALSE)
,(10,3,FALSE)
,(1,4,FALSE)
,(2,4,FALSE),
(3,4,FALSE),
(4,4,FALSE)
,(5,4,FALSE)
,(6,4,FALSE),
(7,4,FALSE)
,(8,4,FALSE)
,(9,4,FALSE)
,(10,4,FALSE);

SELECT * FROM ANSWERS;

--answered
DROP TYPE IF EXISTS USER_ANSWERED CASCADE;
CREATE TYPE user_answered AS (id INT,
							  question TEXT,
							  OPTIONS text,
							  answer TEXT,
							  POINTS INT,
							  TIME INT,
							  ANSWERED_TIME TIMESTAMP
);

CREATE OR REPLACE FUNCTION get_answered(user_id INT)
RETURNS SETOF user_answered
AS $$
DECLARE
  result user_answered;
BEGIN
  FOR result IN
    SELECT ans.id,
	q.question,
	q."options",
	ans.ansr,
	q_l.points,
	q_l."time",
	ans.updated_date_time
    FROM answers ans
    JOIN questions q ON ans.q_id = q.id
	JOIN users usr ON usr.id = ans.usr_id
	JOIN questions_lookup  q_l ON q_l.id = q.id_lookup
	WHERE ans.usr_id = user_id
  LOOP
    RETURN NEXT result;
  END LOOP;
END;
$$ LANGUAGE plpgsql;

-- user test available
DROP TYPE IF EXISTS TEST_AVAILABLE_TBL CASCADE;
CREATE TYPE TEST_AVAILABLE_TBL AS (id INT,
								   description TEXT,
								   bonus_point INT,
								   question_point INT,
								   question_time INT
);

CREATE OR REPLACE FUNCTION TEST_AVAILABLE(user_id INT)
RETURNS SETOF TEST_AVAILABLE_TBL
AS $$
DECLARE
  result TEST_AVAILABLE_TBL;
BEGIN
  FOR result IN
    SELECT DISTINCT tt.id,tt.description,tt.points as bonus_point,
	SUM(ql.points) AS question_point,
	SUM(ql."time") AS question_time
	FROM test_type tt
	LEFT JOIN test_lookup tl ON tl.typ_id = tt.id
	LEFT JOIN questions q ON  q.test_lookup_id = tl.id
	LEFT JOIN questions_lookup ql ON  ql.id = q.id_lookup
	LEFT JOIN answers a ON a.q_id = q.id
	WHERE a.usr_id = user_id
	GROUP BY tt.id
  LOOP
    RETURN NEXT result;
  END LOOP;
END;
$$ LANGUAGE plpgsql;



--Select * from COMPANY_PROFILE;
--Select * from USR_WORK_PROFILE;
--Select * from ANSWERS ;
--Select * from QUESTIONS;
--Select * from QUESTIONS_LOOKUP;
--Select * from USERS ;
--Select * from ROLES ;
--SELECT * FROM get_answered(3);
--SELECT * FROM TEST_AVAILABLE(3);

--update USERS set email='ksnavinkumar.diary@gmail.com' where id = 1