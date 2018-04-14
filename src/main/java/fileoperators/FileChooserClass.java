package fileoperators;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileChooserClass {
    private File selected;

    public void start() throws Exception {
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                String folder = System.getProperty("user.dir");
                JFileChooser fc = new JFileChooser(folder);
                if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ) {
                    selected = fc.getSelectedFile();
                }
            }
        });
        System.out.println(selected.getAbsolutePath());
    }

    public File getFile() {
        return this.selected;
    }
}