package client.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    static Socket socket;
    private final int PORT = 8189;

    private final String IP_ADDRESS = "localhost";

    private DataInputStream in;
    private DataOutputStream out;

    static List<String> onlineClientsNicks = new ArrayList<>();

    static WriterHistory writerHistory = new WriterHistory();
    @FXML
    public  TextField textField;
    @FXML
    public  Label signUpLabel;
//    @FXML
//    public TextArea textAreaForOnlineClients;
    public  String nickname;
    @FXML
    public  TextArea textAreaForMessages;
    @FXML
    public  HBox startWindow;
    @FXML
    public  Pane signInWindow;
    @FXML
    public  Pane signUpWindow;
    @FXML
    public TextField loginField;
    @FXML
    public TextField nicknameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public  VBox chatWindow;
    @FXML
    public TextField loginIn;
    @FXML
    public PasswordField passwordIn;
    @FXML
    public  Label signInLabel;
    @FXML
    public  ListView listViewForOnlineClients;

    @FXML
    public void signIn() throws IOException {

        if (loginIn.getText().contains(" ")){
            signInLabel.setText("Логин и никнейм не должны содержать пробелов");
            signInLabel.setVisible(true);
        }else {
            String clientData = loginIn.getText() + " " + passwordIn.getText();
            out.writeUTF("SI "+clientData);
            String isNext = in.readUTF();
            if (isNext.equals("0")) {
                signInLabel.setVisible(false);
                signInWindow.setVisible(false);
                writerHistory.loadHistory(textAreaForMessages);
                chatWindow.setVisible(true);
                nickname = in.readUTF();
                
                startChatWorking();
            } else if (isNext.equals("1")){
                signInLabel.setText("Неправильный логин или пароль");
                signInLabel.setVisible(true);
            }else if (isNext.equals("2")){
                signInLabel.setText("Клиент уже онлайн");
                signInLabel.setVisible(true);
            }
        }
    }

//    @FXML
//    public void signUp() throws IOException {
//        if (loginField.getText().trim().length()==0||passwordField.getText().trim().length()==0){
//            signUpLabel.setText("Логин и пароль не могут быть пустыми");
//            signUpLabel.setVisible(true);
//        }else if (loginField.getText().contains(" ")|| nicknameField.getText().contains(" ")){
//            signUpLabel.setText("Логин и никнейм не должны содержать пробелов");
//            signUpLabel.setVisible(true);
//        }else {
//
//            nickname = loginField.getText();
//            String clientData = loginField.getText()+" "+loginField.getText()+" "+ passwordField.getText();
//            out.writeUTF("R "+clientData);
//            if (in.readBoolean()){
//                signUpLabel.setVisible(false);
//                signUpWindow.setVisible(false);
//                Client.showOrCloseRegWindow();
//                chatWindow.setVisible(true);
//                startChatWorking();
//            }else {
//                signUpLabel.setText("Такой логин уже существует");
//                signUpLabel.setVisible(true);
//            }
//        }
//    }

    @FXML
    public void backToStartWindow() throws IOException {
        socket.close();
//        out.writeUTF("0");
        signInLabel.setVisible(false);
        signUpLabel.setVisible(false);
        signUpWindow.setVisible(false);
        signInWindow.setVisible(false);
        chatWindow.setVisible(false);
        textAreaForMessages.clear();
        dropTitle();
        startWindow.setVisible(true);
    }

    @FXML
    public void exitAction() throws IOException {
//        writerHistory.writeMessagesHistory(textAreaForMessages.getText());
        System.exit(0);
    }
    @FXML
    public void goToSignUpWindow() throws IOException {
        connectToServer();
        Client.showOrCloseRegWindow();
    }
    @FXML
    protected void goToSignInWindow() throws IOException {
        connectToServer();
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

    private void  connectToServer(){
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

    }

    public void startChatWorking(){
        setClientTitle();
        textField.requestFocus();
        Thread t2 =new Thread(()->{
            try{
                out.writeUTF("//ready");
                    while (true){
                        String msgIn = in.readUTF();
                        if (msgIn.equals("//")){
                            InputStream inputStream = socket.getInputStream();
                            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                            onlineClientsNicks = (List<String>) objectInputStream.readObject();
                            listViewForOnlineClients.getItems().clear();
                            for (String n:onlineClientsNicks) {
                                listViewForOnlineClients.getItems().add(n);
                            }
                        }else {
                            textAreaForMessages.appendText(msgIn+"\n");
                            writerHistory.writeMessagesHistory(msgIn+"\n");
                        }
                    }
            }catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
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
        Client.stageMain.setTitle("HelloChat!: "+ nickname);
    }
    private static void dropTitle(){
        Platform.runLater(()->{
            Client.stageMain.setTitle("HelloChat!");
        });

    }

    public void sendTo(MouseEvent mouseEvent){
        textField.clear();
        textField.appendText("//wto "+listViewForOnlineClients.getSelectionModel().getSelectedItem()+" ");
        textField.requestFocus();
    }

}