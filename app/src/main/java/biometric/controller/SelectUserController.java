package biometric.controller;

import biometric.model.Funcionario;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JFrame;

public class SelectUserController {

    Connection db;
    JPanel nextPanel;
    JFrame frame;

    public SelectUserController(Connection db, JFrame frame, JPanel nextPanel) {
        this.db = db;
        this.nextPanel = nextPanel;
        this.frame = frame;
    }

    public ArrayList<Funcionario> retrieveFuncionarios() throws SQLException {
        Statement statement = db.createStatement();
        ArrayList<Funcionario> f = new ArrayList<>();
        ResultSet result = statement.executeQuery(
            "SELECT  Funcionario.Id, Username, Funcionario.Name,"
                + "Phone, Avatar, Cargo.Name as CargoName "
                + "FROM Funcionario "
                + "INNER JOIN "
                + "Cargo ON Funcionario.CargoId = Cargo.Id"
        );

        while (result.next()) {
            f.add(new Funcionario(
                result.getInt("Id"),
                result.getString("Phone"),
                result.getString("Username"),
                result.getString("Name"),
                result.getString("Avatar"),
                result.getString("CargoName")
            ));
        }

        return f;
    }

    public void goToNextPanel(JPanel e) {
        frame.remove(e);
        frame.add(nextPanel);
        frame.revalidate();
        frame.repaint();
    }
}
