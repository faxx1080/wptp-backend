-- Table: public.questions

-- Inserting few rows of data from Math questions spreadsheet
	
INSERT INTO public.questions(
	"section", questiontext, imagelink, equations, categoriesalgebra, categoriesgeometry, difficulty, questiontype, choiceatext, choicebtext, choicectext, choicedtext, choiceetext, correctanswerchoice, correctanswertext, imagesolutionlink)
	VALUES 
		('Math', 'Given the equation below, what is b/a?', NULL, 'a/3b = 3/7', 'Algebraic Manipulation', 'NOT GEOMETRY', 2, 'Multiple choice', '7/9', '9/7', '3/21', '7/3', NULL, 'A', NULL, 'https://drive.google.com/open?id=1mHBiSbBi__TgvxH11Ejv6q_pYHYPOwHH'),
		('Math', 'Based on the equation below, find x:', NULL, 'x + 5 = -2x + 7', 'Solving for a Variable, Two-Step Equations', 'NOT GEOMETRY', 1, 'Multiple choice', '-4', '1', '2/3', '-2/3', NULL, 'C', NULL, 'https://drive.google.com/open?id=1Gb7I-8b6HTq_DHjy6JVTXuGAFa6MWFo-'),
		('Math', 'What is the y-intercept of the following graph?',	'https://drive.google.com/open?id=1YhJMeWlw7luQz4igIy8OWFwLEoXQwfyz', NULL, 'Intercepts, Linear Equations, Understanding Equations', 'NOT GEOMETRY', 1, 'Multiple choice', '(3,0)',	'(0,3)', '(-3,0)', '(-1.5,0)', NULL, 'B', NULL,	'https://drive.google.com/open?id=1nmW2U4RNHRvlGfKxCTa-_0jtAZiBBBww');