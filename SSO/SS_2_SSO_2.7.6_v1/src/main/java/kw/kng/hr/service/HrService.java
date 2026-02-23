package kw.kng.hr.service;

import java.util.List;

import kw.kng.hr.dto.HrFamilyDto;

public interface HrService 
{
	List<HrFamilyDto> getHrFamilyDto_List(Long militaryId);

}
