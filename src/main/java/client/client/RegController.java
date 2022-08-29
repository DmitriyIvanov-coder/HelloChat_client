package client.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RegController implements Initializable {
    @FXML
    public TextField loginField;
    @FXML
    public TextField changeLoginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label signUpLabel;
    public String newLogin;
    @FXML
    public Button signUpBtn;
//    @FXML
//    public CheckBox checkBoxForChangeLogin;
    @FXML
    public RadioButton regRadioBtn;
    @FXML
    public RadioButton changeLoginRadioBtn;
    private static DataInputStream in;
    private static DataOutputStream out;

    @FXML
    public void signUp() throws IOException {

            in = new DataInputStream(Controller.socket.getInputStream());
            out= new DataOutputStream(Controller.socket.getOutputStream());
            if (changeLoginRadioBtn.isSelected()){
                if (checkEnteredData()){
                    newLogin = changeLoginField.getText();
                    System.out.println(changeLoginField.getText());
                    String clientData = loginField.getText()+" "+passwordField.getText()+" "+changeLoginField.getText() ;
                    out.writeUTF("Ch "+clientData);
                    if (in.readInt()==0){
                        signUpLabel.setText("Логин изменён успешно");
                        signUpLabel.setTextFill(Color.GREEN);
                        signUpLabel.setVisible(true);
//                        signUpBtn.setDisable(true);
                    }else if(in.readInt()==1) {
                        signUpLabel.setText("Неправильный логин или пароль");
                        signUpLabel.setVisible(true);
                    } else if (in.readInt()==2) {
                        signUpLabel.setText("Такой новый логин уже существует");
                        signUpLabel.setVisible(true);
                    }
                }
            }else {
                if (checkEnteredData()){
//                    nickname = loginField.getText();
                    String clientData = loginField.getText()+" "+loginField.getText()+" "+ passwordField.getText();
                    out.writeUTF("R "+clientData);
                    if (in.readBoolean()){
                        signUpLabel.setText("Вы успешно зарегистрированы");
                        signUpLabel.setTextFill(Color.GREEN);
                        signUpLabel.setVisible(true);
//                        signUpBtn.setDisable(true);

                    }else {
                        signUpLabel.setTextFill(Color.RED);
                        signUpLabel.setText("Такой логин уже существует");
                        signUpLabel.setVisible(true);
                    }
                }
            }


    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private boolean checkEnteredData() {
        if (changeLoginRadioBtn.isSelected()){
            if (loginField.getText().trim().length() == 0 || passwordField.getText().trim().length() == 0
            || changeLoginField.getText().trim().length()==0) {
                signUpLabel.setText("Логин и пароль не могут быть пустыми");
                signUpLabel.setVisible(true);
                return false;
            } else if (loginField.getText().contains(" ") || changeLoginField.getText().contains(" ")
            || changeLoginField.getText().contains(" ")) {
                signUpLabel.setText("Логин не должен содержать пробелы");
                signUpLabel.setVisible(true);
                return false;
            }else if (changeLoginField.getText().equals(loginField.getText())){
                signUpLabel.setText("Новый логин не должен совпадать со старым");
                signUpLabel.setVisible(true);
                return false;
            }
        }else {
            if (loginField.getText().trim().length() == 0 || passwordField.getText().trim().length() == 0) {
                signUpLabel.setText("Логин и пароль не могут быть пустыми");
                signUpLabel.setVisible(true);
                return false;
            } else if (loginField.getText().contains(" ") || changeLoginField.getText().contains(" ")) {
                signUpLabel.setText("Логин не должен содержать пробелы");
                signUpLabel.setVisible(true);
                return false;
            }
        }
        return true;
    }

    public void backToStartWindow() throws IOException {
        out= new DataOutputStream(Controller.socket.getOutputStream());
        out.writeUTF("exit");
        Client.showOrCloseRegWindow();
        Controller.socket.close();
        signUpLabel.setTextFill(Color.RED);
        signUpLabel.setVisible(false);
        signUpBtn.setDisable(false);
        loginField.clear();
        changeLoginField.clear();
        passwordField.clear();
    }


    public void changeMode(ActionEvent actionEvent) {
        if(changeLoginRadioBtn.isSelected()){
            signUpLabel.setVisible(false);
            changeLoginField.setDisable(false);
            signUpBtn.setDisable(false);
        }else{
            signUpLabel.setVisible(false);
            changeLoginField.clear();
            changeLoginField.setDisable(true);
        }
    }
}
