package jame.dev.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {
    private int id;
    private UUID uuid;
    private String desc;
    private int priority;
    private Status status;
}
