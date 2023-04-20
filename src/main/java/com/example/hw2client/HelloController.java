package com.example.hw2client;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
     InetAddress address;
    @FXML
    private TextField LocalIP;

    @FXML
    private TextField LocalPort;

    @FXML
    private JFXTextArea MessageArea;

    @FXML
    public JFXListView<Label> MessageView;

    @FXML
    private TextField RemoteIP;

    @FXML
    private TextField RemotePort;

    @FXML
    private TextField StatusView;


    public HelloController() throws UnknownHostException {
    }

    @FXML
    void ClearAllButton(ActionEvent event) {

    }
    @FXML
    void ClearButton(ActionEvent event) throws IOException {
        int index = MessageView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            MessageView.getItems().remove(index);
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData = new byte[1024];
            sendData = String.valueOf(index).getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, Integer.parseInt(RemotePort.getText()));
            clientSocket.send(sendPacket);
        }
        else
            JOptionPane.showMessageDialog(null , "Please Select a Message");
    }

    @FXML
    void SendButton(ActionEvent event) throws IOException {
        ObservableList<Label> items = MessageView.getItems();
        if (RemoteIP.getText().isEmpty() || RemotePort.getText().isEmpty())
            JOptionPane.showMessageDialog(null, "Please Enter The Port Number or The IP", "Error Message", JOptionPane.ERROR_MESSAGE);
        else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime now = LocalDateTime.now();
            Label label = new Label();
            String text = MessageArea.getText();
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData = new byte[1024];
            sendData = (address.getHostName() + ": " + text).getBytes();
            InetAddress address = InetAddress.getByName(RemoteIP.getText()); // get the destination ip
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, Integer.parseInt(RemotePort.getText()));
            clientSocket.send(sendPacket);
            label.setTextFill(Color.RED);
            label.setStyle("-fx-font-family: Cambria;");
            label.setText("Me: " + text +" (" + now.format(dtf) +")");
            items.add(label);
            MessageArea.setText("");

        }
    }

    @FXML
    void TestButton(ActionEvent event) {

    }
    public void addSentence(String sentence) {
       // MessageView.getItems().add(sentence);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            MessageArea.setText("");
            address = InetAddress.getLocalHost(); // get my device name and its ip
            LocalIP.setText(address.getHostAddress()); // get the IP from address
            LocalPort.setText("9876");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}