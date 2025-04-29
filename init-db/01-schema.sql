-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS nesrine;
USE nesrine;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    birth_date DATE DEFAULT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS transport_reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transport_id INT NOT NULL,
    user_id INT NOT NULL,
    reservation_date DATE NOT NULL,
    nombre_personnes INT NOT NULL,
    pdf_path VARCHAR(255),
    FOREIGN KEY (transport_id) REFERENCES transport(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert users data
INSERT INTO users (id, first_name, last_name, email, password, address, phone, birth_date, role) VALUES
(2, 'mohamed', 'rayen esprit', 'rayen@med.com', '$2a$10$3WzMZ5cxRh.oGZv0dXntEuSuK6ZN9tHeCZTKfMu/x8pvSQ1eRA7De', 'esprit', '25362547', '2025-04-15', 'Admin'),
(3, 'med', 'rayen', 'med@esprit.tn', '$2a$10$VnPW1qTJ1DZREbajYcXuSuvNw47k.cYJ8cdQIP5fwJSBPprCMUkNy', 'espri', '25362514', '2025-04-08', 'Client'),
(5, 'medTransporteur', 'rayen', 'medTransporteur@esprit.tn', '$2a$10$VnPW1qTJ1DZREbajYcXuSuvNw47k.cYJ8cdQIP5fwJSBPprCMUkNy', 'espri', '25362514', '2025-04-08', 'Transporteur'),
(6, 'medHôte', 'rayen', 'medHôte@esprit.tn', '$2a$10$VnPW1qTJ1DZREbajYcXuSuvNw47k.cYJ8cdQIP5fwJSBPprCMUkNy', 'espri', '25362514', '2025-04-08', 'Hôte'),
(7, 'medRestaurant', 'rayen', 'medRestaurant@esprit.tn', '$2a$10$VnPW1qTJ1DZREbajYcXuSuvNw47k.cYJ8cdQIP5fwJSBPprCMUkNy', 'espri', '25362514', '2025-04-08', 'Restaurant'),
(8, 'medVoyageur', 'rayen', 'medVoyageur@esprit.tn', '$2a$10$VnPW1qTJ1DZREbajYcXuSuvNw47k.cYJ8cdQIP5fwJSBPprCMUkNy', 'espri', '25362514', '2025-04-08', 'Voyageur'),
(9, 'amir', 'jbari', 'amirj5353@gmail.com', '$2a$10$IeRAvhBdpkwBdR/r.03nPeNDHRE9dHpGBqG5mgIogyqXDUS5gxZF6', 'Tunis', '58261801', '2010-04-01', 'Admin'),
(10, 'amm', 'alllll', 'amir@gmail.com', '$2a$10$eZD9FCvl9c7Sa6m49WbHfew4/ztUWTpElPOplDgrxbhTv5ltlcUAy', 'aaaa', '23121231', '2025-04-11', 'Transporteur'),
(13, 'amani', 'amani', 'amani@gmail.com', '$2a$10$5dui6gspcYEKc3Qwzp6/uORvXBRjLKQktGnUsC/mYv.V/Xy4Lq5NK', 'bardo', '50759413', '2025-04-22', 'Transporteur'),
(14, 'ons', 'nn', 'ons@gmail.com', '$2a$10$yQPXX/W9fXL01zRX455OkuVU3S.OBZMG774n0/2ojnCvoQIzCqssW', 'bardo', '22336447', '2025-04-16', 'Admin'),
(15, 'ons', 'nn', 'ons2@gmail.com', '$2a$10$rV4MARru6vybjlJSdFRhje5t65E5mKikp.7CoE5dL8YfttKJ.BgNC', 'bardo', '22669771', '2025-04-17', 'Voyageur'),
(16, 'nes', 'rine', 'nesrine@gmail.com', '$2a$10$YQ7oooMNc3E1/ygUafGhZOPJzRN6pxxueM2/.7sNl/hosgL0xYwwK', 'ariana', '25023669', '2025-04-30', 'Restaurant');

-- Restaurant table
CREATE TABLE IF NOT EXISTS restaurant (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    localisation VARCHAR(255) NOT NULL,
    image VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    prix DOUBLE NOT NULL,
    lat DECIMAL(10,6) DEFAULT NULL,
    lng DECIMAL(10,6) DEFAULT NULL,
    image1 VARCHAR(255) DEFAULT NULL,
    image2 VARCHAR(255) DEFAULT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Add promotion column to restaurant table if it doesn't exist
ALTER TABLE restaurant ADD COLUMN IF NOT EXISTS promotion BOOLEAN DEFAULT FALSE;

-- Insert restaurant data
INSERT INTO restaurant (id, nom, localisation, image, description, prix, lat, lng, image1, image2, user_id) VALUES
(12, 'Dar El Jeld', '5 – 10 Rue Dar El Jeld, Tunis 1006 Tunisie', 'restaurant_images/dar_el_jeld.jpg', 'DAR EL JELD est un restaurant de cuisine traditionnelle tunisienne, avec promo les plats', 20, 0.000000, 0.000000, 'restaurant_images/dar_el_jeld_1.jpg', 'restaurant_images/dar_el_jeld_2.jpg', 16),
(13, 'pasta cosi', 'Rue du Lac de Constance, Les Berges du Lac I, 1053 Tunis, Tunisia', 'restaurant_images/pasta_cosi.jpg', 'Lundi-Jeudi : 12h-23h\nVendredi-Dimanche : 12h-00h\nContact\n(+216) 53 283 233 avec des promos', 30, 0.000000, 0.000000, 'restaurant_images/pasta_cosi_1.jpg', 'restaurant_images/pasta_cosi_2.jpg', 16);

-- Transport table
CREATE TABLE IF NOT EXISTS transport (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    prix DOUBLE NOT NULL,
    disponibilte TINYINT(1) NOT NULL DEFAULT 0,
    image VARCHAR(255) NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert transport data
INSERT INTO transport (id, type, description, prix, disponibilte, image, user_id) VALUES
(18, 'bmw', 'louer bmw hjchduhvfujij', 200, 0, 'transport_images/bmw_x6.jpg', 13),
(19, 'BMW i4', 'Ben Jemaa Motors SA Concessionnaire BMW-MINI Charguia 2 16, Rue de L\'Artisanat, 1080 Ariana Tunis\n+216-70-837355', 150, 0, 'transport_images/bmw_i4.jpg', 13);

-- Vlog table
CREATE TABLE IF NOT EXISTS vlog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    image VARCHAR(255) DEFAULT NULL,
    video VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert vlog data
INSERT INTO vlog (id, content, image, video, created_at, author_id) VALUES
(1, 'eazdjdqs dsqkjdqjhsbdsq jdqsqdbjdsq', 'vlog_images/logo.png', 'vlog_videos/video1.mp4', '2025-04-15 14:26:34', 9),
(2, 'behi hahadh', 'vlog_images/image1.jpeg', 'vlog_videos/video2.mp4', '2025-04-15 16:08:43', 10);

-- Reclamation table
CREATE TABLE IF NOT EXISTS reclamation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    auteur_id INT NOT NULL,
    cible_id INT NOT NULL,
    titre VARCHAR(255) NOT NULL,
    description TEXT DEFAULT NULL,
    statut VARCHAR(50) DEFAULT 'En attente',
    date_soumission TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    photo VARCHAR(255) DEFAULT NULL,
    document VARCHAR(255) DEFAULT NULL,
    categorie VARCHAR(100) DEFAULT NULL,
    FOREIGN KEY (auteur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (cible_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert reclamation data
INSERT INTO reclamation (id, auteur_id, cible_id, titre, description, statut, date_soumission, photo, document, categorie) VALUES
(1, 10, 6, 'QFDSDQFSQD', 'VQSDFQSDDS', 'En cours', '2025-04-15 15:08:18', NULL, NULL, 'Hôte');
