package com.example.gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class GUIController {
    @FXML
    private TextArea descriptionTextArea; // Zone pour afficher ou saisir la description.

    @FXML
    private TextArea sqlScriptsTextArea; // Zone pour afficher les scripts SQL générés.

    @FXML
    private Button browseFileButton; // Bouton pour parcourir un fichier.
    /**
     * Méthode appelée lorsque le bouton "Parcourir Fichier" est cliqué.
     * Permet de sélectionner un fichier texte et de charger son contenu
     * dans la zone de texte de description.
     */
    @FXML
    public void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier texte");

        // Filtre pour n'afficher que les fichiers .txt
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers texte", "*.txt")
        );

        // Ouvrir la boîte de dialogue de sélection
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Lire le contenu du fichier et le placer dans la zone de texte
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                descriptionTextArea.setText(content.toString()); // Ajouter le texte lu.
            } catch (IOException e) {
                e.printStackTrace();
                descriptionTextArea.setText("Erreur lors de la lecture du fichier !");
            }
        }
    }

    /**
     * Méthode appelée lorsque le bouton "Générer Scripts SQL" est cliqué.
     * Cette méthode doit contenir la logique pour convertir la description
     * en scripts SQL (ajouter votre propre logique ici).
     */
    @FXML
    public void handleGenerateScripts() {
        String description = descriptionTextArea.getText();
        if (description.isEmpty()) {
            sqlScriptsTextArea.setText("Veuillez entrer une description ou charger un fichier.");
            return;
        }

        // Simuler une génération de scripts SQL (logique à personnaliser)
        String generatedSQL = "CREATE TABLE Example (\n" +
                "    id INT PRIMARY KEY,\n" +
                "    name VARCHAR(100)\n" +
                ");";

        sqlScriptsTextArea.setText(generatedSQL); // Afficher le résultat.
    }
}
