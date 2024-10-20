package biometric.model;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;

public class Funcionario {
    private int id;
    private String phone;
    private String username;
    private String name;
    private URL avatar;
    private String cargoName;

    public Funcionario(int id, String phone, String username, String name, String avatar, String cargoName) {
        this.id = id;
        this.phone = phone;
        this.username = username;
        this.name = name; 
        this.cargoName = cargoName;
        try {
            this.avatar = URI.create(avatar).toURL();
        } catch (MalformedURLException e) {
            /* the avatar image won't display */
            System.err.println("NONFATAL: Incorrect format of Avatar URL");
            e.printStackTrace();
        }
    }

    public String toString() {
        return cargoName + " - " + username;
    }

    public URL getAvatarURL() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }
    public int getId() {
        return id;
    }

    public String getInfo() {
        return "<html>" + name + "<br/>"
            + "TELEFONE: " + phone + "<br/>"
            + "CARGO: " + cargoName + "</html>";
    }
}
