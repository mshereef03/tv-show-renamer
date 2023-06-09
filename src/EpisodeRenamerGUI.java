import com.apple.eawt.Application;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class EpisodeRenamerGUI extends JFrame implements ActionListener {

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

    private final JCheckBox basic;
    private final JCheckBox complex;


    private final JScrollPane scroll;
    private JTextArea outputArea;

    private JPanel panel;

    final static String apiKey = "16edf408";

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

        extensionLabel = new JLabel("Extension: (.srt)");
        extensionField = new JTextField(10);

        showNameLabel = new JLabel("Show Name: (South Park)");
        showNameField = new JTextField(20);

        seasonNumLabel = new JLabel("Season Number: (05)");
        seasonNumField = new JTextField(10);

        runButton = new JButton("Run");


        panel = new JPanel(new GridBagLayout());
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
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);

        // Add the outputArea to the GUI layout
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridheight = 5;
        constraints.fill = GridBagConstraints.BOTH;
        scroll = new JScrollPane(outputArea);
        panel.add(scroll, constraints);

        JPanel bottom = new JPanel(new GridLayout());

        basic = new JCheckBox("Nested Subs");
        complex = new JCheckBox("Nested Subs With Multiple Languages");
        bottom.add(basic);
        bottom.add(complex);

        add(panel,BorderLayout.CENTER);
        add(bottom,BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);


    }

    public static void main(String[] args) {

        EpisodeRenamerGUI gui = new EpisodeRenamerGUI();
        gui.setVisible(true);
        gui.runButton.addActionListener(gui);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String folderPath = folderField.getText();
        String extension = extensionField.getText();
        String showName = showNameField.getText();
        String seasonNum = seasonNumField.getText();
        try {
            URL url = new URL("http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + showName + "&season=" + seasonNum);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            Scanner scanner = new Scanner(url.openStream());
            String response = scanner.useDelimiter("\\Z").next();
            scanner.close();

            char status = response.substring(response.indexOf("Response\":\"") + 11).charAt(0);

            if(extension.startsWith(".")&&responseCode==200&&status=='T'&&!(basic.isSelected()&&complex.isSelected())) {

                repaint();
                outputArea.update(outputArea.getGraphics());
                EpisodeRenamer.renameEpisodes(folderPath, extension, showName, seasonNum,scroll, outputArea,basic.isSelected(),complex.isSelected());
                JOptionPane.showMessageDialog(new EpisodeRenamerGUI(), "Renaming Completed !");
            }
            else{
                repaint();
                outputArea.append("Failure.");
                outputArea.update(outputArea.getGraphics());
                if(!extension.startsWith("."))JOptionPane.showMessageDialog(new EpisodeRenamerGUI(), "Invalid Extension, e.g. '.mkv'");
                else if(basic.isSelected()&&complex.isSelected()) JOptionPane.showMessageDialog(new EpisodeRenamerGUI(), "Can't select both types of subs !");
                else JOptionPane.showMessageDialog(new EpisodeRenamerGUI(), "Couldn't find show or API Failure");
            }
        } catch (InterruptedException |IOException ex) {
            ex.printStackTrace();
        }

    }





}