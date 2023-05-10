package com.example.hw2client;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ResourceBundle;
import java.util.Scanner;

public class HelloController implements Initializable {
    InetAddress  address = InetAddress.getLocalHost();
    @FXML
    public JFXListView <Label> messageView;
    @FXML
    private JFXTextArea messageArea;
    @FXML
    private TextField localIP;
    @FXML
    public TextField LocalPort;
    @FXML
    private TextField RemoteIP;
    @FXML
    public TextField RemotePort;

    @FXML
    public TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;
    @FXML
    private TextField TCPIP;
    @FXML
    private TextField TCPPort;
    @FXML
    public JFXListView <String> onlineUsers;
    @FXML
    private JFXComboBox <String> availableInterface;
    String name;
    public HelloController() throws UnknownHostException {
    }

    @FXML
    void ClearAllButton(ActionEvent event) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] sendData = new byte[1024];
        sendData = ("Clear").getBytes();
        InetAddress address = InetAddress.getByName(RemoteIP.getText()); // get the destination ip
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, Integer.parseInt(RemotePort.getText()));
        clientSocket.send(sendPacket);
        messageView.getItems().clear();
    }

    @FXML
    void ClearButton(ActionEvent event) throws IOException {
        int index = messageView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            messageView.getItems().remove(index);
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData = new byte[1024];
            sendData = String.valueOf(index).getBytes();
            InetAddress address = InetAddress.getByName(RemoteIP.getText()); // get the destination ip
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, Integer.parseInt(RemotePort.getText()));
            clientSocket.send(sendPacket);
        }
        else
            JOptionPane.showMessageDialog(null , "Please Select a Message");

    }

    @FXML
    void SendButton(ActionEvent event) throws IOException {
        ObservableList<Label> items = messageView.getItems();
        if (RemoteIP.getText().isEmpty() || RemotePort.getText().isEmpty())
            JOptionPane.showMessageDialog(null, "Please Enter The Port Number or The IP", "Error Message", JOptionPane.ERROR_MESSAGE);
        else if (messageArea.getText().isEmpty())
            JOptionPane.showMessageDialog(null , "Please Enter a Message");
        else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime now = LocalDateTime.now();
            Label label = new Label();
            String text = messageArea.getText();
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData = new byte[1024];

            if(check())
                name = usernameTextField.getText();
            else name = address.getHostName();
            sendData = (name+ ": " + text).getBytes();
            InetAddress address = InetAddress.getByName(RemoteIP.getText()); // get the destination ip
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, Integer.parseInt(RemotePort.getText()));
            clientSocket.send(sendPacket);
            label.setTextFill(Color.RED);
            label.setStyle("-fx-font-family: Cambria;");
            label.setText("Me: " + text +" (" + now.format(dtf) +")");
            items.add(label);
            messageArea.setText("");

        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageArea.setText("");
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
        localIP.setText(address.getHostAddress());
        availableInterface.getItems().add("WIFI: " + address.getHostAddress());
        availableInterface.setValue(availableInterface.getItems().get(0));
    }
    @FXML
    void Login(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty())
            JOptionPane.showMessageDialog(null, "Please Enter the Password and Username");
        else if (TCPIP.getText().isEmpty() || TCPPort.getText().isEmpty())
            JOptionPane.showMessageDialog(null , "Please Enter TCP IP and PortNumber");
        else {
            try {
                String username = usernameTextField.getText();
                String password = passwordTextField.getText();
                String portnumber = LocalPort.getText();
                String data = username + "\n" + password + "\n" + portnumber + "\n";

                Socket socket = new Socket(TCPIP.getText(),Integer.parseInt(TCPPort.getText()));
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();

                // Close the socket
                socket.close();
            }
            catch(IOException e){
                //JOptionPane.showMessageDialog(null , "Client: Please Enter Correct IP and Port Number");
                System.out.println(e);
            }
        }
    }
    @FXML
    void Logout(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty())
            JOptionPane.showMessageDialog(null, "Please Enter the Password and Username");
        else if (TCPIP.getText().isEmpty() || TCPPort.getText().isEmpty())
            JOptionPane.showMessageDialog(null , "Please Enter TCP IP and PortNumber");
        else {
            try {
                String username = usernameTextField.getText();
                String data = username + "\n" + "Logout" + "\n";
                Socket socket = new Socket(TCPIP.getText(), Integer.parseInt(TCPPort.getText()));
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();

                // Close the socket
                socket.close();
                if (!onlineUsers.getItems().isEmpty()) {
                    for (int i = 0; i < onlineUsers.getItems().size(); i++) {
                        DatagramSocket clientSocket = new DatagramSocket();
                        byte[] sendData = new byte[1024];
                        String[] strings = onlineUsers.getItems().get(i).split(",");
                        sendData = ("Logout,Now\n" + usernameTextField.getText()).getBytes();
                        InetAddress address = InetAddress.getByName(strings[1]); // get the destination ip
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, Integer.parseInt(strings[2]));
                        clientSocket.send(sendPacket);
                    }
                    usernameTextField.setText("");
                    passwordTextField.setText("");
                    name = address.getHostName();
                    onlineUsers.getItems().clear();
                }
            } catch (IOException e) {
                //JOptionPane.showMessageDialog(null , "Client: Please Enter Correct IP and Port Number");
                System.out.println(e);
            }
        }

    }
    @FXML
    public void getText(MouseEvent event) {
        String[] selectedItem = onlineUsers.getSelectionModel().getSelectedItem().split(",");
        if (selectedItem != null) {
            RemoteIP.setText(selectedItem[1]);
            RemotePort.setText(selectedItem[2]);
        }
    }
    public boolean check() throws FileNotFoundException {
        boolean flag = false;
        Scanner scanner = new Scanner(new File("D:\\SoftwareProject\\HW2Server\\src\\main\\java\\com\\example\\hw2server\\username.txt"));
        while (scanner.hasNextLine()) {
            String string = scanner.nextLine();
            String[] strings = string.split(" ");
            if (strings[0].equals(usernameTextField.getText()) && strings[1].equals(passwordTextField.getText())) {
                flag = true;
            }
        }
        return flag;
    }
    @FXML
    void addInterface(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        HelloController newController = loader.getController();
        HelloApplication application = new HelloApplication();
        application.startServerLogic(newController);
        Stage newStage = new Stage();
        Scene scene = new Scene(root, 918, 566);
        newStage.setScene(scene);
        newStage.show();
    }
    @FXML
    void sendAll(ActionEvent event) throws IOException {
        ObservableList<Label> items = messageView.getItems();
        if (onlineUsers.getItems().isEmpty())
            JOptionPane.showMessageDialog(null , "There is no users!");
        else if (messageArea.getText().isEmpty())
            JOptionPane.showMessageDialog(null , "Please Enter a Message");
        else {
            String text = messageArea.getText();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime now = LocalDateTime.now();
            Label label = new Label();
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData;
            if(check())
                name = usernameTextField.getText();
            else name = address.getHostName();
            sendData = (name+ ": " + text).getBytes();
            for (int i = 0 ; i < onlineUsers.getItems().size() ; i++) {
                String [] strings = onlineUsers.getItems().get(i).split(",");
                InetAddress address = InetAddress.getByName(strings[1]); // get the destination ip
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, Integer.parseInt(strings[2]));
                clientSocket.send(sendPacket);
            }

            label.setTextFill(Color.RED);
            label.setStyle("-fx-font-family: Cambria;");
            label.setText("Me: " + text +" (" + now.format(dtf) +")");
            items.add(label);
            messageArea.setText("");
        }
    }
}