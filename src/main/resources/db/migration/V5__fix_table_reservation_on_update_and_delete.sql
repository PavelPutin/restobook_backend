ALTER TABLE public.table_reservation DROP CONSTRAINT "table_FK";
ALTER TABLE public.table_reservation ADD CONSTRAINT "table_FK"  FOREIGN KEY (table_id)
    REFERENCES public.table_data (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE;

ALTER TABLE public.table_reservation DROP CONSTRAINT "reservation_FK";
ALTER TABLE public.table_reservation ADD CONSTRAINT "reservation_FK"  FOREIGN KEY (reservation_id)
    REFERENCES public.reservation (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE;