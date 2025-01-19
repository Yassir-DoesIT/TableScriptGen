package com.example.gui;

import com.example.dsl.DSLParser;
import com.example.dsl.SQLGenerator;
import com.example.dsl.Validator;
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
    private TextArea descriptionTextArea;

    @FXML
    private TextArea sqlScriptsTextArea;

    @FXML
    private Button browseFileButton;

    @FXML
    public void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier texte");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                descriptionTextArea.setText(content.toString());
            } catch (IOException e) {
                e.printStackTrace();
                descriptionTextArea.setText("Erreur lors de la lecture du fichier !");
            }
        }
    }

    @FXML
    public void handleGenerateScripts() {
        String description = descriptionTextArea.getText().trim();
        if (description.isEmpty()) {
            sqlScriptsTextArea.setText("Veuillez entrer une description ou charger un fichier.");
            return;
        }

        try {
            // Step 1: Parse the DSL input
            var tree = DSLParser.parse(description);

            // Step 2: Validate the parsed tree
            Validator validator = new Validator();
            validator.validate(tree);

            // Step 3: Generate SQL scripts
            SQLGenerator generator = new SQLGenerator();
            String generatedSQL = generator.generate(tree);

            // Display the generated SQL scripts in the text area
            sqlScriptsTextArea.setText(generatedSQL);

        } catch (Exception e) {
            // Display any errors in the SQL scripts text area
            e.printStackTrace();
            sqlScriptsTextArea.setText("Erreur: " + e.getMessage());
        }
    }
}
