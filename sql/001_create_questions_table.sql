CREATE SCHEMA questions
	CREATE TABLE questions.questions (
	    id SERIAL PRIMARY KEY,
	    CorrectAnswerChoice VARCHAR(1),
	    Difficulty INTEGER,
	    QuestionText TEXT,
	    ChoiceAText TEXT,
	    ChoiceBText TEXT,
	    ChoiceCText TEXT,
	    ChoiceDText TEXT,
	    ChoiceEText TEXT,
	    QuestionType VARCHAR(255),
	    Section VARCHAR(255),
	    AnswerExplanation TEXT,
	    CategoriesAlgebra TEXT,
	    CategoriesGeometry TEXT,
	    ImageLink TEXT
	)