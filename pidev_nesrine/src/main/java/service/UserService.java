package service;

import models.User;
import utils.MyDatabase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import service.LoginHistoryService;
import java.util.Random;

public class UserService {
    private final Connection con;

    public UserService() {
        this.con = MyDatabase.getInstance().getCon();
    }

    public void signup(User user) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, address, phone, birth_date, role, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashedPassword);
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getPhone());
            ps.setDate(7, Date.valueOf(user.getBirthDate()));
            ps.setString(8, user.getRole());
            ps.setBoolean(9, user.isActive()); // Ajouter le champ active
            ps.executeUpdate();
            System.out.println("User signed up successfully"); // Debug log


        } catch (SQLException e) {
            System.err.println("Error signing up user: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
    }

    public boolean login(String email, String password) {
        // Vérifier d'abord si la colonne active existe
        boolean columnExists = checkIfColumnExists("users", "active");

        // Construire la requête SQL en fonction de l'existence de la colonne active
        String sql;
        if (columnExists) {
            // Si la colonne active existe, on récupère également cette information
            sql = "SELECT id, password, active FROM users WHERE email = ?";
        } else {
            // Sinon, on récupère juste le mot de passe
            sql = "SELECT id, password FROM users WHERE email = ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String hashedPassword = rs.getString("password");
                boolean match = BCrypt.checkpw(password, hashedPassword);

                // Créer une instance de LoginHistoryService pour enregistrer la connexion
                LoginHistoryService loginHistoryService = new LoginHistoryService();

                // Si le mot de passe ne correspond pas, on refuse l'accès
                if (!match) {
                    System.out.println("Login attempt for email: " + email + " - Password does not match");
                    // Enregistrer la tentative de connexion échouée (mot de passe incorrect)
                    loginHistoryService.recordLogin(userId, false, getClientIpAddress(), getClientUserAgent());
                    return false;
                }

                // Si la colonne active existe, on vérifie si l'utilisateur est actif
                if (columnExists) {
                    int activeValue = rs.getInt("active");
                    boolean isActive = (activeValue == 1);

                    System.out.println("Login attempt for email: " + email + " - Password match: " + match +
                                     " - User active: " + isActive + " (value: " + activeValue + ")");

                    // Si l'utilisateur est inactif, on refuse l'accès
                    if (!isActive) {
                        System.out.println("Login denied: User account is inactive");
                        // Enregistrer la tentative de connexion échouée (compte inactif)
                        loginHistoryService.recordLogin(userId, false, getClientIpAddress(), getClientUserAgent());
                        return false;
                    }
                } else {
                    // Si la colonne active n'existe pas, on ajoute la colonne et on active l'utilisateur par défaut
                    try {
                        String alterSql = "ALTER TABLE users ADD COLUMN active TINYINT(1) NOT NULL DEFAULT 1";
                        try (Statement st = con.createStatement()) {
                            st.executeUpdate(alterSql);
                            System.out.println("Colonne 'active' ajoutée à la table users");
                        }
                    } catch (SQLException e) {
                        // Ignorer l'erreur si la colonne existe déjà
                        if (!e.getMessage().contains("Duplicate column")) {
                            System.err.println("Erreur lors de l'ajout de la colonne 'active': " + e.getMessage());
                        }
                    }

                    System.out.println("Login attempt for email: " + email + " - Password match: " + match +
                                     " - User considered active (column not yet created)");
                }

                // Si on arrive ici, c'est que l'authentification est réussie
                // Enregistrer la connexion réussie
                loginHistoryService.recordLogin(userId, true, getClientIpAddress(), getClientUserAgent());
                return true;
            } else {
                System.out.println("Login attempt for email: " + email + " - User not found");
            }
        } catch (SQLException e) {
            System.err.println("Error logging in user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Récupère l'adresse IP du client
     * @return L'adresse IP ou "Unknown" si elle ne peut pas être déterminée
     */
    private String getClientIpAddress() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException e) {
            return "Unknown";
        }
    }

    /**
     * Récupère l'agent utilisateur du client
     * @return L'agent utilisateur ou "JavaFX Application" par défaut
     */
    private String getClientUserAgent() {
        return "JavaFX Application";
    }

    public User getByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("role")
                );

                // Définir le statut actif par défaut à true
                user.setActive(true);

                // Essayer de récupérer la colonne active si elle existe
                try {
                    user.setActive(rs.getBoolean("active"));
                } catch (SQLException e) {
                    // La colonne n'existe pas encore, on garde la valeur par défaut (true)
                    System.out.println("La colonne 'active' n'existe pas encore dans la base de données.");
                }
                System.out.println("Found user by email: " + email + ", ID: " + user.getId()); // Debug log
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
        System.out.println("No user found for email: " + email); // Debug log
        return null;
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, address = ?, phone = ?, birth_date = ?, role = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getPhone());
            ps.setDate(6, Date.valueOf(user.getBirthDate()));
            ps.setString(7, user.getRole());
            ps.setInt(8, user.getId());
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated user with ID: " + user.getId() + ", rows affected: " + rowsAffected); // Debug log
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace(); // Debug log
            return false;
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted user with ID: " + id); // Debug log
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
    }

    public List<User> display() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    "********", // Hide password
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("role")
                );

                // Définir le statut actif par défaut à true
                user.setActive(true);

                // Essayer de récupérer la colonne active si elle existe
                try {
                    // Récupérer la valeur comme un entier et la convertir en booléen
                    int activeValue = rs.getInt("active");
                    boolean isActive = (activeValue == 1);
                    user.setActive(isActive);
                    System.out.println("Utilisateur " + user.getId() + " (" + user.getFirstName() + " " + user.getLastName() + ") - Valeur active: " + activeValue + " - Statut actif: " + isActive);
                } catch (SQLException e) {
                    // La colonne n'existe pas encore, on garde la valeur par défaut (true)
                    System.out.println("La colonne 'active' n'existe pas encore dans la base de données.");

                    // Essayons d'ajouter la colonne
                    if (!checkIfColumnExists("users", "active")) {
                        try {
                            String alterSql = "ALTER TABLE users ADD COLUMN active TINYINT(1) NOT NULL DEFAULT 1";
                            try (Statement alterSt = con.createStatement()) {
                                alterSt.executeUpdate(alterSql);
                                System.out.println("Colonne 'active' ajoutée à la table users");
                            }
                        } catch (SQLException alterEx) {
                            System.err.println("Erreur lors de l'ajout de la colonne 'active': " + alterEx.getMessage());
                        }
                    }
                }
                users.add(user);
            }
            System.out.println("Retrieved " + users.size() + " users"); // Debug log
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }

        return users;
    }

    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("role")
                );

                // Définir le statut actif par défaut à true
                user.setActive(true);

                // Essayer de récupérer la colonne active si elle existe
                try {
                    user.setActive(rs.getBoolean("active"));
                } catch (SQLException e) {
                    // La colonne n'existe pas encore, on garde la valeur par défaut (true)
                    System.out.println("La colonne 'active' n'existe pas encore dans la base de données.");
                }
                System.out.println("Found user by ID: " + id); // Debug log
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
        System.out.println("No user found for ID: " + id); // Debug log
        return null;
    }

    /**
     * Recherche des utilisateurs selon plusieurs critères
     * @param role Le type/rôle de l'utilisateur (peut être null pour ignorer ce critère)
     * @param email L'email de l'utilisateur (peut être null pour ignorer ce critère)
     * @param lastName Le nom de l'utilisateur (peut être null pour ignorer ce critère)
     * @param firstName Le prénom de l'utilisateur (peut être null pour ignorer ce critère)
     * @return Une liste d'utilisateurs correspondant aux critères de recherche
     */
    public List<User> searchUsers(String role, String email, String lastName, String firstName) {
        List<User> users = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        // Ajouter les conditions de recherche si les paramètres ne sont pas vides
        if (role != null && !role.trim().isEmpty()) {
            sqlBuilder.append(" AND role LIKE ?");
            parameters.add("%" + role + "%");
        }

        if (email != null && !email.trim().isEmpty()) {
            sqlBuilder.append(" AND email LIKE ?");
            parameters.add("%" + email + "%");
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            sqlBuilder.append(" AND last_name LIKE ?");
            parameters.add("%" + lastName + "%");
        }

        if (firstName != null && !firstName.trim().isEmpty()) {
            sqlBuilder.append(" AND first_name LIKE ?");
            parameters.add("%" + firstName + "%");
        }

        String sql = sqlBuilder.toString();
        System.out.println("Search SQL: " + sql); // Debug log

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Définir les paramètres de la requête
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    "********", // Masquer le mot de passe
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("role")
                );

                // Définir le statut actif par défaut à true
                user.setActive(true);

                // Essayer de récupérer la colonne active si elle existe
                try {
                    user.setActive(rs.getBoolean("active"));
                } catch (SQLException e) {
                    // La colonne n'existe pas encore, on garde la valeur par défaut (true)
                    System.out.println("La colonne 'active' n'existe pas encore dans la base de données.");
                }
                users.add(user);
            }
            System.out.println("Search found " + users.size() + " users"); // Debug log
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }

        return users;
    }

    /**
     * Active un utilisateur
     * @param userId L'identifiant de l'utilisateur à activer
     * @return true si l'activation a réussi, false sinon
     */
    public boolean activateUser(int userId) {
        return setUserActiveStatus(userId, true);
    }

    /**
     * Désactive un utilisateur
     * @param userId L'identifiant de l'utilisateur à désactiver
     * @return true si la désactivation a réussi, false sinon
     */
    public boolean deactivateUser(int userId) {
        return setUserActiveStatus(userId, false);
    }

    /**
     * Modifie le statut actif d'un utilisateur
     * @param userId L'identifiant de l'utilisateur
     * @param active Le nouveau statut actif
     * @return true si la modification a réussi, false sinon
     */
    private boolean setUserActiveStatus(int userId, boolean active) {
        System.out.println("Début de setUserActiveStatus pour l'utilisateur " + userId + ", active=" + active);

        // Vérifier d'abord si la colonne active existe
        boolean columnExists = checkIfColumnExists("users", "active");
        System.out.println("La colonne 'active' existe: " + columnExists);

        if (!columnExists) {
            // La colonne n'existe pas encore, on doit l'ajouter
            try {
                // Utiliser TINYINT(1) au lieu de BOOLEAN pour une meilleure compatibilité
                String alterSql = "ALTER TABLE users ADD COLUMN active TINYINT(1) NOT NULL DEFAULT 1";
                System.out.println("Exécution de la requête SQL: " + alterSql);
                try (Statement st = con.createStatement()) {
                    st.executeUpdate(alterSql);
                    System.out.println("Colonne 'active' ajoutée à la table users");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'ajout de la colonne 'active': " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        // Maintenant on peut mettre à jour le statut
        String sql = "UPDATE users SET active = ? WHERE id = ?";
        System.out.println("Exécution de la requête SQL: " + sql + " avec active=" + active + " et id=" + userId);

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Utiliser setInt au lieu de setBoolean pour une meilleure compatibilité
            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Nombre de lignes affectées: " + rowsAffected);

            if (rowsAffected > 0) {
                System.out.println("User " + userId + " " + (active ? "activated" : "deactivated") + " successfully");

                // Vérifier que la mise à jour a bien été effectuée
                String checkSql = "SELECT active FROM users WHERE id = ?";
                try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                    checkPs.setInt(1, userId);
                    ResultSet rs = checkPs.executeQuery();
                    if (rs.next()) {
                        int activeValue = rs.getInt("active");
                        boolean currentStatus = (activeValue == 1);
                        System.out.println("Statut actuel de l'utilisateur " + userId + " après mise à jour: valeur=" + activeValue + ", booléen=" + currentStatus);
                        if (currentStatus != active) {
                            System.err.println("ATTENTION: Le statut n'a pas été correctement mis à jour! Attendu: " + active + ", Obtenu: " + currentStatus);
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la vérification du statut: " + e.getMessage());
                }

                return true;
            } else {
                System.out.println("No user found with ID: " + userId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error " + (active ? "activating" : "deactivating") + " user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vérifie si une colonne existe dans une table
     * @param tableName Le nom de la table
     * @param columnName Le nom de la colonne
     * @return true si la colonne existe, false sinon
     */
    private boolean checkIfColumnExists(String tableName, String columnName) {
        try {
            DatabaseMetaData meta = con.getMetaData();
            ResultSet rs = meta.getColumns(null, null, tableName, columnName);
            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de la colonne: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Génère un token de réinitialisation pour un utilisateur et l'enregistre dans la base de données
     * @param email L'email de l'utilisateur qui demande la réinitialisation
     * @return Le token généré ou null si l'utilisateur n'existe pas
     */
    public String generatePasswordResetToken(String email) {
        // Vérifier si l'utilisateur existe
        User user = getByEmail(email);
        if (user == null) {
            System.out.println("Aucun utilisateur trouvé avec l'email: " + email);
            return null;
        }

        // Vérifier si les colonnes nécessaires existent
        ensureResetColumnsExist();

        // Générer un token unique
        String token = generateToken();

        // Définir la date d'expiration (24 heures à partir de maintenant)
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);

        // Enregistrer le token dans la base de données
        String sql = "UPDATE users SET reset_token = ?, reset_token_expiry = ? WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, Timestamp.valueOf(expiry));
            ps.setString(3, email);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Token de réinitialisation généré pour l'utilisateur: " + email);
                return token;
            } else {
                System.out.println("Erreur lors de la mise à jour du token pour l'utilisateur: " + email);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la génération du token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Vérifie si un token de réinitialisation est valide
     * @param token Le token à vérifier
     * @return L'email de l'utilisateur si le token est valide, null sinon
     */
    public String validatePasswordResetToken(String token) {
        // Vérifier si les colonnes nécessaires existent
        if (!ensureResetColumnsExist()) {
            return null;
        }

        String sql = "SELECT email, reset_token_expiry FROM users WHERE reset_token = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                Timestamp expiry = rs.getTimestamp("reset_token_expiry");

                // Vérifier si le token n'a pas expiré
                if (expiry != null && expiry.toLocalDateTime().isAfter(LocalDateTime.now())) {
                    System.out.println("Token valide pour l'utilisateur: " + email);
                    return email;
                } else {
                    System.out.println("Token expiré pour l'utilisateur: " + email);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la validation du token: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Token invalide ou inexistant: " + token);
        return null;
    }

    /**
     * Réinitialise le mot de passe d'un utilisateur avec un token valide
     * @param token Le token de réinitialisation
     * @param newPassword Le nouveau mot de passe
     * @return true si la réinitialisation a réussi, false sinon
     */
    public boolean resetPassword(String token, String newPassword) {
        // Valider le token et récupérer l'email de l'utilisateur
        String email = validatePasswordResetToken(token);
        if (email == null) {
            System.out.println("Token invalide ou expiré");
            return false;
        }

        // Hacher le nouveau mot de passe
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // Mettre à jour le mot de passe et supprimer le token
        String sql = "UPDATE users SET password = ?, reset_token = NULL, reset_token_expiry = NULL WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, email);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mot de passe réinitialisé avec succès pour l'utilisateur: " + email);
                return true;
            } else {
                System.out.println("Erreur lors de la réinitialisation du mot de passe pour l'utilisateur: " + email);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la réinitialisation du mot de passe: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Génère un token aléatoire pour la réinitialisation de mot de passe
     * @return Un token unique
     */
    private String generateToken() {
        // Générer un UUID aléatoire
        String uuid = UUID.randomUUID().toString();

        // Générer un code numérique à 6 chiffres
        Random random = new Random();
        int numericCode = 100000 + random.nextInt(900000); // Génère un nombre entre 100000 et 999999

        // Combiner les deux pour un token plus facile à utiliser
        return numericCode + "-" + uuid.substring(0, 8);
    }

    /**
     * Vérifie si les colonnes nécessaires à la réinitialisation de mot de passe existent et les crée si nécessaire
     * @return true si les colonnes existent ou ont été créées avec succès, false sinon
     */
    private boolean ensureResetColumnsExist() {
        boolean resetTokenExists = checkIfColumnExists("users", "reset_token");
        boolean resetTokenExpiryExists = checkIfColumnExists("users", "reset_token_expiry");

        if (resetTokenExists && resetTokenExpiryExists) {
            return true;
        }

        try {
            Statement st = con.createStatement();

            if (!resetTokenExists) {
                st.executeUpdate("ALTER TABLE users ADD COLUMN reset_token VARCHAR(100) NULL");
                System.out.println("Colonne 'reset_token' ajoutée à la table users");
            }

            if (!resetTokenExpiryExists) {
                st.executeUpdate("ALTER TABLE users ADD COLUMN reset_token_expiry TIMESTAMP NULL");
                System.out.println("Colonne 'reset_token_expiry' ajoutée à la table users");
            }

            st.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout des colonnes de réinitialisation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vérifie si un utilisateur est actif
     * @param userId L'identifiant de l'utilisateur
     * @return true si l'utilisateur est actif, false sinon ou si l'utilisateur n'existe pas
     */
    public boolean isUserActive(int userId) {
        // Vérifier d'abord si la colonne active existe
        boolean columnExists = checkIfColumnExists("users", "active");

        if (!columnExists) {
            // La colonne n'existe pas encore, on considère que tous les utilisateurs sont actifs par défaut
            String checkUserSql = "SELECT id FROM users WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(checkUserSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                return rs.next(); // L'utilisateur existe, donc il est actif par défaut
            } catch (SQLException e) {
                System.err.println("Error checking if user exists: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        // La colonne existe, on vérifie la valeur
        String sql = "SELECT active FROM users WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int activeValue = rs.getInt("active");
                boolean isActive = (activeValue == 1);
                System.out.println("isUserActive: Utilisateur " + userId + " - Valeur active: " + activeValue + " - Statut actif: " + isActive);
                return isActive;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user is active: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Récupère tous les utilisateurs actifs
     * @return Une liste des utilisateurs actifs
     */
    public List<User> getActiveUsers() {
        List<User> users = new ArrayList<>();

        // Vérifier d'abord si la colonne active existe
        boolean columnExists = checkIfColumnExists("users", "active");

        String sql;
        if (columnExists) {
            sql = "SELECT * FROM users WHERE active = TRUE";
        } else {
            // Si la colonne n'existe pas, tous les utilisateurs sont considérés comme actifs
            sql = "SELECT * FROM users";
        }

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    "********", // Masquer le mot de passe
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("role")
                );

                // Définir le statut actif à true (tous les utilisateurs retournés sont actifs)
                user.setActive(true);

                // Si la colonne existe, essayer de récupérer la valeur réelle
                if (columnExists) {
                    try {
                        user.setActive(rs.getBoolean("active"));
                    } catch (SQLException e) {
                        // Ignorer l'erreur et garder la valeur par défaut
                    }
                }

                // N'ajouter que les utilisateurs actifs
                if (user.isActive()) {
                    users.add(user);
                }
            }
            System.out.println("Retrieved " + users.size() + " active users");
        } catch (SQLException e) {
            System.err.println("Error fetching active users: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Récupère tous les utilisateurs inactifs
     * @return Une liste des utilisateurs inactifs
     */
    public List<User> getInactiveUsers() {
        List<User> users = new ArrayList<>();

        // Vérifier d'abord si la colonne active existe
        boolean columnExists = checkIfColumnExists("users", "active");

        // Si la colonne n'existe pas, aucun utilisateur n'est inactif
        if (!columnExists) {
            System.out.println("La colonne 'active' n'existe pas encore, aucun utilisateur n'est inactif.");
            return users;
        }

        String sql = "SELECT * FROM users WHERE active = FALSE";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    "********", // Masquer le mot de passe
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("role")
                );

                // Définir le statut actif à false
                user.setActive(false);
                users.add(user);
            }
            System.out.println("Retrieved " + users.size() + " inactive users");
        } catch (SQLException e) {
            System.err.println("Error fetching inactive users: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }
}
