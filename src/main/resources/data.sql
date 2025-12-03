-- Datos iniciales para desarrollo
-- Se ejecuta automáticamente después de crear las tablas
-- Primero borra los usuarios existentes
DELETE FROM users;

INSERT INTO users (id, email, password_hash, role, created_at)
VALUES ('11111111-1111-1111-1111-111111111111',
        'admin@spotify.com',
        '$2a$10$rId1s.tar3YPxGzcI1.k6uirOQylLyN/2RTcTWFBEzABCOlieCVVK', --admin123
        'ADMIN',
        CURRENT_TIMESTAMP());

INSERT INTO users (id, email, password_hash, role, created_at)
VALUES ('22222222-2222-2222-2222-222222222222',
        'user@spotify.com',
        '$2a$10$rId1s.tar3YPxGzcI1.k6uirOQylLyN/2RTcTWFBEzABCOlieCVVK', --admin123
        'USER',
        CURRENT_TIMESTAMP());