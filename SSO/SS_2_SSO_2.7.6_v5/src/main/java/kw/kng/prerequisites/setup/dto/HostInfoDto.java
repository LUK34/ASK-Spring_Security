package kw.kng.prerequisites.setup.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class HostInfoDto {

    private String hostName;
    private String canonicalHostName;
    private String primaryIpAddress;

    private String osName;
    private String osVersion;
    private String osArchitecture;

    private String javaVersion;
    private String javaVendor;

    private String userName;
    private String workingDirectory;

    private int availableProcessors;

    private List<NetworkInterfaceDto> networkInterfaces =
            new ArrayList<NetworkInterfaceDto>();

}