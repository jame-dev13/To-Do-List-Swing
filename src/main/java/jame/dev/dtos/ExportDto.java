package jame.dev.dtos;

import jame.dev.models.Status;
import lombok.Builder;

@Builder
public record ExportDto(String desc,
                        Integer priority,
                        Status status) {
}
