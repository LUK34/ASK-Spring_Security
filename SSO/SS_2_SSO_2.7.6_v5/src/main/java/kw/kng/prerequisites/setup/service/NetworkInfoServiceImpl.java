package kw.kng.prerequisites.setup.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.stereotype.Service;

import kw.kng.prerequisites.setup.dto.HostInfoDto;
import kw.kng.prerequisites.setup.dto.NetworkInterfaceDto;

@Service
public class NetworkInfoServiceImpl implements NetworkInfoService {

	@Override
	public HostInfoDto getHostInfo() throws UnknownHostException, SocketException {

		HostInfoDto dto = new HostInfoDto();

		InetAddress localHost = InetAddress.getLocalHost();

		dto.setHostName(localHost.getHostName());
		dto.setCanonicalHostName(localHost.getCanonicalHostName());
		dto.setPrimaryIpAddress(localHost.getHostAddress());

		/*
		 * Operating system information
		 */
		dto.setOsName(System.getProperty("os.name"));
		dto.setOsVersion(System.getProperty("os.version"));
		dto.setOsArchitecture(System.getProperty("os.arch"));

		/*
		 * Java runtime information
		 */
		dto.setJavaVersion(System.getProperty("java.version"));
		dto.setJavaVendor(System.getProperty("java.vendor"));

		/*
		 * Runtime information
		 */
		dto.setUserName(System.getProperty("user.name"));
		dto.setWorkingDirectory(System.getProperty("user.dir"));

		dto.setAvailableProcessors(Runtime.getRuntime().availableProcessors());

		/*
		 * Get all network adapters.
		 */
		dto.setNetworkInterfaces(getNetworkInterfaces());

		return dto;
	}

	@Override
	public List<NetworkInterfaceDto> getNetworkInterfaces() throws SocketException {

		List<NetworkInterfaceDto> result = new ArrayList<NetworkInterfaceDto>();

		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

		if (interfaces == null) {
			return result;
		}

		for (NetworkInterface networkInterface : Collections.list(interfaces)) {

			NetworkInterfaceDto dto = new NetworkInterfaceDto();

			dto.setName(networkInterface.getName());
			dto.setDisplayName(networkInterface.getDisplayName());

			dto.setUp(networkInterface.isUp());
			dto.setLoopback(networkInterface.isLoopback());
			dto.setVirtual(networkInterface.isVirtual());
			dto.setPointToPoint(networkInterface.isPointToPoint());

			dto.setMtu(networkInterface.getMTU());

			/*
			 * MAC address
			 */
			dto.setMacAddress(formatMacAddress(networkInterface.getHardwareAddress()));

			/*
			 * IPv4 + IPv6 addresses assigned to this interface.
			 */
			List<String> addresses = new ArrayList<String>();

			Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

			while (inetAddresses.hasMoreElements()) {

				InetAddress address = inetAddresses.nextElement();

				addresses.add(address.getHostAddress());
			}

			dto.setIpAddresses(addresses);

			result.add(dto);
		}

		return result;
	}

	private String formatMacAddress(byte[] macAddress) {

		if (macAddress == null || macAddress.length == 0) {
			return null;
		}

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < macAddress.length; i++) {

			if (i > 0) {
				builder.append("-");
			}

			builder.append(String.format("%02X", macAddress[i] & 0xFF));
		}

		return builder.toString();
	}
}