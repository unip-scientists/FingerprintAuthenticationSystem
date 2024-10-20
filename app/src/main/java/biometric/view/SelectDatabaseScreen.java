package biometric.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
                    controller.clickedDatabaseButton(buttons, e);
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

                /* this button has 2 roles, although not considered a good aproach:
                 * 1. It choose the identity and type of the fingerprint
                 * 2. It sets up and matches the chosen candidate with the probe on the other end
                 */ 

                if (scannerButton.getText().equals("Confirmar Identidade")) {
                    try {
                        boolean isSimilar = controller.match(funcionario.getId());

                        if (isSimilar) {
                            // go to third panel to show database
                            System.out.println("You are authenticated!");
                        } else {
                            // show not authorized message
                            System.out.println("You are an IMPOSTOR!");
                        }
                    } catch (IOException e1) {
                        System.out.println("FATAL: can't compare fingerprints | can't confirm identity");
                        e1.printStackTrace();
                    }
                    return;
                }

                if (controller.getCurrentSelectedDatabase().isEmpty()) {
                    // ERROR NONFATAL: the user must select one of the three databases
                    System.out.println("MUST SELECT DATABASE");
                    return;
                }

                // check if database selected applies to job role

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
                        e1.printStackTrace();
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
                        System.out.println("FATAL ERROR IO/SQL");
                        e1.printStackTrace();
                    }

                } else {
                    // NONFATAL: error, can't choose this option.
                    System.out.println("ERROR essa opcao nao existe");
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
