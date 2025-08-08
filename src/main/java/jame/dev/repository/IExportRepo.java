package jame.dev.repository;

import jame.dev.dtos.ExportDto;

import java.util.List;

public interface IExportRepo {
    void toTxt(List<ExportDto> tasks, String path, String name);
    void toCsv(List<ExportDto> tasks, String path, String name);
}
