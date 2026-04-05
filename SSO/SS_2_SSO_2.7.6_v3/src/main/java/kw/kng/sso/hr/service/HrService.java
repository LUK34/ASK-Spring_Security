package kw.kng.sso.hr.service;

import java.util.List;

import kw.kng.sso.hr.dto.HrFamilyDto;

public interface HrService 
{
	List<HrFamilyDto> getHrFamilyDto_List(Long militaryId);
	List<HrFamilyDto> nutrio_getHrFamilyDto_List(Long militaryId);

}
