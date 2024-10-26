package biometric.controller;

import biometric.view.App;
import biometric.view.SelectUserScreen;
import biometric.view.SelectDatabaseScreen;
import biometric.view.Setup;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.swing.JPanel;

public class TableController {

    App frame;
    SelectDatabaseScreen previousScreen;
    SelectUserScreen firstScreen;

    public void setPreviousScreen(SelectDatabaseScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    public void setFirstScreen(SelectUserScreen firstScreen) {
        this.firstScreen = firstScreen;
    }

    public void goToFirstPanel(JPanel e) {
        frame.remove(e);
        frame.add(firstScreen);
        frame.revalidate();
        frame.repaint();
    }

    public void goToPreviousPanel(JPanel e) {
        frame.remove(e);
        frame.add(previousScreen);
        frame.revalidate();
        frame.repaint();
    }

    public TableController(App frame) {
        this.frame = frame;
    }

    public Object[][] getTableData(String tableName) throws SQLException {
        Connection conn = Setup.getConnection();

        // the idea here is to create pagination but, not in the project scope
        Object[][] data = new Object[10][3];

        Statement stm = conn.createStatement();
        ResultSet r = stm.executeQuery("SELECT * FROM " + tableName + " LIMIT 10");

        int i = 0;
        while (r.next()) {
            Object[] tmp = {r.getInt("Id"), r.getString("Property"), r.getString("Region")};
            System.out.println();
            data[i++] = tmp;
        }
        return data;
    }
}
