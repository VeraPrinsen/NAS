package gui;

import host.Task;
import outgoingpacketcontrol.SendingTask;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class InfoGUI extends JFrame implements ChangeListener {

    private Task task;

    private JLabel titleLable;
    private JLabel progressLable;
    private JToggleButton toggleButton;

    public InfoGUI(Task task) {
        this.task = task;

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

    @Override
    public void stateChanged(ChangeEvent e) {
        if (toggleButton.isSelected()) {
            pause();
        } else {
            resume();
        }
    }

    private void pause() {
        task.pause();
        toggleButton.setText("Resume");
    }

    private void resume() {
        task.resume();
        toggleButton.setText("Pause");
    }

    public void setTitleLabel(String message) {
        titleLable.setText(message);
    }

    public void setProgressLable(String message) {
        progressLable.setText(message);
    }

    // Fully closes the info window
    public void finished() {
        this.setVisible(false);
        this.dispose();
    }
}
