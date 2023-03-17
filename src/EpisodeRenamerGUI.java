import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class EpisodeRenamerGUI extends JFrame {

    private final JLabel folderLabel;
    private final JTextField folderField;
    private final JButton folderButton;

    private final JLabel extensionLabel;
    private final JTextField extensionField;

    private final JLabel showNameLabel;
    private final JTextField showNameField;

    private final JLabel seasonNumLabel;
    private final JTextField seasonNumField;

    private final JButton runButton;

    public EpisodeRenamerGUI() {
        super("Episode Renamer");

        folderLabel = new JLabel("Folder:");
        folderField = new JTextField(20);
        folderField.setEditable(true);
        folderButton = new JButton("Select Folder");
        folderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("/Users/mshereef/Desktop"));
                chooser.setDialogTitle("Select Folder");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(EpisodeRenamerGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    folderField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        extensionLabel = new JLabel("Extension:");
        extensionField = new JTextField(10);

        showNameLabel = new JLabel("Show Name:");
        showNameField = new JTextField(20);

        seasonNumLabel = new JLabel("Season Number:");
        seasonNumField = new JTextField(10);

        runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String folderPath = folderField.getText();
                String extension = extensionField.getText();
                String showName = showNameField.getText();
                String seasonNum = seasonNumField.getText();
                try {
                    EpisodeRenamer.renameEpisodes(folderPath, extension, showName, seasonNum);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.insets = new Insets(5, 5, 5, 5);
        panel.add(folderLabel, constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(folderField, constraints);
        constraints.gridx = 2;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(folderButton, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(extensionLabel, constraints);
        constraints.gridx = 1;
        panel.add(extensionField, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(showNameLabel, constraints);
        constraints.gridx = 1;
        panel.add(showNameField, constraints);
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(seasonNumLabel, constraints);
        constraints.gridx = 1;
        panel.add(seasonNumField, constraints);
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(runButton, constraints);

        // Create a new JTextArea component to display the output
        JTextArea outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);

        // Create a new PrintStream that redirects the standard output stream to the JTextArea
        PrintStream printStream = new PrintStream(new CustomOutputStream(outputArea));

        // Redirect the standard output stream to the PrintStream
        System.setOut(printStream);
        System.setErr(printStream);

        // Add the outputArea to the GUI layout
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridheight = 5;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(outputArea), constraints);

        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EpisodeRenamerGUI gui = new EpisodeRenamerGUI();
                gui.setVisible(true);
                gui.runButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String folderPath = gui.folderField.getText();
                        String extension = gui.extensionField.getText();
                        String showName = gui.showNameField.getText();
                        String seasonNum = gui.seasonNumField.getText();
                        try {
                            EpisodeRenamer.renameEpisodes(folderPath, extension, showName, seasonNum);
                            JOptionPane.showMessageDialog(new EpisodeRenamerGUI(), "Renaming Completed !");
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private static class CustomOutputStream extends OutputStream {
        private final JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            // Append the character to the JTextArea
            textArea.append(String.valueOf((char) b));
            // Scroll to the bottom of the JTextArea
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

}