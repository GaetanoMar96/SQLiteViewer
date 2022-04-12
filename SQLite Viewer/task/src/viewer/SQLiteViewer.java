package viewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SQLiteViewer extends JFrame {

    public SQLiteViewer() {
        super("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        initComponents();


        setVisible(true);
    }

    private void initComponents() {

        JTextField fileNameTextField  = new JTextField();
        fileNameTextField.setName("FileNameTextField");
        fileNameTextField.setBounds(20,20, 750,30);
        add(fileNameTextField);

        JButton openFileButton = new JButton("Open");
        openFileButton.setName("OpenFileButton");
        openFileButton.setBounds(780,20,80,30);
        add(openFileButton);

        JComboBox<String> tablesComboBox = new JComboBox<>();
        tablesComboBox.setName("TablesComboBox");
        tablesComboBox.setBounds(20,60, 850,30);
        add(tablesComboBox);

        JTextArea queryTextArea = new JTextArea();
        queryTextArea.setName("QueryTextArea");
        queryTextArea.setBounds(20,100, 700,120);
        add(queryTextArea);

        JButton executeQueryButton = new JButton("Execute");
        executeQueryButton.setName("ExecuteQueryButton");
        executeQueryButton.setBounds(740,100,100,50);
        add(executeQueryButton);

        JTable table = new JTable();
        table.setName("Table");
        table.setBounds(20, 250, 850, 200);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        add(tableScrollPane, BorderLayout.CENTER);

        queryTextArea.setEnabled(false);
        executeQueryButton.setEnabled(false);

        openFileButton.addActionListener(event -> {
            if (fileNameTextField.getText().equals("firstDatabase.db") ||
                    fileNameTextField.getText().equals("secondDatabase.db")) {
                tablesComboBox.setEnabled(true);
                queryTextArea.setEnabled(true);
                executeQueryButton.setEnabled(true);

                try (JDBConn database = new JDBConn(fileNameTextField.getText())) {
                tablesComboBox.removeAllItems();
                database.getTables().forEach(tablesComboBox::addItem);
                queryTextArea.setText(String.format(JDBConn.ALL_ROWS_QUERY, tablesComboBox.getSelectedItem()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
                JOptionPane.showMessageDialog(new Frame(), "File doesn't exist!");
                executeQueryButton.setEnabled(false);
                queryTextArea.setEnabled(false);
            }
        });

        tablesComboBox.addItemListener(event -> queryTextArea.setText(
                String.format(JDBConn.ALL_ROWS_QUERY,event.getItem().toString())));

        executeQueryButton.addActionListener(event ->
        { try (JDBConn database = new JDBConn(fileNameTextField.getText())) {
            TableModelClass tableModel = database.getData(
                    queryTextArea.getText());
            table.setModel(tableModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        });
    }

}
