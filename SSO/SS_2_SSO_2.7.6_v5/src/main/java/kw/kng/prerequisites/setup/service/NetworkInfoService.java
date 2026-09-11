package kw.kng.prerequisites.setup.service;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import kw.kng.prerequisites.setup.dto.HostInfoDto;
import kw.kng.prerequisites.setup.dto.NetworkInterfaceDto;

public interface NetworkInfoService {
	HostInfoDto getHostInfo() throws UnknownHostException, SocketException;

	List<NetworkInterfaceDto> getNetworkInterfaces() throws SocketException;

}
