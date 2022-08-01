package client.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegController implements Initializable {
    @FXML
    public TextField loginField;
    @FXML
    public TextField nicknameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label signUpLabel;
    public String nickname;
    @FXML
    public Button signUpBtn;
    private DataInputStream in;
    private DataOutputStream out;

    @FXML
    public void signUp() throws IOException {

            in = new DataInputStream(Controller.socket.getInputStream());
            out= new DataOutputStream(Controller.socket.getOutputStream());

        if (loginField.getText().trim().length()==0||passwordField.getText().trim().length()==0){
            signUpLabel.setText("Логин и пароль не могут быть пустыми");
            signUpLabel.setVisible(true);
        }else if (loginField.getText().contains(" ")|| nicknameField.getText().contains(" ")){
            signUpLabel.setText("Логин и никнейм не должны содержать пробелов");
            signUpLabel.setVisible(true);
        }else {

            nickname = loginField.getText();
            String clientData = loginField.getText()+" "+loginField.getText()+" "+ passwordField.getText();
            out.writeUTF("R "+clientData);
            if (in.readBoolean()){
                signUpLabel.setText("Вы успешно зарегистрированы");
                signUpLabel.setTextFill(Color.GREEN);
                signUpLabel.setVisible(true);
                signUpBtn.setDisable(true);

            }else {
                signUpLabel.setText("Такой логин уже существует");
                signUpLabel.setVisible(true);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void backToStartWindow() throws IOException {
        Client.showOrCloseRegWindow();
        Controller.socket.close();
        signUpLabel.setTextFill(Color.RED);
        signUpLabel.setVisible(false);
        signUpBtn.setDisable(false);
        loginField.clear();
        nicknameField.clear();
        passwordField.clear();
    }


}
