package service;

import models.LoginHistory;
import models.User;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class LoginHistoryService {
    private final Connection con;
    private final UserService userService;

    public LoginHistoryService() {
        con = MyDatabase.getInstance().getConnection();
        userService = new UserService();
        ensureTableExists();

        // Vérifier si la table est vide et ajouter des données de test si nécessaire
        checkAndAddTestData();
    }

    /**
     * Vérifie si la table est vide et ajoute des données de test si nécessaire
     */
    private void checkAndAddTestData() {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM login_history")) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("La table login_history est vide, ajout de données de test");
                addTestEntries();
            } else {
                System.out.println("La table login_history contient déjà des données: " + rs.getInt(1) + " entrées");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du contenu de la table: " + e.getMessage());
        }
    }

    /**
     * Enregistre une tentative de connexion dans l'historique
     * @param userId L'identifiant de l'utilisateur
     * @param success Indique si la connexion a réussi
     * @param ipAddress L'adresse IP de l'utilisateur (optionnelle)
     * @param userAgent L'agent utilisateur du navigateur (optionnel)
     * @return true si l'enregistrement a réussi, false sinon
     */
    public boolean recordLogin(int userId, boolean success, String ipAddress, String userAgent) {
        // Vérifier d'abord si la table existe
        ensureTableExists();

        System.out.println("Tentative d'enregistrement de connexion - UserId: " + userId + ", Success: " + success + ", IP: " + ipAddress);

        String sql = "INSERT INTO login_history (user_id, success, ip_address, user_agent) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setBoolean(2, success);
            ps.setString(3, ipAddress != null ? ipAddress : "Unknown");
            ps.setString(4, userAgent != null ? userAgent : "Unknown");

            System.out.println("Exécution de la requête SQL: " + sql + " avec les paramètres: [" + userId + ", " + success + ", " + ipAddress + ", " + userAgent + "]");

            int rowsAffected = ps.executeUpdate();
            System.out.println("Enregistrement de connexion - Lignes affectées: " + rowsAffected);

            if (rowsAffected > 0) {
                // Vérifier que l'enregistrement a bien été ajouté
                try (PreparedStatement checkPs = con.prepareStatement("SELECT COUNT(*) FROM login_history WHERE user_id = ?")) {
                    checkPs.setInt(1, userId);
                    ResultSet rs = checkPs.executeQuery();
                    if (rs.next()) {
                        System.out.println("Nombre total d'entrées pour l'utilisateur " + userId + ": " + rs.getInt(1));
                    }
                }
                return true;
            } else {
                System.out.println("Aucune ligne affectée lors de l'enregistrement de la connexion");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de la connexion: " + e.getMessage());
            e.printStackTrace();

            // Essayer une approche alternative en cas d'erreur
            try {
                String altSql = "INSERT INTO login_history (user_id, success, ip_address, user_agent) VALUES (" +
                                userId + ", " + (success ? "TRUE" : "FALSE") + ", '" +
                                (ipAddress != null ? ipAddress : "Unknown") + "', '" +
                                (userAgent != null ? userAgent : "Unknown") + "')";

                System.out.println("Tentative alternative: " + altSql);

                try (Statement st = con.createStatement()) {
                    int rowsAffected = st.executeUpdate(altSql);
                    System.out.println("Enregistrement alternatif - Lignes affectées: " + rowsAffected);
                    return rowsAffected > 0;
                }
            } catch (SQLException e2) {
                System.err.println("Erreur lors de la tentative alternative: " + e2.getMessage());
                e2.printStackTrace();
            }

            return false;
        }
    }

    /**
     * Récupère l'historique de connexion pour un utilisateur spécifique
     * @param userId L'identifiant de l'utilisateur
     * @return Une liste des entrées d'historique de connexion
     */
    public List<LoginHistory> getUserLoginHistory(int userId) {
        List<LoginHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM login_history WHERE user_id = ? ORDER BY login_time DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LoginHistory entry = createLoginHistoryFromResultSet(rs);
                history.add(entry);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'historique de connexion: " + e.getMessage());
            e.printStackTrace();
        }

        return history;
    }

    /**
     * Récupère l'historique de connexion pour tous les utilisateurs
     * @param limit Limite le nombre d'entrées à récupérer (0 pour toutes)
     * @return Une liste des entrées d'historique de connexion
     */
    public List<LoginHistory> getAllLoginHistory(int limit) {
        List<LoginHistory> history = new ArrayList<>();

        // Vérifier d'abord si la table existe
        ensureTableExists();

        String sql = "SELECT h.*, u.first_name, u.last_name FROM login_history h " +
                     "JOIN users u ON h.user_id = u.id " +
                     "ORDER BY h.login_time DESC";

        if (limit > 0) {
            sql += " LIMIT ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (limit > 0) {
                ps.setInt(1, limit);
            }

            System.out.println("Exécution de la requête SQL: " + sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String userName = rs.getString("first_name") + " " + rs.getString("last_name");
                LoginHistory entry = new LoginHistory(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    userName,
                    rs.getTimestamp("login_time").toLocalDateTime(),
                    rs.getString("ip_address"),
                    rs.getBoolean("success"),
                    rs.getString("user_agent")
                );
                history.add(entry);
            }

            System.out.println("Récupération de " + history.size() + " entrées d'historique");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'historique de connexion: " + e.getMessage());
            e.printStackTrace();

            // En cas d'erreur, essayer de récupérer les données sans le JOIN
            try {
                String simpleSql = "SELECT * FROM login_history ORDER BY login_time DESC";
                if (limit > 0) {
                    simpleSql += " LIMIT " + limit;
                }

                System.out.println("Tentative de récupération simplifiée: " + simpleSql);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(simpleSql);

                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    User user = userService.getById(userId);
                    String userName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "Utilisateur #" + userId;

                    LoginHistory entry = new LoginHistory(
                        rs.getInt("id"),
                        userId,
                        userName,
                        rs.getTimestamp("login_time").toLocalDateTime(),
                        rs.getString("ip_address"),
                        rs.getBoolean("success"),
                        rs.getString("user_agent")
                    );
                    history.add(entry);
                }

                System.out.println("Récupération simplifiée: " + history.size() + " entrées");
            } catch (SQLException e2) {
                System.err.println("Erreur lors de la récupération simplifiée: " + e2.getMessage());
                e2.printStackTrace();
            }
        }

        return history;
    }

    /**
     * Récupère les statistiques de connexion (nombre de connexions réussies/échouées par jour)
     * @param days Nombre de jours à considérer
     * @return Une liste de paires (date, nombre de connexions)
     */
    public List<Object[]> getLoginStats(int days) {
        List<Object[]> stats = new ArrayList<>();
        String sql = "SELECT DATE(login_time) as login_date, success, COUNT(*) as count " +
                     "FROM login_history " +
                     "WHERE login_time >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) " +
                     "GROUP BY DATE(login_time), success " +
                     "ORDER BY login_date DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] stat = new Object[3];
                stat[0] = rs.getDate("login_date").toLocalDate();
                stat[1] = rs.getBoolean("success");
                stat[2] = rs.getInt("count");
                stats.add(stat);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des statistiques de connexion: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Supprime l'historique de connexion d'un utilisateur spécifique
     * @param userId L'identifiant de l'utilisateur
     * @return true si la suppression a réussi, false sinon
     */
    public boolean clearUserLoginHistory(int userId) {
        String sql = "DELETE FROM login_history WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'historique de connexion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime tout l'historique de connexion
     * @return true si la suppression a réussi, false sinon
     */
    public boolean clearAllLoginHistory() {
        String sql = "DELETE FROM login_history";
        try (Statement st = con.createStatement()) {
            int rowsAffected = st.executeUpdate(sql);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'historique de connexion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Crée un objet LoginHistory à partir d'un ResultSet
     * @param rs Le ResultSet contenant les données
     * @return Un objet LoginHistory
     * @throws SQLException Si une erreur survient lors de la récupération des données
     */
    private LoginHistory createLoginHistoryFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        User user = userService.getById(userId);
        String userName = user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown";

        return new LoginHistory(
            rs.getInt("id"),
            userId,
            userName,
            rs.getTimestamp("login_time").toLocalDateTime(),
            rs.getString("ip_address"),
            rs.getBoolean("success"),
            rs.getString("user_agent")
        );
    }

    /**
     * Vérifie si la table login_history existe et la crée si nécessaire
     */
    private void ensureTableExists() {
        try {
            DatabaseMetaData meta = con.getMetaData();
            ResultSet tables = meta.getTables(null, null, "login_history", null);

            if (!tables.next()) {
                // La table n'existe pas, on la crée
                String createTableSQL = "CREATE TABLE login_history (" +
                                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                        "user_id INT NOT NULL, " +
                                        "login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "ip_address VARCHAR(45) NULL, " +
                                        "success BOOLEAN NOT NULL DEFAULT TRUE, " +
                                        "user_agent VARCHAR(255) NULL, " +
                                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";

                try (Statement st = con.createStatement()) {
                    st.executeUpdate(createTableSQL);
                    System.out.println("Table login_history créée avec succès");

                    // Ajouter quelques entrées de test pour démonstration
                    addTestEntries();
                }
            } else {
                System.out.println("La table login_history existe déjà");

                // Vérifier si la table est vide
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM login_history")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        System.out.println("La table login_history est vide, ajout de données de test");
                        addTestEntries();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification/création de la table login_history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ajoute des entrées de test dans l'historique des connexions
     */
    private void addTestEntries() {
        try {
            // Récupérer quelques utilisateurs pour les entrées de test
            List<User> users = new UserService().display();
            if (users.isEmpty()) {
                System.out.println("Aucun utilisateur trouvé pour créer des entrées de test");
                return;
            }

            // Préparer la requête d'insertion
            String sql = "INSERT INTO login_history (user_id, login_time, ip_address, success, user_agent) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            // Générer des dates de connexion récentes
            LocalDateTime now = LocalDateTime.now();

            // Ajouter des entrées pour chaque utilisateur
            for (User user : users) {
                // Connexion réussie aujourd'hui
                ps.setInt(1, user.getId());
                ps.setTimestamp(2, Timestamp.valueOf(now.minusHours((int) (Math.random() * 5))));
                ps.setString(3, "192.168.1." + (int) (Math.random() * 255));
                ps.setBoolean(4, true);
                ps.setString(5, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                ps.executeUpdate();

                // Connexion échouée hier
                ps.setInt(1, user.getId());
                ps.setTimestamp(2, Timestamp.valueOf(now.minusDays(1).minusHours((int) (Math.random() * 12))));
                ps.setString(3, "192.168.1." + (int) (Math.random() * 255));
                ps.setBoolean(4, false);
                ps.setString(5, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                ps.executeUpdate();

                // Connexion réussie il y a 3 jours
                ps.setInt(1, user.getId());
                ps.setTimestamp(2, Timestamp.valueOf(now.minusDays(3).minusHours((int) (Math.random() * 24))));
                ps.setString(3, "192.168.1." + (int) (Math.random() * 255));
                ps.setBoolean(4, true);
                ps.setString(5, "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1");
                ps.executeUpdate();
            }

            System.out.println("Données de test ajoutées à l'historique des connexions");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout des données de test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
