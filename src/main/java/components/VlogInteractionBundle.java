package components;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.*;
import service.*;
import utils.BadWordDetector;
import utils.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class VlogInteractionBundle {
    private final LikeService likeService = new LikeService();
    private final CommentService commentService = new CommentService();
    private final UserService userService = new UserService();
    private final NotificationService notificationService = new NotificationService();

    private final ReactionService reactionService = new ReactionService();

    public VBox create(Vlog vlog, Runnable onInteractionChanged) {
        VBox interactionBox = new VBox(10);
        interactionBox.setPadding(new Insets(10));
        interactionBox.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Ajout du header avec bouton "⋯"
        interactionBox.getChildren().addAll(
                createVlogHeader(vlog),
                createLikeDislikeBox(vlog, onInteractionChanged),
                createCommentSection(vlog, onInteractionChanged)
        );
        return interactionBox;
    }

    private HBox createVlogHeader(Vlog vlog) {
        VlogService vlogService = new VlogService();
        Label authorLabel = new Label("Par " + vlog.getAuthorId());
        authorLabel.setStyle("-fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header;

        boolean isAuthor = Session.getCurrentUser().getId() == vlog.getAuthorId();

        if (!isAuthor) {
            MenuButton menuButton = new MenuButton("⋯");
            MenuItem reportItem = new MenuItem("🚩 Signaler la publication");

            if (vlogService.hasAlreadyReported(Session.getCurrentUser().getId(), vlog.getId())) {
                reportItem.setDisable(true);
                reportItem.setText("🚩 Déjà signalée");
            }

            reportItem.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Signaler Vlog");
                confirm.setHeaderText("Voulez-vous signaler cette publication ?");
                confirm.setContentText("Après 5 signalements, elle sera supprimée automatiquement.");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        vlogService.reportVlog(Session.getCurrentUser().getId(), vlog.getId());

                        NotificationService notificationService = new NotificationService();
                        notificationService.sendNotification(
                                vlog.getAuthorId(),
                                Session.getCurrentUser().getFirstName() + " " + Session.getCurrentUser().getLastName() + " a signalé votre vlog."
                        );

                        new Alert(Alert.AlertType.INFORMATION, "Publication signalée avec succès.").show();

                        int nbSignalements = vlogService.getNombreSignalements(vlog.getId());
                        if (nbSignalements >= 5) {
                            vlogService.deleteVlog(vlog.getId());
                            new Alert(Alert.AlertType.INFORMATION, "La publication a été supprimée automatiquement après 5 signalements.").show();
                        }

                        reportItem.setDisable(true);
                        reportItem.setText("🚩 Déjà signalée");
                    }
                });
            });

            menuButton.getItems().add(reportItem);
            header = new HBox(10, authorLabel, spacer, menuButton);
        } else {
            header = new HBox(10, authorLabel, spacer);
        }

        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private VBox createCommentSection(Vlog vlog, Runnable onInteractionChanged) {
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Ajouter un commentaire...");
        commentArea.setPrefRowCount(2);

        Button sendBtn = new Button("💬 Commenter");
        sendBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 8;");

        VBox commentList = new VBox(5);
        commentList.setVisible(false);
        commentList.setManaged(false);

        final int[] currentPage = {0};
        final int commentsPerPage = 5;

        Button prevBtn = new Button("⏪ Précédent");
        Button nextBtn = new Button("⏩ Suivant");
        HBox paginationBox = new HBox(10, prevBtn, nextBtn);
        paginationBox.setAlignment(Pos.CENTER);

        prevBtn.setOnAction(e -> {
            if (currentPage[0] > 0) {
                currentPage[0]--;
                refreshComments(vlog.getId(), commentList, currentPage[0], commentsPerPage);
            }
        });

        nextBtn.setOnAction(e -> {
            int totalComments = commentService.getCommentsByVlog(vlog.getId()).size();
            if ((currentPage[0] + 1) * commentsPerPage < totalComments) {
                currentPage[0]++;
                refreshComments(vlog.getId(), commentList, currentPage[0], commentsPerPage);
            }
        });

        Button toggleCommentsBtn = new Button("📃 Voir les commentaires");
        toggleCommentsBtn.setOnAction(e -> {
            boolean visible = commentList.isVisible();
            commentList.setVisible(!visible);
            commentList.setManaged(!visible);
            if (!visible) {
                currentPage[0] = 0;
                refreshComments(vlog.getId(), commentList, currentPage[0], commentsPerPage);
            }
            if (Session.getCurrentUser().getId() != vlog.getAuthorId()) {
                notificationService.sendNotification(vlog.getAuthorId(),
                        Session.getCurrentUser().getFirstName() + " " + Session.getCurrentUser().getLastName() + " a commenté votre vlog !");
            }

        });

        sendBtn.setOnAction(e -> {
            String content = commentArea.getText().trim();
            if (!content.isEmpty()) {
                if (BadWordDetector.containsBadWords(content)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Commentaire inapproprié");
                    alert.setContentText("Merci de rester respectueux.");
                    alert.show();
                    return;
                }
                commentService.addComment(new Comment(vlog.getId(), Session.getCurrentUser().getId(), content));
                commentArea.clear();
                currentPage[0] = 0;
                refreshComments(vlog.getId(), commentList, currentPage[0], commentsPerPage);
                onInteractionChanged.run();
            }
            if (Session.getCurrentUser().getId() != vlog.getAuthorId()) {
                notificationService.sendNotification(vlog.getAuthorId(),
                        Session.getCurrentUser().getFirstName() + " " + Session.getCurrentUser().getLastName() + " a commanter  a votre  vlog !");
            }
        });

        VBox section = new VBox(10, commentArea, sendBtn, toggleCommentsBtn, commentList, paginationBox);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-background-radius: 8;");
        return section;
    }
    private HBox createLikeDislikeBox(Vlog vlog, Runnable onInteractionChanged) {
        Button likeBtn = new Button("👍");
        Button dislikeBtn = new Button("👎");
        Label likeCount = new Label();
        Label dislikeCount = new Label();
        Label likeUsers = new Label("Aimeurs: ");
        Label dislikeUsers = new Label("Détesteurs: ");

        Consumer<List<User>> showUserProfiles = (List<User> users) -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Utilisateurs");

            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(10));
            for (User u : users) {
                vbox.getChildren().add(new Label(u.getFirstName() + " " + u.getLastName()));
            }

            Button close = new Button("Fermer");
            close.setOnAction(ev -> stage.close());
            vbox.getChildren().add(close);

            stage.setScene(new Scene(vbox, 300, 200));
            stage.show();
        };

        Runnable update = () -> {
            boolean liked = likeService.hasLiked(vlog.getId(), Session.getCurrentUser().getId(), true);
            boolean disliked = likeService.hasLiked(vlog.getId(), Session.getCurrentUser().getId(), false);

            likeBtn.setStyle(liked ? "-fx-background-color: #27ae60; -fx-text-fill: white;" : "-fx-background-color: #ecf0f1;");
            dislikeBtn.setStyle(disliked ? "-fx-background-color: #c0392b; -fx-text-fill: white;" : "-fx-background-color: #ecf0f1;");

            likeCount.setText(String.valueOf(likeService.countLikes(vlog.getId(), true)));
            dislikeCount.setText(String.valueOf(likeService.countLikes(vlog.getId(), false)));

            List<User> likedUsers = likeService.getUsersWhoLiked(vlog.getId());
            List<User> dislikedUsers = likeService.getUsersWhoDisliked(vlog.getId());

            likeUsers.setText("Aimeurs: " + likedUsers.size());
            dislikeUsers.setText("Détesteurs: " + dislikedUsers.size());

            likeUsers.setOnMouseClicked(e -> showUserProfiles.accept(likedUsers));
            dislikeUsers.setOnMouseClicked(e -> showUserProfiles.accept(dislikedUsers));
        };

        likeBtn.setOnAction(e -> {
            if (likeService.hasLiked(vlog.getId(), Session.getCurrentUser().getId(), true)) {
                likeService.removeLike(vlog.getId(), Session.getCurrentUser().getId());
            } else {
                if (likeService.hasLiked(vlog.getId(), Session.getCurrentUser().getId(), false)) {
                    likeService.removeLike(vlog.getId(), Session.getCurrentUser().getId());
                }
                likeService.likeOrDislike(new Like(vlog.getId(), Session.getCurrentUser().getId(), true));
                if (Session.getCurrentUser().getId() != vlog.getAuthorId()) {
                    notificationService.sendNotification(vlog.getAuthorId(),
                            Session.getCurrentUser().getFirstName() + " " + Session.getCurrentUser().getLastName() + " a aimé votre vlog !");
                }
            }
            update.run();
        });

        dislikeBtn.setOnAction(e -> {
            if (likeService.hasLiked(vlog.getId(), Session.getCurrentUser().getId(), false)) {
                likeService.removeLike(vlog.getId(), Session.getCurrentUser().getId());
            } else {
                if (likeService.hasLiked(vlog.getId(), Session.getCurrentUser().getId(), true)) {
                    likeService.removeLike(vlog.getId(), Session.getCurrentUser().getId());
                }
                likeService.likeOrDislike(new Like(vlog.getId(), Session.getCurrentUser().getId(), false));
                if (Session.getCurrentUser().getId() != vlog.getAuthorId()) {
                    notificationService.sendNotification(vlog.getAuthorId(),
                            Session.getCurrentUser().getFirstName() + " " + Session.getCurrentUser().getLastName() + " a détesté votre vlog !");
                }
            }
            update.run();
        });

        update.run();

        HBox box = new HBox(10, likeBtn, likeCount, dislikeBtn, dislikeCount, likeUsers, dislikeUsers);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
    private void refreshComments(int vlogId, VBox list, int page, int commentsPerPage) {
        list.getChildren().clear();

        VlogService vlogService = new VlogService();
        Vlog vlog = vlogService.getVlogById(vlogId); // ✅ Pour la notification

        List<Comment> allComments = commentService.getCommentsByVlog(vlogId);

        int start = page * commentsPerPage;
        int end = Math.min(start + commentsPerPage, allComments.size());
        List<Comment> currentComments = allComments.subList(start, end);

        for (Comment c : currentComments) {
            User user = userService.getById(c.getUserId());

            Label content = new Label(c.getContent());
            content.setWrapText(true);
            content.setMaxWidth(400);

            Label userLabel = new Label("Par: " + user.getFirstName() + " " + user.getLastName());
            userLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d;");

            // Traduction
            ComboBox<String> languageComboBox = new ComboBox<>();
            languageComboBox.getItems().addAll("Français", "Anglais", "Arabe", "Espagnol", "Allemand", "Italien", "Japonais");
            languageComboBox.setValue("Français");

            Button translateBtn = new Button("🌐 Traduire");
            translateBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8;");

            translateBtn.setOnAction(e -> {
                String selectedLang = languageComboBox.getValue();
                String targetLang;

                switch (selectedLang) {
                    case "Anglais":
                        targetLang = "en";
                        break;
                    case "Arabe":
                        targetLang = "ar";
                        break;
                    case "Espagnol":
                        targetLang = "es";
                        break;
                    case "Allemand":
                        targetLang = "de";
                        break;
                    case "Italien":
                        targetLang = "it";
                        break;
                    case "Japonais":
                        targetLang = "ja";
                        break;
                    default:
                        targetLang = "fr";
                        break;
                }

                try {
                    String translated = TranslationService.translateText(c.getContent(), "fr", targetLang);
                    content.setText(translated);
                    translateBtn.setText("✔ Traduit (" + selectedLang + ")");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Impossible de traduire le commentaire pour le moment.").show();
                }
            });

            HBox translateBox = new HBox(10, languageComboBox, translateBtn);

            VBox commentBox = new VBox(5, content, userLabel, translateBox);
            commentBox.setPadding(new Insets(8));
            commentBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-background-radius: 6; -fx-border-radius: 6;");

            if (Session.getCurrentUser().getId() == c.getUserId()) {
                Button editBtn = new Button("✏️ Modifier");
                Button deleteBtn = new Button("🗑️ Supprimer");

                editBtn.setOnAction(e -> {
                    TextInputDialog dialog = new TextInputDialog(c.getContent());
                    dialog.setTitle("Modifier commentaire");
                    dialog.setHeaderText("Éditez votre commentaire :");

                    dialog.showAndWait().ifPresent(newContent -> {
                        if (!newContent.trim().isEmpty() && !BadWordDetector.containsBadWords(newContent)) {
                            c.setContent(newContent.trim());
                            commentService.updateComment(c);
                            refreshComments(vlogId, list, page, commentsPerPage);
                        }
                    });
                });

                deleteBtn.setOnAction(e -> {
                    commentService.deleteComment(c.getId());
                    refreshComments(vlogId, list, page, commentsPerPage);
                });

                HBox actionsBox = new HBox(10, editBtn, deleteBtn);
                commentBox.getChildren().add(actionsBox);
            }

            // Réponses aux commentaires
// Réponses aux commentaires
            List<Comment> replies = commentService.getReplies(c.getId());
            for (Comment reply : replies) {
                User replyUser = userService.getById(reply.getUserId());

                Label replyContent = new Label(reply.getContent());
                replyContent.setWrapText(true);
                replyContent.setMaxWidth(380);

                Label replyUserLabel = new Label("Réponse de: " + replyUser.getFirstName() + " " + replyUser.getLastName());
                replyUserLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d;");

                // Création d'une boîte pour la réponse
                VBox replyBox = new VBox(3, replyContent, replyUserLabel);
                replyBox.setPadding(new Insets(5));
                replyBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #ddd; -fx-background-radius: 6; -fx-border-radius: 6;");

                // Bouton de suppression
                Button deleteBtn = new Button("❌ Supprimer");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6;");
                deleteBtn.setOnAction(event -> {
                    // Suppression de la réponse en appelant le service
                    commentService.deleteReply(reply.getId());
                    // Rafraîchissement des commentaires après suppression
                    refreshComments(vlogId, list, page, commentsPerPage);
                });

                // Ajouter le bouton de suppression à la boîte de la réponse
                replyBox.getChildren().add(deleteBtn);

                // Ajouter la réponse à la liste des commentaires
                commentBox.getChildren().add(replyBox);
            }

// Bouton pour répondre à un commentaire
            Button replyBtn = new Button("↩ Répondre");
            replyBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6;");

            replyBtn.setOnAction(event -> {
                TextInputDialog replyDialog = new TextInputDialog();
                replyDialog.setTitle("Répondre au commentaire");
                replyDialog.setHeaderText("Écrivez votre réponse :");

                replyDialog.showAndWait().ifPresent(replyContent -> {
                    // Vérification du contenu de la réponse avant de l'ajouter
                    if (!replyContent.trim().isEmpty() && !BadWordDetector.containsBadWords(replyContent)) {
                        // Ajouter la réponse
                        Comment reply = new Comment(vlogId, Session.getCurrentUser().getId(), replyContent.trim(), c.getId());
                        commentService.addReply(c.getId(), reply);

                        // Rafraîchir les commentaires
                        refreshComments(vlogId, list, page, commentsPerPage);

                        // Si la réponse n'est pas pour l'auteur du commentaire, envoyer une notification
                        if (Session.getCurrentUser().getId() != c.getUserId()) {
                            notificationService.sendNotification(
                                    c.getUserId(),
                                    Session.getCurrentUser().getFirstName() + " " + Session.getCurrentUser().getLastName() +
                                            " a répondu à votre commentaire !"
                            );
                        }
                    } else {
                        // Si la réponse contient des mauvais mots, afficher une alerte
                        new Alert(Alert.AlertType.ERROR, "La réponse contient des mots inappropriés.").show();
                    }
                });
            });

// Ajouter le bouton de réponse à la vue du commentaire
            commentBox.getChildren().add(replyBtn);
            list.getChildren().add(commentBox);
        }}}