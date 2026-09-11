package kw.kng.prerequisites.setup.rest;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kw.kng.prerequisites.setup.dto.HostInfoDto;
import kw.kng.prerequisites.setup.dto.NetworkInterfaceDto;
import kw.kng.prerequisites.setup.service.NetworkInfoService;

@RestController
@RequestMapping("/api/prerequisites/network")
public class NetworkInfoRest {

	private final NetworkInfoService networkInfoService;

	public NetworkInfoRest(NetworkInfoService networkInfoService) 
	{

		this.networkInfoService = networkInfoService;
	}

	/**
	 * Returns general information about the current application server.
	 */
	@GetMapping("/host")
	public ResponseEntity<HostInfoDto> getHostInfo() throws UnknownHostException, SocketException {

		return ResponseEntity.ok(networkInfoService.getHostInfo());
	}

	/**
	 * Returns all network interfaces detected by the JVM.
	 */
	@GetMapping("/interfaces")
	public ResponseEntity<List<NetworkInterfaceDto>> getNetworkInterfaces() throws SocketException {

		return ResponseEntity.ok(networkInfoService.getNetworkInterfaces());
	}
}
