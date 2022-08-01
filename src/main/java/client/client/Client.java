package client.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Client extends Application {
    static FXMLLoader fxmlLoaderMain = new FXMLLoader(Client.class.getResource("mainWindow.fxml"));

    static FXMLLoader fxmlLoaderReg = new FXMLLoader(Client.class.getResource("regWindow.fxml"));
    public static Stage stageMain;
    public static Stage stageReg ;
    @Override
    public void start(Stage stage) {
        this.stageMain = stage;
        stageReg = new Stage();
        try {
            loadMainWindow();
            loadRegWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void loadMainWindow() throws IOException {
        Scene sceneMain = new Scene(fxmlLoaderMain.load(), 320, 240);
        stageMain.setTitle("HelloChat!");
        stageMain.setScene(sceneMain);
        stageMain.setResizable(false);
        stageMain.show();
        stageMain.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }
    public static void showOrCloseRegWindow(){
        if (stageReg.isShowing()){
//            stageMain.show();
            stageReg.close();
        }else {

            stageReg.showAndWait();
//            stageReg.show();
        }
    }

    public void loadRegWindow() throws IOException {
        Scene scene = new Scene(fxmlLoaderReg.load(), 320, 240);
        stageReg.setTitle("Registration on HelloChat!");
        stageReg.setScene(scene);
        stageReg.setResizable(false);
        stageReg.initOwner(stageMain);
        stageReg.initModality(Modality.APPLICATION_MODAL);
        stageReg.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stageReg.close();
                try {
                    Controller.socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}