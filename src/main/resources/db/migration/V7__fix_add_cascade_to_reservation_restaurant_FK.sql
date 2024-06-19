alter table public.reservation
    drop constraint "reservation_restaurant_FK",
    add constraint "reservation_restaurant_FK" foreign key (restaurant_id)
        references public.restaurant (id) match simple
        on update cascade
        on delete cascade;