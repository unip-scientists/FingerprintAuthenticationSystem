package biometric.view;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import biometric.controller.TableController;

public class TableScreen extends JPanel {
    private JScrollPane scrollPane;
    private TableController controller;
    JPanel panel;

    public TableScreen(TableController controller) {
        this.controller = controller;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        JButton goBack = new JButton("Voltar");
        JButton restart = new JButton("Reiniciar Simulação");

        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.goToPreviousPanel(TableScreen.this);
            }
        });

        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.goToFirstPanel(TableScreen.this);
            }
        });

        panel.add(goBack);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(restart);

    }

    public void createJTable(String tableName) throws SQLException {
        String[] columnNames = { "Id", "Propriedade", "Regiāo" };
        JTable table = new JTable(controller.getTableData(tableName), columnNames);

        if (scrollPane == null) {
            scrollPane = new JScrollPane(table);

            add(scrollPane);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(panel);
            add(Box.createRigidArea(new Dimension(0, 5)));
            return;
        }

        scrollPane.setViewportView(table);
    }
}
