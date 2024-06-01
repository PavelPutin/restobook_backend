ALTER TABLE public.table_reservation DROP CONSTRAINT "table_reservation_PK";
ALTER TABLE public.table_reservation ADD COLUMN id serial;
ALTER TABLE public.table_reservation ADD CONSTRAINT "table_reservation_serial_PK" PRIMARY KEY (id);
