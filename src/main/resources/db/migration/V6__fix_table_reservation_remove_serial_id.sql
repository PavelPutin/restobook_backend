ALTER TABLE public.table_reservation DROP CONSTRAINT "table_reservation_serial_PK";
ALTER TABLE public.table_reservation DROP COLUMN id;
ALTER TABLE public.table_reservation ADD CONSTRAINT "table_reservation_PK" PRIMARY KEY (table_id, reservation_id);