package kw.kng.prerequisites.setup.service;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import kw.kng.prerequisites.setup.dto.ApplicationInfoDto;
import kw.kng.prerequisites.setup.dto.DatabaseInfoDto;
import kw.kng.prerequisites.setup.dto.JvmInfoDto;
import kw.kng.prerequisites.setup.dto.MemoryInfoDto;

@Service
public class SystemInfoServiceImpl implements SystemInfoService {

	private final Environment environment;
	private final DataSource dataSource;

	@Value("${spring.application.name:UNKNOWN}")
	private String applicationName;

	@Value("${server.port:UNKNOWN}")
	private String serverPort;

	public SystemInfoServiceImpl(Environment environment, DataSource dataSource) {

		this.environment = environment;
		this.dataSource = dataSource;
	}

	@Override
	public ApplicationInfoDto getApplicationInfo() {

		ApplicationInfoDto dto = new ApplicationInfoDto();

		dto.setApplicationName(applicationName);
		dto.setServerPort(serverPort);

		String[] profiles = environment.getActiveProfiles();

		if (profiles != null && profiles.length > 0) {
			dto.setActiveProfiles(String.join(",", profiles));
		} else {
			dto.setActiveProfiles("DEFAULT");
		}

		/*
		 * Java 8 compatible way of obtaining process information.
		 *
		 * RuntimeMXBean name is commonly:
		 *
		 * PID@HOSTNAME
		 */
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		String runtimeName = runtimeMXBean.getName();

		if (runtimeName != null && runtimeName.contains("@")) {
			dto.setProcessId(runtimeName.substring(0, runtimeName.indexOf("@")));
		} else {
			dto.setProcessId(runtimeName);
		}

		return dto;
	}

	@Override
	public JvmInfoDto getJvmInfo() {

		JvmInfoDto dto = new JvmInfoDto();

		dto.setJavaVersion(System.getProperty("java.version"));

		dto.setJavaVendor(System.getProperty("java.vendor"));

		dto.setJavaHome(System.getProperty("java.home"));

		dto.setVmName(System.getProperty("java.vm.name"));

		dto.setVmVendor(System.getProperty("java.vm.vendor"));

		dto.setVmVersion(System.getProperty("java.vm.version"));

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		dto.setUptimeMilliseconds(runtimeMXBean.getUptime());

		dto.setAvailableProcessors(Runtime.getRuntime().availableProcessors());

		return dto;
	}

	@Override
	public MemoryInfoDto getMemoryInfo() {

		MemoryInfoDto dto = new MemoryInfoDto();

		Runtime runtime = Runtime.getRuntime();

		long maxMemory = runtime.maxMemory();

		long totalMemory = runtime.totalMemory();

		long freeMemory = runtime.freeMemory();

		long usedMemory = totalMemory - freeMemory;

		dto.setMaxMemoryBytes(maxMemory);
		dto.setTotalMemoryBytes(totalMemory);
		dto.setFreeMemoryBytes(freeMemory);
		dto.setUsedMemoryBytes(usedMemory);

		long mb = 1024L * 1024L;

		dto.setMaxMemoryMb(maxMemory / mb);
		dto.setTotalMemoryMb(totalMemory / mb);
		dto.setFreeMemoryMb(freeMemory / mb);
		dto.setUsedMemoryMb(usedMemory / mb);

		return dto;
	}

	@Override
	public DatabaseInfoDto getDatabaseInfo() {

		DatabaseInfoDto dto = new DatabaseInfoDto();

		Connection connection = null;

		try {

			connection = dataSource.getConnection();

			DatabaseMetaData metadata = connection.getMetaData();

			dto.setConnected(true);

			dto.setDatabaseProductName(metadata.getDatabaseProductName());

			dto.setDatabaseProductVersion(metadata.getDatabaseProductVersion());

			dto.setDriverName(metadata.getDriverName());

			dto.setDriverVersion(metadata.getDriverVersion());

		} catch (Exception ex) {

			dto.setConnected(false);

			/*
			 * Do not expose complete exception information, credentials or JDBC
			 * configuration through REST.
			 */
			dto.setErrorMessage("Unable to connect to database.");

		} finally {

			if (connection != null) {

				try {
					connection.close();
				} catch (Exception ignored) {
					// Nothing required here.
				}
			}
		}

		return dto;
	}

}
