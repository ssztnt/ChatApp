package org.example.socialnetworkfx.socialnetworkfx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.socialnetworkfx.socialnetworkfx.domain.Friendship;
import org.example.socialnetworkfx.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.socialnetworkfx.service.FriendshipRequestService;
import org.example.socialnetworkfx.socialnetworkfx.service.FriendshipService;
import org.example.socialnetworkfx.socialnetworkfx.service.MessageService;
import org.example.socialnetworkfx.socialnetworkfx.service.UserService;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static javafx.application.Application.launch;

public class LoginView {
    @FXML
    public TextField emailText;
    @FXML
    public Button loginButton;
    @FXML
    public Button registerButton;
    @FXML
    public PasswordField passwordText;
    @FXML
    public Label errorMessage;
    @FXML
    public ImageView yahooLogo;
    @FXML
    public Hyperlink hyperlinkHelp;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService requestService;
    private MessageService messageService;

    public void handleRegisterButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../register-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("TALK2ME");

        RegisterView registerView = loader.getController();
        registerView.setService(userService, stage);
        stage.show();


    }

    public void setService(UserService userService, FriendshipService friendshipService, FriendshipRequestService requestService,MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.requestService = requestService;
        this.messageService = messageService;
    }

    public void handleLoginButton(ActionEvent actionEvent) throws IOException {
        String email = emailText.getText();
        String password = passwordText.getText();
        User loggedIn = userService.findByEmail(email, password);
        if (loggedIn != null) {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../main-menu-view.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            MainMenuView mainMenuView = loader.getController();
            mainMenuView.setService(loggedIn.getID(), userService, friendshipService, requestService,messageService, stage);
            errorMessage.setVisible(false);
            emailText.clear();
            passwordText.clear();
            stage.show();

        } else {
            errorMessage.setText("Invalid email or password");
            errorMessage.setVisible(true);
        }
    }

    public void handleYahoo(ActionEvent actionEvent) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://youtu.be/1UJz0O2NjOo"));
    }
}
