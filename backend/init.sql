-- Una base de datos por microservicio, cada una con SU usuario que solo ve SU base.
CREATE DATABASE IF NOT EXISTS nutri_predios    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS nutri_usuarios   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS nutri_mediciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'nutri_predios'@'%'    IDENTIFIED BY 'predios_pass';
CREATE USER IF NOT EXISTS 'nutri_usuarios'@'%'   IDENTIFIED BY 'usuarios_pass';
CREATE USER IF NOT EXISTS 'nutri_mediciones'@'%' IDENTIFIED BY 'mediciones_pass';

GRANT ALL PRIVILEGES ON nutri_predios.*    TO 'nutri_predios'@'%';
GRANT ALL PRIVILEGES ON nutri_usuarios.*   TO 'nutri_usuarios'@'%';
GRANT ALL PRIVILEGES ON nutri_mediciones.* TO 'nutri_mediciones'@'%';
FLUSH PRIVILEGES;
