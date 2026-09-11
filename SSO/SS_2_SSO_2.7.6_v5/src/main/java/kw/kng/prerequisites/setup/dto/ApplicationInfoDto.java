package kw.kng.prerequisites.setup.dto;

import lombok.Data;

@Data
public class ApplicationInfoDto {

    private String applicationName;
    private String activeProfiles;
    private String serverPort;
    private String processId;
}
