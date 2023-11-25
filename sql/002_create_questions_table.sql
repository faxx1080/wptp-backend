-- Table: public.questions

-- DROP TABLE IF EXISTS public.questions;

CREATE TABLE IF NOT EXISTS public.questions
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    correctanswerchoice character varying(1) COLLATE pg_catalog."default",
    difficulty integer,
    questiontext text COLLATE pg_catalog."default",
    choiceatext text COLLATE pg_catalog."default",
    choicebtext text COLLATE pg_catalog."default",
    choicectext text COLLATE pg_catalog."default",
    choicedtext text COLLATE pg_catalog."default",
    choiceetext text COLLATE pg_catalog."default",
    questiontype character varying(255) COLLATE pg_catalog."default",
    section character varying(255) COLLATE pg_catalog."default",
    answerexplanation text COLLATE pg_catalog."default",
    categoriesalgebra text COLLATE pg_catalog."default",
    categoriesgeometry text COLLATE pg_catalog."default",
    imagelink text COLLATE pg_catalog."default",
    CONSTRAINT questions_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.questions
    OWNER to postgres;

REVOKE ALL ON TABLE public.questions FROM rwuser;

GRANT ALL ON TABLE public.questions TO pg_database_owner;

GRANT ALL ON TABLE public.questions TO postgres;

GRANT SELECT, INSERT, DELETE, UPDATE, TRUNCATE ON TABLE public.questions TO rwuser;