package gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class InfoGUI extends JFrame implements ChangeListener {

    private JPanel titlePanel;
    private JToggleButton toggleButton;
    private JLabel titleLable;
    private JPanel progressPanel;
    private JLabel barLable;
    private JLabel progressLable;

    public InfoGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addComponents(this.getContentPane());

        this.setSize(300, 100);
        this.setVisible(true);
    }

    private void addComponents(Container pane) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        pane.add(Box.createVerticalGlue());

        titleLable = new JLabel("titleLable");
        titleLable.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add(titleLable);

        pane.add(Box.createVerticalGlue());

        progressLable = new JLabel("progressLable");
        progressLable.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add(progressLable);

        pane.add(Box.createVerticalGlue());

        toggleButton = new JToggleButton();
        toggleButton.setText("Pause");
        toggleButton.addChangeListener(this);
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add(toggleButton);

        pane.add(Box.createVerticalGlue());
    }

    public static void main(String[] args) {
        new InfoGUI();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (toggleButton.isSelected()) {
            pause();
        } else {
            resume();
        }
    }

    private void pause() {
        toggleButton.setText("Resume");
    }

    private void resume() {
        toggleButton.setText("Pause");
    }

    private void setTitleLabel(String message) {
        titleLable.setText(message);
    }

    private void setProgressLable(String message) {
        progressLable.setText(message);
    }

    // Fully closes the info window
    public void finished() {
        this.setVisible(false);
        this.dispose();
    }
}
