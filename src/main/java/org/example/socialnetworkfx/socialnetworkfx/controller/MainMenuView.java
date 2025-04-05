package org.example.socialnetworkfx.socialnetworkfx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.socialnetworkfx.socialnetworkfx.domain.Friendship;
import org.example.socialnetworkfx.socialnetworkfx.domain.FriendshipRequest;
import org.example.socialnetworkfx.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.socialnetworkfx.domain.event.FriendshipEntityChange;
import org.example.socialnetworkfx.socialnetworkfx.service.FriendshipRequestService;
import org.example.socialnetworkfx.socialnetworkfx.service.FriendshipService;
import org.example.socialnetworkfx.socialnetworkfx.service.MessageService;
import org.example.socialnetworkfx.socialnetworkfx.service.UserService;
import org.example.socialnetworkfx.socialnetworkfx.utils.Observer;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainMenuView implements Observer<FriendshipEntityChange> {
    @FXML
    public TableColumn firstnameColumn;
    @FXML
    public TableView tableView;
    @FXML
    public TableColumn lastnameColumn;
    @FXML
    public TableColumn<User, String> sinceColumn;
    @FXML
    public Label userNameField;
    @FXML
    public ImageView chatImage;
    @FXML
    public ImageView settingImage;
    @FXML
    public ImageView addImage;
    @FXML
    public ImageView deleteImage;
    @FXML
    public ImageView editImage;
    @FXML
    public Label numberOfRequests;
    @FXML
    public ImageView redDot;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonPrevious;
    @FXML
    private Label labelPage;

    private int pageSize = 2;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;


    private Long IDUser;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService requestService;
    private MessageService messageService;
    private Stage stage;
    ObservableList<User> model = FXCollections.observableArrayList();
    ObservableList<FriendshipRequest> model2 = FXCollections.observableArrayList();

    public void setService(Long IDUser, UserService userService, FriendshipService friendshipService, FriendshipRequestService requestService,MessageService messageService,Stage stage) {
        this.IDUser = IDUser;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.requestService = requestService;
        this.messageService=messageService;
        this.stage = stage;
        friendshipService.addObserver(this);
        initModel();
    }

    public void initialize() {

        firstnameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        sinceColumn.setCellValueFactory(cellData -> {
            Long senderId = cellData.getValue().getID();
            Friendship sender = friendshipService.findOne(senderId, IDUser);
            return new SimpleStringProperty(sender != null ? sender.getDatesince().toString() : "Unknown");
        });

        this.buttonNext.setOnAction(this::onNextPage);
        this.buttonPrevious.setOnAction(this::onPreviousPage);

        tableView.setItems(model);

    }

    private void initModel() {
        Iterable<User> messages = friendshipService.getFriends(IDUser);
        List<User> users = StreamSupport.stream(messages.spliterator(), false)
                .sorted(new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        return o1.getFirstName().compareTo(o2.getFirstName());
                    }
                })
                .collect(Collectors.toList());
        totalNumberOfElements = users.size();
        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalNumberOfElements);
        if (fromIndex <= toIndex) {
            List<User> usersPage = users.subList(fromIndex, toIndex);
            model.setAll(usersPage);
        } else {
            model.clear();
        }
        setUser(IDUser);
        updateRequestIndicator();
        updatePageLabel(); 
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        buttonPrevious.setDisable(currentPage == 0);
        int totalPages = (int) Math.ceil((double) totalNumberOfElements / pageSize);
        buttonNext.setDisable(currentPage >= totalPages - 1);
    }

    private void updateRequestIndicator() {
        if (requestService.getNoNewRequest(IDUser) > 0) {
            redDot.setVisible(true);
            numberOfRequests.setVisible(true);
            numberOfRequests.setText(String.valueOf(requestService.getNoNewRequest(IDUser)));
        } else {
            redDot.setVisible(false);
            numberOfRequests.setVisible(false);
        }
    }

    private void updatePageLabel() {
        int totalPages = (int) Math.ceil((double) totalNumberOfElements / pageSize);
        labelPage.setText("Page " + (currentPage + 1) + " of " + totalPages);

    }

    public void setUser(Long IDUser) {
        User user = userService.findOne(IDUser);
        String fullName = user.getFirstName() + " " + user.getLastName();
        userNameField.setText(fullName);
    }


    public void handleAcceptRequest(MouseEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../accept-request.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        Scene scene = new Scene(root);
        Stage stage2 = new Stage();
        stage2.setScene(scene);
        stage2.setTitle("Yahoo Messenger");

        AcceptRequest requestView = loader.getController();
        requestView.setService(requestService, userService, friendshipService, IDUser);
        stage2.show();

    }

    @Override
    public void update(FriendshipEntityChange friendshipEntityChange) {
        initModel();
    }

    public void handleRemoveFriend(MouseEvent actionEvent) {
        User request = (User) tableView.getSelectionModel().getSelectedItem();
        System.out.println(request.getID());
        Friendship friendship = friendshipService.findOne(request.getID(), IDUser);
        System.out.println(friendship.getID());
        FriendshipRequest friendshipRequest = requestService.findByIDs(IDUser, request.getID());
        System.out.println(friendshipRequest.getID());
        requestService.delete(friendshipRequest.getID());
        friendshipService.delete(friendship.getID());

    }

    public void handleRemoveUser(MouseEvent actionEvent) {
        friendshipService.removeAllByID(IDUser);
        requestService.removeAllByID(IDUser);
        messageService.removeByID(IDUser);
        userService.delete(IDUser);
        stage.close();

    }

    public void handleAccountSetting(MouseEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../settings-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        Scene scene = new Scene(root);
        Stage stage2 = new Stage();
        stage2.setScene(scene);
        stage2.setTitle("Yahoo Messenger");
        SettingsView settingsView = loader.getController();
        settingsView.setService(userService, IDUser);
        stage2.show();
    }

    public void handleChat(MouseEvent actionEvent) throws IOException {
        User request = (User) tableView.getSelectionModel().getSelectedItem();
        if (request == null) {
            // Handle the case where no user is selected, e.g., show an alert
            System.out.println("No user selected");
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../chat-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        Scene scene = new Scene(root);
        Stage stage2 = new Stage();
        stage2.setScene(scene);
        stage2.setTitle("Yahoo Messenger");
        ChatView chatView = loader.getController();
        String name = request.getFirstName() + " " + request.getLastName();
        chatView.setService(messageService, userService, request.getID(), IDUser, name);
        stage2.show();
    }

    public void onNextPage(ActionEvent actionEvent) {
        currentPage ++;
        initModel();
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage --;
        initModel();
    }
}
