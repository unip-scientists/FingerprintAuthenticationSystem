package biometric.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import javax.swing.Box;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.io.File;
import java.awt.Image;
import java.awt.Component;
import javax.swing.JComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import biometric.controller.SelectDatabaseController;
import biometric.model.Funcionario;

public class SelectDatabaseScreen extends JPanel {

    private Funcionario funcionario;
    private File scannedFingerprint;
    private JLabel avatarImage;
    private JLabel usernameLabel;
    private JLabel funcionarioInfo;
    private JLabel fingerprintImage;
    private JLabel statusLabel;
    private String db;

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        BufferedImage img;

        try {
            img = ImageIO.read(funcionario.getAvatarURL());
            avatarImage.setIcon(
                    new ImageIcon(img.getScaledInstance(avatarImage.getWidth(), avatarImage.getHeight(),
                            Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            /* NONFATAL: profile photo is not displayed */
            e.printStackTrace();
        }
        usernameLabel.setText(funcionario.getUsername());
        funcionarioInfo.setText(funcionario.getInfo());
    }

    public SelectDatabaseScreen(SelectDatabaseController controller) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        /* top - Funcionario information */
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));

        JPanel leftInfoPanel = new JPanel();
        leftInfoPanel.setLayout(new BoxLayout(leftInfoPanel, BoxLayout.PAGE_AXIS));

        avatarImage = new JLabel();
        avatarImage.setSize(75, 75);
        usernameLabel = new JLabel();
        funcionarioInfo = new JLabel();

        avatarImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftInfoPanel.add(avatarImage);
        leftInfoPanel.add(usernameLabel);

        topPanel.add(leftInfoPanel);
        topPanel.add(funcionarioInfo);

        /* middle - button to select databaes */
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.LINE_AXIS));

        JButton dbRios = new JButton("Rios");
        JButton dbMares = new JButton("Mares");
        JButton dbLencol = new JButton("Lencol");

        /* for action listener */
        JButton[] buttons = { dbRios, dbMares, dbLencol };
        for (JButton b : buttons) {
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String db = controller.clickedDatabaseButton(buttons, e);
                    SelectDatabaseScreen.this.db = db;
                }
            });
        }

        middlePanel.add(dbRios);
        middlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        middlePanel.add(dbMares);
        middlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        middlePanel.add(dbLencol);

        /* bottom - fingerprint interface simulation */
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));

        fingerprintImage = new JLabel();

        JComboBox<String> dropdownList = new JComboBox<>();
        dropdownList.setPreferredSize(new Dimension(175, 20));
        dropdownList.setMaximumSize(new Dimension(175, 20));
        dropdownList.addItem("Tipo de Autorização");
        dropdownList.addItem("Digital autorizada");
        dropdownList.addItem("Digital NÃO autorizada");

        JButton scannerButton = new JButton("obter impressão digital");

        statusLabel = new JLabel("Esperando scanner");

        bottomPanel.add(dropdownList);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        bottomPanel.add(scannerButton);
        bottomPanel.add(statusLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(fingerprintImage);

        dropdownList.setAlignmentX(Component.CENTER_ALIGNMENT);
        scannerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fingerprintImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        scannerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                /*
                 * this button has 2 roles, although not considered a good aproach:
                 * 1. It choose the identity and type of the fingerprint
                 * 2. It sets up and matches the chosen candidate with the probe on the other
                 * end
                 */

                String c = funcionario.getCargoName();

                if (db.equals("Lencol") && !c.equals("Ministro")
                    || db.equals("Mares") && c.equals("Funcionario")) {

                    // NONFATAL
                    JOptionPane.showMessageDialog(SelectDatabaseScreen.this, "You do not have access to this source.", "Forbidden", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (scannerButton.getText().equals("Confirmar Identidade")) {
                    try {
                        boolean isSimilar = controller.match(funcionario.getId());

                        if (isSimilar) {
                            controller.goToNextPanel(SelectDatabaseScreen.this,controller.getCurrentSelectedDatabase());
                        } else {
                            JOptionPane.showMessageDialog(SelectDatabaseScreen.this, "Your identity is not confirmed.", "Not Authorized", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(SelectDatabaseScreen.this, "Fingerprint confirmation FAILED", "Fingerprint Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("FATAL: can't compare fingerprints | can't confirm identity");
                        e1.printStackTrace();
                    } catch (SQLException e2) {
                        // FATAL: without being able to access database, the authentication must fail
                        JOptionPane.showMessageDialog(SelectDatabaseScreen.this, "Database source could not be fetched.", "Querry Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("the database couldn't fetch resource or is unavailable | can't access database");
                    }
                    return;
                }

                if (controller.getCurrentSelectedDatabase().isEmpty()) {
                    // ERROR NONFATAL: the user must select one of the three databases
                    JOptionPane.showMessageDialog(SelectDatabaseScreen.this, "You have to select one of the databases.", "Options", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (((String) dropdownList.getSelectedItem()).equals("Digital autorizada")) {
                    try {
                        scannedFingerprint = controller.getCandidate(funcionario.getId());
                        BufferedImage img = ImageIO.read(scannedFingerprint);
                        fingerprintImage.setSize(100, 100);
                        fingerprintImage.setIcon(new ImageIcon(
                                img.getScaledInstance(fingerprintImage.getWidth(), fingerprintImage.getHeight(),
                                        Image.SCALE_SMOOTH)));
                    } catch (IOException e1) {
                        // FATAL: there is no matching if the candidate is missing.
                        JOptionPane.showMessageDialog(SelectDatabaseScreen.this, "Fingerprint candidate can't be scanned.", "Scanner Error", JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                        return;
                    }

                } else if (((String) dropdownList.getSelectedItem()).equals("Digital NÃO autorizada")) {
                    try {
                        scannedFingerprint = controller.getCandidate();
                        BufferedImage img = ImageIO.read(scannedFingerprint);
                        fingerprintImage.setSize(100, 100);
                        fingerprintImage.setIcon(new ImageIcon(
                                img.getScaledInstance(fingerprintImage.getWidth(), fingerprintImage.getHeight(),
                                        Image.SCALE_SMOOTH)));
                    } catch (IOException | SQLException e1) {
                        // FATAL: IO - problem with reading image, can't continue the program
                        // FATAL: SQL - can't access the database can't create fingerprint template
                        JOptionPane.showMessageDialog(SelectDatabaseScreen.this, "Fingerprint Authentication Error", "Scanner Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("FATAL ERROR IO/SQL");
                        e1.printStackTrace();
                    }

                } else {
                    // NONFATAL: error, can't choose this option.
                    JOptionPane.showMessageDialog(SelectDatabaseScreen.this, "The option selected is invalid.", "Options", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                scannerButton.setText("Confirmar Identidade");
                statusLabel.setText("Esperando autenticação");
            }
        });

        add(topPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(middlePanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(bottomPanel);

    }
}
