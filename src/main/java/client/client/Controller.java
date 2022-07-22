package client.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    static Socket socket;
    static final int PORT = 8189;
    static final String IP_ADDRESS = "192.168.1.140";

    static DataInputStream in;
    static DataOutputStream out;
    public TextField textField;
    public TextArea textArea;
    @FXML
    public HBox startWindow;
    @FXML
    public Pane signInWindow;
    @FXML
    public Pane signUpWindow;
    @FXML
    public TextField login;
    @FXML
    public TextField nickname;
    @FXML
    public PasswordField password;
    @FXML
    public Label existedLogin;
    @FXML
    public VBox chatWindow;
    @FXML
    public TextField loginIn;
    @FXML
    public PasswordField passwordIn;
    @FXML
    public Label invalidData;

    @FXML
    public void signIn() throws IOException {
        String clientData = loginIn.getText()+" "+passwordIn.getText();
        out.writeUTF(clientData);
        if (in.readBoolean()){
            invalidData.setVisible(false);
            signInWindow.setVisible(false);
            chatWindow.setVisible(true);
            startChatWorking();
        }else {
            invalidData.setVisible(true);
        }
    }

    @FXML
    public void signUp() throws IOException {
        String clientData = login.getText()+" "+password.getText()+" "+nickname.getText();
        out.writeUTF(clientData);
        if (in.readBoolean()){
            signUpWindow.setVisible(false);
            existedLogin.setVisible(false);
            chatWindow.setVisible(true);
            startChatWorking();
        }else {
            existedLogin.setVisible(true);
        }
    }
    @FXML
    public void backToStartWindow() throws IOException {
        out.writeUTF("0");
        signUpWindow.setVisible(false);
        signInWindow.setVisible(false);
        startWindow.setVisible(true);
    }
    @FXML
    public void exitAction() throws IOException {
        socket.close();
        System.exit(0);
    }
    @FXML
    public void goToSignUpWindow() throws IOException {
        out.writeBoolean(true);
        startWindow.setVisible(false);
        signUpWindow.setVisible(true);
    }
    @FXML
    protected void goToSignInWindow() throws IOException {
        out.writeBoolean(false);
        startWindow.setVisible(false);
        signInWindow.setVisible(true);
    }

    @FXML
    protected void onSendButtonClick() throws IOException {
        String msgOut =  textField.getText();
        out.writeUTF(msgOut);
        textField.clear();
        textField.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket(IP_ADDRESS,PORT);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startChatWorking(){
        Thread t2 =new Thread(()->{
            try{
                    while (true){
                        String msgIn = in.readUTF();
//                        Boolean msgIn = in.readBoolean();
                        if (msgIn.equals("/end")){
                            System.out.println("server disconnect us");
                            out.writeUTF("/end");
                            break;
                        }
                        textArea.appendText(msgIn+"\n");
//                            System.out.println("Server: "+msgIn);
                    }
            }catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        t2.start();
//        t2.setDaemon(true);
    }


}