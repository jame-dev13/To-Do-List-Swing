package jame.dev;

import jame.dev.GUI.CRUDPane;
import jame.dev.Service.ExportService;
import jame.dev.Service.TaskService;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(() -> {
            new CRUDPane(new TaskService(), new ExportService());
        });
    }
}