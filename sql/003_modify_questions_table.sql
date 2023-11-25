-- Table: public.questions

-- Add new columns: equations, correctanswertext (FRQ answers), imagesolutionlink
ALTER TABLE public.questions
ADD COLUMN equations text COLLATE pg_catalog."default",
ADD COLUMN correctanswertext text COLLATE pg_catalog."default",
ADD COLUMN imagesolutionlink text COLLATE pg_catalog."default";