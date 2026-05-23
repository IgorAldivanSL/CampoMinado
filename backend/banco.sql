-- ==========================================================
-- Script SQL – Banco de dados: campo_minado
-- Execute no phpMyAdmin ou MySQL CLI
-- ==========================================================

-- 1. Criar o banco de dados
CREATE DATABASE IF NOT EXISTS campo_minado
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. Selecionar o banco
USE campo_minado;

-- 3. Criar a tabela de partidas
CREATE TABLE IF NOT EXISTS partidas (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(100)     NOT NULL,
    pontuacao  INT              NOT NULL DEFAULT 0,
    data       DATETIME         NOT NULL
);

-- 4. Dados de exemplo (opcional)
INSERT INTO partidas (nome, pontuacao, data) VALUES
    ('Igor', 540, '2025-05-01 14:30:00'),
    ('Ana',  320, '2025-05-02 10:15:00'),
    ('Carlos', 0, '2025-05-03 09:00:00');
