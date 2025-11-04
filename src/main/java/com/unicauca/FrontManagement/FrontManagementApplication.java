package com.unicauca.FrontManagement;

import com.unicauca.FrontManagement.utilities.Navigation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

/**
 * JavaFX App
 */

@SpringBootApplication
public class FrontManagementApplication extends Application{
    private static Scene scene;

    // 🔹 Contexto de Spring Boot
    private static ConfigurableApplicationContext springContext;

    // 🔹 Stage principal
    private static Stage primaryStage;

    @Override
    public void init() {
        // Inicializa el contexto de Spring Boot
        springContext = SpringApplication.run(FrontManagementApplication.class);
        // Le pasa el contexto a la clase Navigation
        Navigation.setSpringContext(springContext);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Navigation.setStage(stage);
        Navigation.changeView("Login");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FrontManagementApplication.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}


