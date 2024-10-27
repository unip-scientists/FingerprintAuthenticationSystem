package biometric.controller;

import java.awt.event.ActionEvent;
import javax.swing.JButton;

import biometric.fingerprint.Matcher;
import biometric.view.App;
import biometric.view.Setup;
import biometric.view.TableScreen;

import java.awt.Color;
import javax.swing.JPanel;
import java.io.File;
import java.io.IOException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectDatabaseController {
    private String currentSelectedDatabase = "";
    public String getCurrentSelectedDatabase() {
        if (currentSelectedDatabase.equals("")) return "";

        if (currentSelectedDatabase.equals("Rios")) return "Rio";
        else if (currentSelectedDatabase.equals("Mares")) return "Oceano";
        return "Lencol";
    }

    private Matcher matcher;
    private TableScreen nextPanel;
    private App frame;

    public void setNextPanel(TableScreen nextPanel) {
        this.nextPanel = nextPanel;
    }


    public SelectDatabaseController(App frame, Matcher matcher) {
        this.matcher = matcher;
        this.frame = frame;
    }

    public boolean match() throws IOException {
        // build templates, this might be in another thread so it doesn't crash the main thread.
        matcher.buildTemplates();
        return matcher.compare(40);
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

    public File authorizedMatcher(int id) {
        // candidate + default probe
        return matcher.chooseCandidate(id);
    }

    public File forbadeMatcher(int id) throws SQLException {
        String query = "SELECT Id FROM Funcionario WHERE Id != ? ORDER BY RAND() LIMIT 1";
    
        try (PreparedStatement stmt = Setup.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id); // Define o valor para o parâmetro
    
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                int candidateId = res.getInt("Id");
    
                File candidate = matcher.chooseCandidate(candidateId);
                matcher.chooseProbe(id);
    
                return candidate;
            } else {
                throw new SQLException("Nenhum candidato encontrado.");
            }
        }
    }

    public void goToNextPanel(JPanel e, String tableName) throws SQLException {
        nextPanel.createJTable(tableName);
        frame.remove(e);
        frame.add(nextPanel);
        frame.revalidate();
        frame.repaint();

    }
}
