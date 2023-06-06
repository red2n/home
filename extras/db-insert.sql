---  GET_NEXT_ID TABLE INSERT
INSERT INTO GET_NEXT_ID (NEXT_ID,TBL_NAME)
VALUES (1000,'USERS'),
 (1000,'ROLES'),
 (1000,'USERSTATUS'),
 (1000,'PERMISSIONS_LOOKUP'),
 (1000,'ACCESS_GROUP'),
 (1000,'USER_GROUP'),
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

--SELECT * from get_next_id('USERS','navins@nav.com');

-- INSERT ROLES
-- TRUNCATE TABLE ROLES;
-- INSERT INTO ROLES (ROLE_ID,DESCRIPTION,SHORT_DESC)
-- VALUES (NEXT_ID,'Admin','ADM'),
-- (NEXT_ID+=1,'User','USR'),
-- (NEXT_ID+=1,'Guest','GST'),
-- (NEXT_ID+=1,'Super User','SUR');
-- SELECT * FROM ROLES;

-- INSERT USERSTATUS
TRUNCATE TABLE USERSTATUS;
INSERT INTO USERSTATUS (STATUS_ID,DESCRIPTION)
VALUES (100,'Active'),
 (1000,'In-Active'),
 (1001,'Locked');
SELECT * FROM USERSTATUS;

--- INSERT PERMISSIONS_LOOKUP
TRUNCATE TABLE PERMISSIONS_LOOKUP;
INSERT INTO PERMISSIONS_LOOKUP (PERM_ID,DESCRIPTION,SHORT_DESC)
VALUES(100,'View Only','VIW'), 
(1000,'Create','CRE'), 
(1001,'Edit','EDT'),
(1002,'Preview And Comment','CMT'),
(1003,'Delete','DEL');
SELECT * FROM PERMISSIONS_LOOKUP;

--INSERT ACCESS_GROUP
TRUNCATE TABLE ACCESS_GROUP;
INSERT INTO ACCESS_GROUP (ACCESS_ID,PARENT_ID,DESCRIPTION,ROUTES,MAP_ROUTE)
VALUES (100,NULL,'Default User','//Home','//Home'),
(1000,NULL,'Internal Default User','//dashboard','//dashboard'),
(1001,1000,'Quick View','//quick-view','//quick-view'),
(1002,1000,'Chart','//chart','//chart'),
(1003,NULL,'User Access Control','//users','//users'),
(1004,1003,'Create Screen','//create-users','//create-users'),
(1005,1003,'Map User Group','//map-user-roup','//map-user-roup'),
(1006,NULL,'Q and A Control group 1','//questions','//questions'),
(1007,1006,'Create Screen','//create-questions','//create-questions'),
(1008,NULL,'Default Companies','//companies','//companies'),
(1009,1008,'Create Companies','//create-companies','//create-companies'),
(1010,NULL,'Q and A Control group 1','//answers','//answers'),
(1011,1010,'Create Answers','//create-answers','//create-answers');

SELECT * FROM ACCESS_GROUP;

---INSERT INTO USER_GROUP
TRUNCATE TABLE USER_GROUP;
INSERT INTO USER_GROUP(GROUP_ID,PARENT_ID,ACCESS_ID,PERM_ID,DESCRIPTION)
VALUES(100,NULL,100,100,'Default'),
(1000,NULL,NULL,NULL,'Internal default'),
(1001,1000,100,100,''),
(1002,1000,1000,100,''),
(1003,NULL,NULL,NULL,'User Access Group'),
(1004,1003,1003,1000,''),
(1005,NULL,NULL,NULL,'Super User Group'),
(1006,1005,1006,1000,'');
SELECT * FROM USER_GROUP

--- INSERT USERS
TRUNCATE TABLE USERS;
INSERT INTO USERS (FIRST_NAME,LAST_NAME,EMAIL,PHONE,USER_STATUS,GROUP_ID)
VALUES ('Guest','guest','guest.noreply@gmail.com','+987654321',100,100),
('navin','nkumar','ksnavinkumar.diary@gmail.com','+9751449866',100,100),
('Dhanvika','Navin','d@d.com','+9456449866',100,1000),
('Suga','Priya','sugi.mib@gmail.com','+9751449858',1000,1000),
('Subramanian','rangasamy','R@R.com','+91994214881',1000,1000);
SELECT * FROM USERS;

-- INSERT COMPANY_PROFILE
TRUNCATE TABLE COMPANY_PROFILE;
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

--INSERT USR_WORK_PROFILE
TRUNCATE TABLE USR_WORK_PROFILE;
INSERT INTO USR_WORK_PROFILE (C_ID,CURRENT_CTC,EXPECTED_CTC,USR_ID)
VALUES (1,10000,50000,1),
(3,600000,700000,2),
(5,11000,80000,3),
(7,986354,4513696,4);
SELECT * FROM USR_WORK_PROFILE;

--INSERT QUESTIONS_LOOKUP
TRUNCATE TABLE QUESTIONS_LOOKUP;
INSERT INTO QUESTIONS_LOOKUP(DESCRIPTION,POINTS)
VALUES ('Multiple Option',10),
('Demo Questions',1),
('Yes/No',11),
('True/False',12);

SELECT * FROM QUESTIONS_LOOKUP;

---INSERT INTO TEST_TYPE
TRUNCATE TABLE TEST_TYPE;
INSERT INTO TEST_TYPE(DESCRIPTION,POINTS)
VALUES ('Cognitive Ability',20),
('Language',20),
('Personality & Culture',20),
('Programming Skills',20),
('Role Specific Skills',20),
('Situational Judgment',20);

SELECT * FROM TEST_TYPE;

---INSERT INTO TEST_LOOKUP
TRUNCATE TABLE TEST_LOOKUP;
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


---INSERT INTO QUESTIONS 
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

---INSERT INTO ANSWERS
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