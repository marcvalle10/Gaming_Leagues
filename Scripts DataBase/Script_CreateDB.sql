-- Create a Gaming_Leagues database.
CREATE DATABASE "Gaming_Leagues"
    WITH
    OWNER = developer
    TEMPLATE = template1
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

GRANT ALL ON DATABASE "Gaming_Leagues" TO developer WITH GRANT OPTION;
