DROP TABLE IF EXISTS usuario;

CREATE TABLE roles (
                       id_rol BIGINT PRIMARY KEY AUTO_INCREMENT,
                       nombre VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE usuario (
                         id_usuario BIGINT PRIMARY KEY AUTO_INCREMENT,
                         nombre VARCHAR(255) NOT NULL,
                         apellido VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         documento_identidad VARCHAR(50) NOT NULL UNIQUE,
                         telefono VARCHAR(20),
                         fecha_nacimiento DATE NOT NULL,
                         direccion VARCHAR(255) NOT NULL,
                         id_rol BIGINT NOT NULL,
                         salario_base DECIMAL(12, 2) NOT NULL,
                         FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');
INSERT INTO roles (nombre) VALUES ('ROLE_ADVISER');
INSERT INTO roles (nombre) VALUES ('ROLE_CLIENT');
