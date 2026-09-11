package kw.kng.prerequisites.setup.service;

import kw.kng.prerequisites.setup.dto.ApplicationInfoDto;
import kw.kng.prerequisites.setup.dto.DatabaseInfoDto;
import kw.kng.prerequisites.setup.dto.JvmInfoDto;
import kw.kng.prerequisites.setup.dto.MemoryInfoDto;

public interface SystemInfoService 
{
	 ApplicationInfoDto getApplicationInfo();

	    JvmInfoDto getJvmInfo();

	    MemoryInfoDto getMemoryInfo();

	    DatabaseInfoDto getDatabaseInfo();

}
