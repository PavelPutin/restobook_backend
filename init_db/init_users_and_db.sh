#!/bin/bash
psql -U "restobook" <<-END
    CREATE ROLE kc_user WITH
        LOGIN
        NOSUPERUSER
        CREATEDB
        NOCREATEROLE
        INHERIT
        NOREPLICATION
        NOBYPASSRLS
        CONNECTION LIMIT -1
        PASSWORD 'kc_pass';
    COMMENT ON ROLE kc_user IS 'Keycloak database user';
END

psql -U "restobook" <<-END
    CREATE DATABASE keycloak
        WITH
        OWNER = kc_user
        ENCODING = 'UTF8'
        LOCALE_PROVIDER = 'libc'
        CONNECTION LIMIT = -1
        IS_TEMPLATE = False;
    COMMENT ON DATABASE keycloak
        IS 'Базы данных приложения Restobook';
END