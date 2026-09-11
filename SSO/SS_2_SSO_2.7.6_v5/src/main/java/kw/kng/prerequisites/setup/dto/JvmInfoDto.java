package kw.kng.prerequisites.setup.dto;

import lombok.Data;

@Data
public class JvmInfoDto {

    private String javaVersion;
    private String javaVendor;
    private String javaHome;

    private String vmName;
    private String vmVendor;
    private String vmVersion;

    private long uptimeMilliseconds;

    private int availableProcessors;
}
