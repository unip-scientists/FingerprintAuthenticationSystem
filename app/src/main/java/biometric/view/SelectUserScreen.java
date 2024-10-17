package biometric.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;

import java.util.ArrayList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.Box;
import java.awt.Component;
import java.awt.Dimension;

import biometric.controller.SelectUserController;
import biometric.model.Funcionario;

public class SelectUserScreen extends JPanel {

    SelectUserController controller;
    JLabel avatarImage = new JLabel();
    JButton continueButton = new JButton();
    JComboBox<Funcionario> dropdownList = new JComboBox<>();

    public SelectUserScreen(SelectUserController controller) {
        super();
        this.controller = controller;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        continueButton.setText("Continuar");

        /* center components */
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropdownList.setAlignmentX(Component.CENTER_ALIGNMENT);

        ArrayList<Funcionario> funcionarios;

        try {
            funcionarios = controller.retrieveFuncionarios();

            for (Funcionario f : funcionarios) {
                dropdownList.addItem(f);
            }

            /* first item is selected by default */
            avatarImage.setIcon(new ImageIcon(funcionarios.get(0).getAvatarURL()));

            dropdownList.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        Funcionario f = (Funcionario) e.getItem();
                        avatarImage.setIcon(new ImageIcon(f.getAvatarURL()));
                    }
                }
            });

            continueButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.goToNextPanel(SelectUserScreen.this);
                }
            });


            add(avatarImage);
            add(dropdownList);
            add(Box.createRigidArea(new Dimension(0, 10)));
            add(continueButton);
            add(Box.createRigidArea(new Dimension(0, 10)));

        } catch (SQLException e) {
            /* FATAL: without employees the program can't function */
            e.printStackTrace();
        }
    }
}
