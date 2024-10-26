package biometric.controller;

import biometric.view.Setup;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class TableController {

    public void goToFirstPanel() {

    }

    public void goToPreviousPanel() {

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
