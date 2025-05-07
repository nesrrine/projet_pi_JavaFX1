-- Script pour ajouter la colonne active à la table users
ALTER TABLE users ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
