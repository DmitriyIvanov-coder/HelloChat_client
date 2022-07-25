package client.client;

import javafx.application.Platform;
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

public class Controller implements Initializable {

    static Socket socket;
    static final int PORT = 8189;

    static final String IP_ADDRESS = "192.168.1.140";

    static DataInputStream in;
    static DataOutputStream out;
    public TextField textField;
    String nickname;
    public TextArea textArea;
    @FXML
    public HBox startWindow;
    @FXML
    public Pane signInWindow;
    @FXML
    public Pane signUpWindow;
    @FXML
    public TextField loginField;
    @FXML
    public TextField nicknameField;
    @FXML
    public PasswordField passwordField;
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
    public Label signUpSpase;
    @FXML
    public Label signInSpace;

    @FXML
    public void signIn() throws IOException {
        if (loginIn.getText().contains(" ")){
            signInSpace.setVisible(true);
        }else {
            String clientData = loginIn.getText() + " " + passwordIn.getText();
            out.writeUTF("Ch "+clientData);
            if (in.readBoolean()) {
                signInSpace.setVisible(false);
                invalidData.setVisible(false);
                signInWindow.setVisible(false);
                chatWindow.setVisible(true);
                nickname = in.readUTF();
                startChatWorking();
            } else {
                signInSpace.setVisible(false);
                invalidData.setVisible(true);
            }
        }
    }

    @FXML
    public void signUp() throws IOException {

        if (loginField.getText().contains(" ")|| nicknameField.getText().contains(" ")){
            signUpSpase.setVisible(true);
        }else {
            nickname = nicknameField.getText();
            String clientData = loginField.getText()+" "+ nicknameField.getText()+" "+ passwordField.getText();
            out.writeUTF("R "+clientData);
            if (in.readBoolean()){
                signUpSpase.setVisible(false);
                existedLogin.setVisible(false);
                signUpWindow.setVisible(false);
                chatWindow.setVisible(true);
                startChatWorking();
            }else {
                signUpSpase.setVisible(false);
                existedLogin.setVisible(true);
            }
        }
    }

    @FXML
    public void backToStartWindow() throws IOException {
        socket.close();
//        out.writeUTF("0");
        signInSpace.setVisible(false);
        invalidData.setVisible(false);
        signUpSpase.setVisible(false);
        existedLogin.setVisible(false);
        signUpWindow.setVisible(false);
        signInWindow.setVisible(false);
        chatWindow.setVisible(false);
        textArea.clear();
        dropTitle();
        startWindow.setVisible(true);
    }

    @FXML
    public void exitAction() throws IOException {
        System.exit(0);
    }
    @FXML
    public void goToSignUpWindow() throws IOException {
        connectToServer();
//        out.writeBoolean(true);
        startWindow.setVisible(false);
        signUpWindow.setVisible(true);
    }
    @FXML
    protected void goToSignInWindow() throws IOException {
        connectToServer();
//        out.writeBoolean(false);
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

    private void connectToServer(){
        try {
            socket = new Socket(IP_ADDRESS,PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        connectToServer();
    }

    public void startChatWorking(){
        setClientTitle();
        textField.requestFocus();
        Thread t2 =new Thread(()->{
            try{
                    while (true){
                        String msgIn = in.readUTF();
                        textArea.appendText(msgIn+"\n");
                    }
            }catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                try {
                    backToStartWindow();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        t2.start();

    }

    private void setClientTitle(){
        Client.stage.setTitle("HelloChat!: "+ nickname);
    }
    private void dropTitle(){
        Platform.runLater(()->{
            Client.stage.setTitle("HelloChat!");
        });

    }
}