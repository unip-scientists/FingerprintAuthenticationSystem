package biometric.controller;

import biometric.model.Funcionario;
import biometric.view.App;
import biometric.view.SelectDatabaseScreen;
import biometric.view.Setup;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JPanel;

public class SelectUserController {
    SelectDatabaseScreen nextPanel;
    App frame;

    public SelectUserController(App frame, SelectDatabaseScreen nextPanel) {
        this.nextPanel = nextPanel;
        this.frame = frame;
    }

    public ArrayList<Funcionario> retrieveFuncionarios() throws SQLException {
        Statement statement = Setup.getConnection().createStatement();
        ArrayList<Funcionario> f = new ArrayList<>();
        ResultSet result = statement.executeQuery(
                "SELECT  Funcionario.Id, Username, Funcionario.Name,"
                        + "Phone, Avatar, Cargo.Name as CargoName "
                        + "FROM Funcionario "
                        + "INNER JOIN "
                        + "Cargo ON Funcionario.CargoId = Cargo.Id");

        while (result.next()) {
            f.add(new Funcionario(
                    result.getInt("Id"),
                    result.getString("Phone"),
                    result.getString("Username"),
                    result.getString("Name"),
                    result.getString("Avatar"),
                    result.getString("CargoName")));
        }

        return f;
    }

    public void goToNextPanel(JPanel e, Funcionario selectedFuncionario) {
        nextPanel.setFuncionario(selectedFuncionario);
        frame.remove(e);
        frame.add(nextPanel);
        frame.revalidate();
        frame.repaint();
    }
}
