package kw.kng.prerequisites.setup.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class NetworkInterfaceDto {

    private String name;
    private String displayName;

    private boolean up;
    private boolean loopback;
    private boolean virtual;
    private boolean pointToPoint;

    private int mtu;

    private String macAddress;

    private List<String> ipAddresses = new ArrayList<String>();

}