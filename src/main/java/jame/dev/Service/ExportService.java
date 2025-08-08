package jame.dev.Service;

import jame.dev.dtos.ExportDto;
import jame.dev.repository.IExportRepo;
import lombok.extern.java.Log;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Log
public class ExportService implements IExportRepo {

    @Override
    public void toTxt(List<ExportDto> tasks, String path, String name)  {
        try{
            String absolutePath = "%s%s%s".formatted(System.getProperty("user.home"),
                    System.getProperty("file.separator"),
                    path);
            Path dir = Path.of(absolutePath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            Path file = dir.resolve(name);

            List<String> lines = tasks
                    .stream()
                    .map(task ->
                         String.format("Task: %s | Priority level: %d | Status: %s",
                               task.desc(), task.priority(), task.status().name())
                    ).toList();
            Files.write(file, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE);
        }catch (IOException io){
            JOptionPane.showMessageDialog(null, "Error writing file.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            log.info(io.getMessage());
        }
    }

    @Override
    public void toCsv(List<ExportDto> tasks, String path, String name) {
        try {
            String absolutePath = "%s%s%s".formatted(
                    System.getProperty("user.home"),
                    System.getProperty("file.separator"),
                    path
            );

            Path dir = Path.of(absolutePath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            Path file = dir.resolve(name);

            String headers = "name,priority,status\n";
            Files.writeString(file, headers,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE);

            List<String> lines = tasks.stream()
                    .map(t -> String.format("%s,%s,%s",
                            t.desc(),
                            t.priority(),
                            t.status().name()))
                    .toList();

            Files.write(file, lines, StandardOpenOption.APPEND);

        } catch (IOException io) {
            JOptionPane.showMessageDialog(null, "Error writing file.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            log.warning(io.getMessage());
        }
    }



}
