package biometric.view;

import java.sql.SQLException;

import biometric.controller.SelectDatabaseController;
import biometric.controller.SelectUserController;
import biometric.fingerprint.Matcher;

import java.io.IOException;
import javax.swing.JFrame;
import java.awt.EventQueue;

public class App extends JFrame {
    public static void main(String[] args) {
        Setup setup = new Setup();
        App app = new App();
        app.setTitle("Fingerprint Authentication System Simulation");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try (var conn = setup.start()) {
                    Matcher matcher = new Matcher(setup.getFingerprintsPath(), 500);
                    SelectDatabaseScreen selectDatabaseScreen = new SelectDatabaseScreen(
                            new SelectDatabaseController(matcher));
                    SelectUserScreen selectUserScreen = new SelectUserScreen(
                            new SelectUserController(app, selectDatabaseScreen));
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
