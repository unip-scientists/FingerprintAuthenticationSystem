package biometric.view;

import java.sql.SQLException;

import biometric.controller.SelectDatabaseController;
import biometric.controller.SelectUserController;
import biometric.controller.TableController;
import biometric.fingerprint.Matcher;

import java.io.IOException;
import javax.swing.JFrame;
import java.awt.EventQueue;
import javax.swing.JOptionPane;

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

                    TableController tableController = new TableController(app);
                    SelectDatabaseController selectDatabaseController = new SelectDatabaseController(app, matcher);
                    SelectUserController selectUserController = new SelectUserController(app);

                    TableScreen tableScreen = new TableScreen(tableController);
                    SelectDatabaseScreen selectDatabaseScreen = new SelectDatabaseScreen(selectDatabaseController);
                    SelectUserScreen selectUserScreen = new SelectUserScreen(selectUserController);

                    tableController.setFirstScreen(selectUserScreen);
                    tableController.setPreviousScreen(selectDatabaseScreen);

                    selectDatabaseController.setNextPanel(tableScreen);
                    selectUserController.setNextPanel(selectDatabaseScreen);

                    app.add(selectUserScreen);
                    app.setVisible(true);
                    app.pack();

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(app, e.getMessage(), "Database Connection Failure", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(app, "The mock data is not setup correctly in the /data folder", "Invalid Mock Data", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
    }
}
