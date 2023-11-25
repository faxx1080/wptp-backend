-- Table: public.exams

-- DROP TABLE IF EXISTS public.exams;

CREATE TABLE IF NOT EXISTS public.exams
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    "name" text COLLATE pg_catalog."default",
    releasedate text COLLATE pg_catalog."default",
    totalscore integer,
    mathquestioncount integer,
    mathtimelimit integer,
    readingquestioncount integer,
    writingquestioncount integer,
    readingwritingtimelimit integer,
    multiplechoicecount integer,
    freeresponsecount integer
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.exams
    OWNER to postgres;

REVOKE ALL ON TABLE public.exams FROM rwuser;

GRANT ALL ON TABLE public.exams TO pg_database_owner;

GRANT ALL ON TABLE public.exams TO postgres;

GRANT SELECT, INSERT, DELETE, UPDATE, TRUNCATE ON TABLE public.exams TO rwuser;