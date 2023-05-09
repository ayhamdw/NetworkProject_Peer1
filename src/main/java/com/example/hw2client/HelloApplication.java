package com.example.hw2client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
public class HelloApplication extends Application{
    public void startServerLogic(HelloController controller) {
        byte[] receiveData = new byte[1024];
        ObservableList<Label> items = controller.MessageView.getItems();
        ObservableList<String> items1 = controller.onlineUsers.getItems();
        new Thread(() -> {
            try {
                Random random = new Random();
                int port = random.nextInt(65535)+1;
                System.out.println(port);
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
                Parent root = loader.load();
                HelloController load = loader.getController();
                DatagramSocket serverSocket = new DatagramSocket((port));
                while (true) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    InetAddress IPAddress = receivePacket.getAddress(); // the address of client
                    String result = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                    if (result.contains("Logout,Now")){
                        JOptionPane.showMessageDialog(null, "Inside");
                        String [] strings = result.split("\n");
                        JOptionPane.showMessageDialog(null , strings[1]);
                        for (int i = 0; i < items1.size(); i++){
                            if(items1.get(i).contains(strings[1])) {
                                int finalI = i;
                                Platform.runLater(()->items1.remove(finalI));
                            }
                        }
                    }
                    else if (result.contains(":")) {
                        Refactor(items, dtf, now, result);
                    } else if (result.contains(",") && result.length() > 10) {
                        int flag = 1;
                        if (items1.size() == 0) {
                            Platform.runLater(()-> items1.add(result));
                        }
                        else {
                            for (int i = 0; i < items1.size(); i++) {
                                if (controller.onlineUsers.getItems().get(i).equals(result)) {
                                    flag = 0;
                                    break;
                                }
                            }
                            if (flag == 1)
                                Platform.runLater(() -> items1.add(result));
                        }
                    } else if (result.equals("Clear")) {
                        Platform.runLater(() -> items.clear());
                    } else {
                        Refactor(items, dtf, now, result);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = loader.load();
        HelloController load = loader.getController();
        startServerLogic(load);

        Scene scene = new Scene(root, 918, 566);
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();
    }

    private void Refactor(ObservableList<Label> items, DateTimeFormatter dtf, LocalDateTime now, String result) {
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

    public static void main(String[] args) {
         launch();
    }
}