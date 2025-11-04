package com.unicauca.FrontManagement.utilities;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Navigation {

    private static final Map<String, Parent> views = new HashMap<>();
    private static final Map<String, Object> controllers = new HashMap<>();
    @Setter @Getter
    private static Stage stage;

    @Setter @Getter
    private static ConfigurableApplicationContext springContext;

    private static Parent loadFXML(String fxml) throws IOException {
        if (!views.containsKey(fxml)){
            FXMLLoader fxmlLoader = new FXMLLoader(Navigation.class.getResource(
                    "/com/unicauca/FrontManagement/" + fxml + ".fxml"
            ));
            fxmlLoader.setControllerFactory(springContext::getBean);

            Parent root = fxmlLoader.load();
            views.put(fxml, root);
            controllers.put(fxml, fxmlLoader.getController());
        }
        return views.get(fxml);
    }

    public static void showAlert(String title, String message, Alert.AlertType typeAlert) {
        Alert alert = new Alert(typeAlert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        Label label = new Label(message);
        label.setWrapText(true);
        label.setStyle("-fx-font-Tebuchet: MS 14px; -fx-font-family: 'Segoe UI'; -fx-text-fill: #2c3e50;");

        VBox container = new VBox(label);
        container.setSpacing(10);
        container.setPadding(new Insets(10));

        alert.getDialogPane().setContent(container);

        alert.getDialogPane().setStyle(
                "-fx-background-color: #f9f9f9; " +
                        "-fx-border-color: #ABBEF6; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        alert.getDialogPane().lookupButton(ButtonType.OK)
                .setStyle("-fx-background-color: #1E2C9E; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7px;");

        alert.showAndWait();
    }

    public static void changeView(String name) {
        try {
            Parent root = loadFXML(name);
            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void changeViewNexWindow(String viewName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigation.class.getResource("/com/unicauca/FrontManagement/" + viewName + ".fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            controllers.put(viewName, loader.getController());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static <T> T loadInAnchorPane(AnchorPane container, String name) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Navigation.class.getResource(
                    "/com/unicauca/FrontManagement/" + name + ".fxml"
            ));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Node node = fxmlLoader.load();

            container.getChildren().setAll(node);

            T controller = fxmlLoader.getController();
            controllers.put(name, controller);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T getController(String name) {
        return (T) controllers.get(name);
    }
}
