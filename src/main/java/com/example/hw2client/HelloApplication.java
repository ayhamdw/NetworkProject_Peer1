package com.example.hw2client;

import com.jfoenix.controls.JFXListCell;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

public class HelloApplication extends Application{


    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = loader.load();
        HelloController load = loader.getController();
        byte[] receiveData = new byte[1024];
        ObservableList<Label> items = load.MessageView.getItems();
        DatagramSocket serverSocket = new DatagramSocket(9876);
        new Thread(() -> {
            try {
                while (true) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    InetAddress IPAddress = receivePacket.getAddress(); // the address of client
                    String result = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                    if (result.equals("Clear")) {
                        Platform.runLater(() -> items.clear());
                    } else {
                        try {
                            int index = Integer.parseInt(result);
                            if (index >= 0) {
                                Platform.runLater(() -> items.remove(index));
                            }
                        } catch (Exception ex) {
                            Label label = new Label(result + " (" + now.format(dtf) + ")");
                            label.setTextFill(Color.GREEN);
                            label.setStyle("-fx-font-family: Cambria;");
                            Platform.runLater(() -> items.add(label));
                        }
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        Scene scene = new Scene(root, 918, 566);
        stage.setTitle("Server");
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
         launch();
    }
}