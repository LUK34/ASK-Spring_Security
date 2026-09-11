package kw.kng.prerequisites.setup.dto;

import lombok.Data;

@Data
public class MemoryInfoDto {

    private long maxMemoryBytes;
    private long totalMemoryBytes;
    private long freeMemoryBytes;
    private long usedMemoryBytes;

    private long maxMemoryMb;
    private long totalMemoryMb;
    private long freeMemoryMb;
    private long usedMemoryMb;
}