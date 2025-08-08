package jame.dev.Service;

import jame.dev.dtos.ExportDto;
import jame.dev.repository.IExportRepo;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Log
public class ExportService implements IExportRepo {

    @Override
    public void toTxt(List<ExportDto> tasks, String path, String name)  {
        try{
            String absolutePath = "%s%s%s".formatted(System.getProperty("user.home"),
                    System.getProperty("file.separator"),
                    path);
            System.out.println(absolutePath);
            Path file = Files.createFile(Path.of(absolutePath, name));
            List<String> lines = tasks
                    .stream()
                    .map(task ->
                         String.format("Task: %s | Priority level: %d | Status: %s",
                               task.desc(), task.priority(), task.status().name())
                    ).toList();
            Files.write(file, lines, StandardCharsets.UTF_8);
        }catch (IOException io){
            log.warning("The file: " + path +
                    " Not found or the file " + name + "already exists.");
            log.info(io.getMessage());
        }
    }

    @Override
    public void toCsv(List<ExportDto> tasks, String path, String name) {
    }


}
