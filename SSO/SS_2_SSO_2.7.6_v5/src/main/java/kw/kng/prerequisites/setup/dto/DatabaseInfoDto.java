package kw.kng.prerequisites.setup.dto;

import lombok.Data;

@Data
public class DatabaseInfoDto {

    private boolean connected;

    private String databaseProductName;
    private String databaseProductVersion;

    private String driverName;
    private String driverVersion;

    private String errorMessage;
}
