-- Создание схемы event_schema, если её нет
CREATE SCHEMA IF NOT EXISTS event_schema;

-- Установка владельца для схемы event_schema
ALTER SCHEMA event_schema OWNER TO postgres;

-- Создание таблицы event_members
CREATE TABLE IF NOT EXISTS event_schema.event_members (
                                                          id         UUID PRIMARY KEY NOT NULL,
                                                          approved   BOOLEAN,
                                                          clubmember BOOLEAN,
                                                          company    VARCHAR(255),
    email      VARCHAR(255),
    event_id   UUID,
    firstname  VARCHAR(255),
    lastname   VARCHAR(255),
    middlename VARCHAR(255),
    phone      VARCHAR(255),
    position   VARCHAR(255)
    );

-- Установка владельца для таблицы event_members
ALTER TABLE event_schema.event_members OWNER TO postgres;

-- Создание таблицы events
CREATE TABLE IF NOT EXISTS event_schema.events (
                                                   event_id      UUID PRIMARY KEY NOT NULL,
                                                   event_date    TIMESTAMP,
                                                   event_name    VARCHAR(255),
    event_summary VARCHAR(255),
    reg_open      BOOLEAN,
    orgid         UUID,
    address       VARCHAR(255)
    );

-- Установка владельца для таблицы events
ALTER TABLE event_schema.events OWNER TO postgres;

-- Создание таблицы roles
CREATE TABLE IF NOT EXISTS event_schema.roles (
                                                  id   UUID PRIMARY KEY NOT NULL,
                                                  role VARCHAR(255)
    );

-- Установка владельца для таблицы roles
ALTER TABLE event_schema.roles OWNER TO postgres;

-- Создание таблицы users
CREATE TABLE IF NOT EXISTS event_schema.users (
                                                  id       UUID PRIMARY KEY NOT NULL,
                                                  login    VARCHAR(255),
    password VARCHAR(255)
    );

-- Установка владельца для таблицы users
ALTER TABLE event_schema.users OWNER TO postgres;

-- Создание таблицы users_roles
CREATE TABLE IF NOT EXISTS event_schema.users_roles (
                                                        user_id UUID REFERENCES event_schema.users(id),
    role_id UUID REFERENCES event_schema.roles(id),
    CONSTRAINT users_roles_pk PRIMARY KEY (user_id, role_id)
    );

-- Установка владельца для таблицы users_roles
ALTER TABLE event_schema.users_roles OWNER TO postgres;

INSERT INTO event_schema.users VALUES
                                     ('6c81ae12-7bac-4eab-ae1a-160743edbdaf','admin','$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i');

INSERT INTO event_schema.roles VALUES
                                   ('d2dd39a5-b13c-426f-aa29-da82bb6dbf29','ROLE_ADMIN'),
                                   ('9506f129-5b3b-4b82-8202-75aefd1da713','ROLE_ORGANIZATOR'),
                                   ('f93d3f09-452a-4dc8-9b60-c3177242f069','ROLE_USER');

INSERT INTO event_schema.events VALUES
                                   ('603b961f-a315-4c6e-9305-bea0ef60a98c','2024-07-07 17:12:19.146000','first_event','idk',true,'6c81ae12-7bac-4eab-ae1a-160743edbdaf', 'sadasd');

INSERT INTO event_schema.users_roles VALUES
                                   ('6c81ae12-7bac-4eab-ae1a-160743edbdaf','d2dd39a5-b13c-426f-aa29-da82bb6dbf29');

INSERT INTO event_schema.event_members VALUES
                                    ('b90dde83-0ceb-4fa1-b174-6dbeceddbc34',true,true,'company','email','603b961f-a315-4c6e-9305-bea0ef60a98c','Мария','Владимирова','Андреевна','81999999999','0'),
                                    ('57b4c653-9dfb-4372-a029-a7a030c27918',true,true,'company_name','test@test.test','603b961f-a315-4c6e-9305-bea0ef60a98c','Василий','Пупкин','Васильевич','82999999999','0'),
                                    ('673fe76c-9624-46df-ac01-2c6a9ba7cb28',true,true,'Военмех','test1@test.test','603b961f-a315-4c6e-9305-bea0ef60a98c','Милана','Фролова','Львовна','83999999999','0'),
                                    ('7f3795eb-06cf-4fea-a097-3cb2495c3853',true,true,'Яндекс','test2@test.test','603b961f-a315-4c6e-9305-bea0ef60a98c','Дарья','Киселева','Григорьевна','84999999999','0'),
                                    ('d259817f-7a1a-4c36-a1b8-bcbd580944b8',true,true,'СБЕР','test3@test.test','603b961f-a315-4c6e-9305-bea0ef60a98c','Марк','Смирнов','Кириллович','59999999999','0'),
                                    ('3e4bd62d-e3db-4b63-bfb3-5246ccdac251',true,true,'РОСТЕХ','test4@test.test','603b961f-a315-4c6e-9305-bea0ef60a98c','Всеволод','Королев','Максимович','86999999999','0');