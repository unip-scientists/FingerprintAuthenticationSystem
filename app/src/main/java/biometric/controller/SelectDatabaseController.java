package biometric.controller;

import java.awt.event.ActionEvent;
import javax.swing.JButton;

import biometric.fingerprint.Matcher;
import biometric.view.Setup;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectDatabaseController {
    private String currentSelectedDatabase = "";
    public String getCurrentSelectedDatabase() {
        return currentSelectedDatabase;
    }

    private Matcher matcher;

    public SelectDatabaseController(Matcher matcher) {
        this.matcher = matcher;
    }

    public boolean match(int id) throws IOException {
        // build templates, this might be in another thread so it doesn't crash the main thread.
        matcher.buildTemplates();
        return matcher.compare(0.4);
    }

    public void clickedDatabaseButton(JButton[] buttons, ActionEvent clickedButtonEvent) {
        JButton selectedButton = (JButton) clickedButtonEvent.getSource();
        currentSelectedDatabase = selectedButton.getText();

        for (JButton b : buttons) {
            if (currentSelectedDatabase.equals(b.getText())) {
                selectedButton.setOpaque(true);
                selectedButton.setBackground(Color.green);
            } else {
                b.setBackground(null);
            }
        }
    }

    public File getCandidate(int id) {
        return matcher.chooseCandidate(id);
    }

    public File getCandidate() throws SQLException {
        Statement stat = Setup.getConnection().createStatement();

        ResultSet res = stat.executeQuery("SELECT Id FROM Funcionario ORDER BY RAND() LIMIT 1");
        res.next();

        return matcher.chooseCandidate(res.getInt("Id"));
    }
}
