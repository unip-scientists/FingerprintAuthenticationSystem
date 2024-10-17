package biometric.view;

import java.sql.SQLException;

import biometric.controller.SelectUserController;
import biometric.fingerprint.Matcher;

import java.io.IOException;
import javax.swing.JFrame;
import java.awt.EventQueue;

public class App extends JFrame {

    public App() {
        super();
    }

    public static void main(String[] args) {
        Setup setup = new Setup();
        App app = new App();
        app.setTitle("Fingerprint Authentication System Simulation");
        Matcher matcher = new Matcher(setup.getFingerprintsPath(), 500);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try (var conn = setup.start("jdbc:mysql://localhost:40000/app", "app", "test1234")) {
                    SelectDatabaseScreen selectDatabaseScreen = new SelectDatabaseScreen();
                    SelectUserScreen selectUserScreen = new SelectUserScreen(
                            new SelectUserController(conn, app, selectDatabaseScreen));
                    app.add(selectUserScreen);
                    app.setVisible(true);
                    app.pack();
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
