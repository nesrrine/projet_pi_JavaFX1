CREATE DATABASE IF NOT EXISTS `nesrine`;
USE `nesrine`;

CREATE TABLE `reclamation` (
  `id` int(11) NOT NULL,
  `auteur_id` int(11) NOT NULL,
  `cible_id` int(11) NOT NULL,
  `titre` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `statut` varchar(50) DEFAULT 'En attente',
  `date_soumission` timestamp NOT NULL DEFAULT current_timestamp(),
  `photo` varchar(255) DEFAULT NULL,
  `document` varchar(255) DEFAULT NULL,
  `categorie` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO `reclamation` (`id`, `auteur_id`, `cible_id`, `titre`, `description`, `statut`, `date_soumission`, `photo`, `document`, `categorie`) VALUES
(1, 10, 6, 'QFDSDQFSQD', 'VQSDFQSDDS', 'En cours', '2025-04-15 15:08:18', NULL, NULL, 'Hôte');

-- --------------------------------------------------------

--
-- Structure de la table `restaurant`
--

CREATE TABLE `restaurant` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `localisation` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `prix` double NOT NULL,
  `lat` decimal(10,6) DEFAULT NULL,
  `lng` decimal(10,6) DEFAULT NULL,
  `image1` varchar(255) DEFAULT NULL,
  `image2` varchar(255) DEFAULT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `restaurant`
--

INSERT INTO `restaurant` (`id`, `nom`, `localisation`, `image`, `description`, `prix`, `lat`, `lng`, `image1`, `image2`, `user_id`) VALUES
(12, 'Dar El Jeld', '5 – 10 Rue Dar El Jeld, Tunis 1006 Tunisie', 'restaurant_images/dar_el_jeld.jpg', 'DAR EL JELD est un restaurant de cuisine traditionnelle tunisienne, avec promo les plats', 20, 0.000000, 0.000000, 'restaurant_images/dar_el_jeld_1.jpg', 'restaurant_images/dar_el_jeld_2.jpg', 16),
(13, 'pasta cosi', 'Rue du Lac de Constance, Les Berges du Lac I, 1053 Tunis, Tunisia', 'restaurant_images/pasta_cosi.jpg', 'Lundi-Jeudi : 12h-23h\nVendredi-Dimanche : 12h-00h\nContact\n(+216) 53 283 233 avec des promos', 30, 0.000000, 0.000000, 'restaurant_images/pasta_cosi_1.jpg', 'restaurant_images/pasta_cosi_2.jpg', 16);

-- --------------------------------------------------------

--
-- Structure de la table `transport`
--

CREATE TABLE `transport` (
  `id` int(11) NOT NULL,
  `type` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `prix` double NOT NULL,
  `disponibilte` tinyint(1) NOT NULL DEFAULT 0,
  `image` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `transport`
--

INSERT INTO `transport` (`id`, `type`, `description`, `prix`, `disponibilte`, `image`, `user_id`) VALUES
(18, 'bmw', 'louer bmw hjchduhvfujij', 200, 0, 'transport_images/bmw_x6.jpg', 13),
(19, 'BMW i4', 'Ben Jemaa Motors SA Concessionnaire BMW-MINI Charguia 2 16, Rue de L\'Artisanat, 1080 Ariana Tunis\n+216-70-837355', 150, 0, 'transport_images/bmw_i4.jpg', 13);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `role` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `first_name`, `last_name`, `email`, `password`, `address`, `phone`, `birth_date`, `role`) VALUES
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

-- --------------------------------------------------------

--
-- Structure de la table `vlog`
--

CREATE TABLE `vlog` (
  `id` int(11) NOT NULL,
  `content` text NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `video` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `author_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `vlog`
--

INSERT INTO `vlog` (`id`, `content`, `image`, `video`, `created_at`, `author_id`) VALUES
(1, 'eazdjdqs dsqkjdqjhsbdsq jdqsqdbjdsq', 'vlog_images/logo.png', 'vlog_videos/video1.mp4', '2025-04-15 14:26:34', 9),
(2, 'behi hahadh', 'vlog_images/image1.jpeg', 'vlog_videos/video2.mp4', '2025-04-15 16:08:43', 10);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `reclamation`
--
ALTER TABLE `reclamation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `auteur_id` (`auteur_id`),
  ADD KEY `cible_id` (`cible_id`);

--
-- Index pour la table `restaurant`
--
ALTER TABLE `restaurant`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_EB95123FA76ED395` (`user_id`);

--
-- Index pour la table `transport`
--
ALTER TABLE `transport`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_66AB212EA76ED395` (`user_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Index pour la table `vlog`
--
ALTER TABLE `vlog`
  ADD PRIMARY KEY (`id`),
  ADD KEY `author_id` (`author_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `reclamation`
--
ALTER TABLE `reclamation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `restaurant`
--
ALTER TABLE `restaurant`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT pour la table `transport`
--
ALTER TABLE `transport`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT pour la table `vlog`
--
ALTER TABLE `vlog`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `reclamation`
--
ALTER TABLE `reclamation`
  ADD CONSTRAINT `reclamation_ibfk_1` FOREIGN KEY (`auteur_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reclamation_ibfk_2` FOREIGN KEY (`cible_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `restaurant`
--
ALTER TABLE `restaurant`
  ADD CONSTRAINT `fk_restaurant_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `transport`
--
ALTER TABLE `transport`
  ADD CONSTRAINT `fk_transport_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `vlog`
--
ALTER TABLE `vlog`
  ADD CONSTRAINT `vlog_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
