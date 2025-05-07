-- Script pour ajouter les colonnes nécessaires à la récupération de mot de passe
ALTER TABLE users ADD COLUMN reset_token VARCHAR(100) NULL;
ALTER TABLE users ADD COLUMN reset_token_expiry TIMESTAMP NULL;
