CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);