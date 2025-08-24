CREATE TABLE usuario (
                         id_usuario BIGINT PRIMARY KEY AUTO_INCREMENT,
                         nombre VARCHAR(255) NOT NULL,
                         apellido VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         documento_identidad VARCHAR(50) NOT NULL UNIQUE,
                         telefono VARCHAR(20),
                         id_rol VARCHAR(50) NOT NULL,
                         salario_base DECIMAL(12, 2) NOT NULL
);