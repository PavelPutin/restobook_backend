#!/bin/bash
psql -U "${POSTGRES_USER}" <<-END
    CREATE ROLE ${KC_DB_USERNAME} WITH
    	LOGIN
    	NOSUPERUSER
    	CREATEDB
    	NOCREATEROLE
    	INHERIT
    	NOREPLICATION
    	NOBYPASSRLS
    	CONNECTION LIMIT -1
    	PASSWORD '${KC_DB_PASSWORD}';
    COMMENT ON ROLE ${KC_DB_USERNAME} IS 'Keycloak database user';
END

psql -U "${POSTGRES_USER}" <<-END
    CREATE DATABASE keycloak
        WITH
        OWNER = ${KC_DB_USERNAME}
        ENCODING = 'UTF8'
        LOCALE_PROVIDER = 'libc'
        CONNECTION LIMIT = -1
        IS_TEMPLATE = False;
    COMMENT ON DATABASE keycloak
        IS 'Базы данных приложения Restobook';
END