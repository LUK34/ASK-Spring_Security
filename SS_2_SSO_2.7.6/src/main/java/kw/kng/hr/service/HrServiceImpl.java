package kw.kng.hr.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kw.kng.dto.HrFamilyDto;
import kw.kng.hr.repo.HrRepo;
import kw.kng.security.config.JespaAuthFilter;

@Service
public class HrServiceImpl implements HrService 
{
	// -------------------------------------------------------------------------------------------------------------------------
	private static final Logger logger =  LoggerFactory.getLogger(HrServiceImpl.class);
	 
	private HrRepo h_repo;
	public HrServiceImpl(HrRepo h_repo)
	{
		this.h_repo=h_repo;
	}
	// -------------------------------------------------------------------------------------------------------------------------
	
	@Override
	public List<HrFamilyDto> getHrFamilyDto_List(Long militaryId) 
	{
		logger.info("================== SERVICE LAYER -> getHrFamilyDto_List -> START ================== ");
		logger.info("Military Id: " + militaryId);
		
		List<HrFamilyDto> hrFamilyDto_list= h_repo.getHrFamilyDto_List(militaryId);
		if (hrFamilyDto_list == null || hrFamilyDto_list.isEmpty()) 
		{
		    logger.warn("No HR Data Found for Military ID: ", militaryId);
		}
		
		hrFamilyDto_list.forEach(m ->
			System.out.println("Officers Family Details" +
				"Primary Id: " +m.getPrimaryId()+
				"Civil Id: "+m.getCivilId()+
				"Military Id: "+m.getMilitaryId()+
				"English Name: "+m.getNameEn()+
				"Arabic Name: "+m.getNameAr()+
				"Designation: "+m.getDesignation()+
				"Nationality: "+m.getNationality()+
				"Job Status: "+m.getJobStatus()+
				"Relation: "+m.getRelation()
				)		
		);
		logger.info("================== SERVICE LAYER -> getHrFamilyDto_List -> END ================== ");
		return hrFamilyDto_list;
	}

}
