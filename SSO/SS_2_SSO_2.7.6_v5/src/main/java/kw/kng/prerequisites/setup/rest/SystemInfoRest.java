package kw.kng.prerequisites.setup.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kw.kng.prerequisites.setup.dto.ApplicationInfoDto;
import kw.kng.prerequisites.setup.dto.DatabaseInfoDto;
import kw.kng.prerequisites.setup.dto.JvmInfoDto;
import kw.kng.prerequisites.setup.dto.MemoryInfoDto;
import kw.kng.prerequisites.setup.service.SystemInfoService;

@RestController
@RequestMapping("/api/prerequisites/system")
public class SystemInfoRest {

	private final SystemInfoService systemInfoService;

	public SystemInfoRest(SystemInfoService systemInfoService) {

		this.systemInfoService = systemInfoService;
	}

	@GetMapping("/application")
	public ResponseEntity<ApplicationInfoDto> getApplicationInfo() {

		return ResponseEntity.ok(systemInfoService.getApplicationInfo());
	}

	@GetMapping("/jvm")
	public ResponseEntity<JvmInfoDto> getJvmInfo() {

		return ResponseEntity.ok(systemInfoService.getJvmInfo());
	}

	@GetMapping("/memory")
	public ResponseEntity<MemoryInfoDto> getMemoryInfo() {

		return ResponseEntity.ok(systemInfoService.getMemoryInfo());
	}

	@GetMapping("/database")
	public ResponseEntity<DatabaseInfoDto> getDatabaseInfo() {

		return ResponseEntity.ok(systemInfoService.getDatabaseInfo());
	}
}