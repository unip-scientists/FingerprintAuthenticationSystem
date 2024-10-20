package biometric.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Setup {
    private Connection conn;
    Path tablesPath;
    Path fingerprintsPath;

    public Path getFingerprintsPath() {
        return fingerprintsPath;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:40000/app", "app", "test1234");
    }

    Connection start() throws IOException, SQLException {
        this.conn = getConnection();
        String basePath = new File("").getAbsolutePath();
        tablesPath = Path.of(basePath, "/src/main/java/biometric/data");
        fingerprintsPath = Path.of(basePath, "/src/main/java/biometric/data/fingerprint");
        System.out.println(fingerprintsPath);

        try {
            Rio();
            Oceano();
            Lencol();

            Cargo();
            Funcionario();
        } catch (SQLException e) {
            System.out.println("Database had been already set up. Ignoring....");
        }

        return conn;
    }

    private List<String[]> readCSV(Path path) throws IOException {
        ArrayList<String[]> data = new ArrayList<>();

        var lines = Files.lines(path).toList();

        for (String line : lines) {
            data.add(line.split(","));
        }

        return data;
    }

    private void Rio() throws IOException, SQLException {
        // the rio dataset is missing, add it
        List<String[]> csv = readCSV(tablesPath.resolve("Rio.csv"));

        conn.createStatement().executeUpdate(
                "CREATE TABLE Rio ("
                        + "Id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                        + "Property varchar(255),"
                        + "Region varchar(255))");

        var sql = conn.prepareStatement("INSERT INTO Rio (Id, Property, Region) VALUES (?, ?, ?)");
        for (String[] row : csv) {
            sql.setInt(1, Integer.parseInt(row[0]));
            sql.setString(2, row[1]);
            sql.setString(3, row[2]);
            sql.addBatch();
        }

        sql.executeBatch();
    }

    private void Oceano() throws IOException, SQLException {
        List<String[]> csv = readCSV(tablesPath.resolve("Oceano.csv"));

        conn.createStatement().executeUpdate(
                "CREATE TABLE Oceano ("
                        + "Id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                        + "Property varchar(255),"
                        + "Region varchar(255))");

        var sql = conn.prepareStatement("INSERT INTO Oceano (Id, Property, Region) VALUES (?, ?, ?)");
        for (String[] row : csv) {
            sql.setInt(1, Integer.parseInt(row[0]));
            sql.setString(2, row[1]);
            sql.setString(3, row[2]);
            sql.addBatch();
        }

        sql.executeBatch();
    }

    private void Lencol() throws IOException, SQLException {
        List<String[]> csv = readCSV(tablesPath.resolve("Lencol.csv"));

        conn.createStatement().executeUpdate(
                "CREATE TABLE Lencol ("
                        + "Id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                        + "Property varchar(255),"
                        + "Region varchar(255))");

        var sql = conn.prepareStatement("INSERT INTO Lencol (Id, Property, Region) VALUES (?, ?, ?)");
        for (String[] row : csv) {
            sql.setInt(1, Integer.parseInt(row[0]));
            sql.setString(2, row[1]);
            sql.setString(3, row[2]);
            sql.addBatch();
        }

        sql.executeBatch();
    }

    private void Cargo() throws IOException, SQLException {
        List<String[]> csv = readCSV(tablesPath.resolve("Cargo.csv"));

        conn.createStatement().executeUpdate(
                "CREATE TABLE Cargo ("
                        + "Id INTEGER PRIMARY KEY,"
                        + "Name varchar(255))");

        var sql = conn.prepareStatement("INSERT INTO Cargo (Id, Name) VALUES (?, ?)");
        for (String[] row : csv) {
            sql.setInt(1, Integer.parseInt(row[0]));
            sql.setString(2, row[1]);
            sql.addBatch();
        }

        sql.executeBatch();
    }

    private void Funcionario() throws IOException, SQLException {
        List<String[]> csv = readCSV(tablesPath.resolve("Funcionario.csv"));

        conn.createStatement().executeUpdate(
            "CREATE TABLE Funcionario ("
                + "Id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "Username varchar(255),"
                + "Name varchar(255),"
                + "Phone varchar(255),"
                + "Avatar varchar(255),"
                + "CargoId INTEGER,"
                + "FOREIGN KEY (CargoId) REFERENCES Cargo(Id))"
        );

        var sql = conn.prepareStatement(
            "INSERT INTO Funcionario"
                + "(Id, Username, Name, Phone, CargoId, Avatar)"
                + "VALUES (?, ?, ?, ?, ?, ?)"
            );

        for (String[] row : csv) {
            sql.setInt(1, Integer.parseInt(row[0]));
            sql.setString(2, row[1]);
            sql.setString(3, row[2]);
            sql.setString(4, row[3]);
            sql.setInt(5, Integer.parseInt(row[4]));
            sql.setString(6, row[5]);
            sql.addBatch();
        }

        sql.executeBatch();
    }
}
