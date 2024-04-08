CREATE TABLE IF NOT EXISTS public.restaurant
(
    id serial,
    rest_name character varying(512) NOT NULL,
    legal_entity_name text NOT NULL,
    inn numeric(12) NOT NULL,
    rest_comment text,
    PRIMARY KEY (id),
    CONSTRAINT "Unique_legal_entity_name" UNIQUE (legal_entity_name),
    CONSTRAINT "Unique_inn" UNIQUE (inn)
);

COMMENT ON TABLE public.restaurant
    IS 'Отношение для содержащее информацию о подключившихся к системе ресторанах';

CREATE TABLE IF NOT EXISTS public.employee
(
    id serial,
    login character varying(512) COLLATE pg_catalog."default" NOT NULL,
    surname character varying(512) COLLATE pg_catalog."default" NOT NULL,
    employee_name character varying(512) COLLATE pg_catalog."default" NOT NULL,
    patronymic character varying(512) COLLATE pg_catalog."default",
    employee_comment text COLLATE pg_catalog."default",
    changed_pass boolean NOT NULL DEFAULT false,
    restaurant_id integer NOT NULL,
    CONSTRAINT employee_pkey PRIMARY KEY (id),
    CONSTRAINT "Employee_restaurant_FK" FOREIGN KEY (restaurant_id)
        REFERENCES public.restaurant (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

COMMENT ON TABLE public.employee
    IS 'Отношение содержит информацию о сотрудниках всех ресторанов, подключившихся к приложению';


CREATE TABLE IF NOT EXISTS public.table_data
(
    id serial,
    table_number integer NOT NULL,
    seats_number integer NOT NULL,
    state character varying(256) NOT NULL DEFAULT 'normal',
    table_comment text,
    restaurant_id integer NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT "Table_restaurant_FK" FOREIGN KEY (restaurant_id)
        REFERENCES public.restaurant (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT table_number_is_positive CHECK (table_number > 0) NOT VALID,
    CONSTRAINT seats_number_is_positive CHECK (seats_number > 0) NOT VALID
);

COMMENT ON TABLE public.table_data
    IS 'Отношение содержит данные о всех столах в подключившихся ресторанах';


CREATE TABLE IF NOT EXISTS public.reservation
(
    id serial,
    persons_number integer NOT NULL,
    client_phone_number character varying(30) COLLATE pg_catalog."default" NOT NULL,
    client_name character varying(512) COLLATE pg_catalog."default" NOT NULL,
    start_date_time timestamp with time zone NOT NULL,
    duration interval NOT NULL,
    employee_full_name text COLLATE pg_catalog."default" NOT NULL,
    creating_date_time timestamp with time zone NOT NULL,
    state character varying(30) COLLATE pg_catalog."default" NOT NULL DEFAULT 'waiting'::character varying,
    reservation_comment text COLLATE pg_catalog."default",
    restaurant_id integer NOT NULL,
    CONSTRAINT reservation_pkey PRIMARY KEY (id),
    CONSTRAINT "reservation_restaurant_FK" FOREIGN KEY (restaurant_id)
        REFERENCES public.restaurant (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT persons_number_is_positive CHECK (persons_number > 0)
);

COMMENT ON TABLE public.reservation
    IS 'Отношение содержит данные о бронях';

CREATE TABLE IF NOT EXISTS public.table_reservation
(
    table_id integer,
    reservation_id integer,
    CONSTRAINT "table_reservation_PK" PRIMARY KEY (table_id, reservation_id),
    CONSTRAINT "table_FK" FOREIGN KEY (table_id)
        REFERENCES public.table_data (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "reservation_FK" FOREIGN KEY (reservation_id)
        REFERENCES public.reservation (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);