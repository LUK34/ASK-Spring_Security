package kw.kng.security.sso.hr.repo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import kw.kng.security.sso.hr.dto.HrFamilyDto;
import kw.kng.security.sso.hr.dto.SSoUserDto;

@Repository
public class HrRepo 
{
	// ------------------------------------------------------------------------------------------------------
	
	private static final Logger logger =  LoggerFactory.getLogger(HrRepo.class);
	
	 @Autowired
	 private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	// ------------------------------------------------------------------------------------------------------ 
	 
	// ------------------------------------------------------------------------------------------------------
		
		 private RowMapper<SSoUserDto> sso_user_RowMapper() 
		 {
			 System.out.println("REPO -> BOILER PLATE CODE RESOLVER -> sso_user_RowMapper() -> START");
		     return (rs, rowNum) -> 
		     {
		    	 SSoUserDto h = new SSoUserDto();
			 	 h.setPrimaryId(rs.getLong("PRIMARY_ID"));
		    	 h.setCivilId(rs.getLong("CIVIL_ID_NO"));
		    	 h.setMilitaryId(rs.getLong("MILITARY_NO"));
		    	 h.setNameEn(rs.getString("NAME_E"));
		    	 h.setNameAr(rs.getString("NAME_A"));
		    	 h.setGender(rs.getString("GENDER"));
		    	 h.setDesignation(rs.getString("DESIGNATION"));
		    	 h.setMobileNo(rs.getLong("MOBILE_NO"));
		    	 h.setNationality(rs.getString("NATIONALITY"));
		    	 h.setJobStatus(rs.getString("JOB_STATUS"));
		    	 h.setRelation(rs.getString("RELATION"));
		         
		         System.out.println("REPO -> BOILER PLATE CODE RESOLVER -> sso_user_RowMapper() -> END");
		         return h;
		     };
		 }
		// ------------------------------------------------------------------------------------------------------
	 
	 private String loadQueryFromFile(String filePath) 
	 {
		 
		 try 
		 {
		        ClassPathResource resource = new ClassPathResource(filePath);
		        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
		        return new String(bytes, StandardCharsets.UTF_8);
		 } 
		 catch (IOException e) 
		 {
		        throw new RuntimeException("Unable to load SQL file: " + filePath, e);
		 }
		 
	 } 
	 
	 public List<HrFamilyDto> getHrFamilyDto_List(Long militaryId)
	 {
		 logger.info("================== REPO LAYER -> getHrFamilyDto_List -> START ================== ");
		 logger.info("Military Id: " + militaryId);
		 
		 String sql=loadQueryFromFile("sql/hr/vw_hr_family_mid.sql");
		 
		 MapSqlParameterSource params = new MapSqlParameterSource();
	     params.addValue("militaryId", militaryId);
		 
	     RowMapper<HrFamilyDto> rowMapper = (rs, rowNum) -> 
	     {
	    	 	HrFamilyDto  hf = new HrFamilyDto();
	    	 	hf.setPrimaryId(rs.getLong("PRIMARY_ID"));
	    	 	hf.setCivilId(rs.getLong("CIVIL_ID_NO"));
	    	 	hf.setMilitaryId(rs.getLong("MILITARY_NO"));
	    	 	hf.setNameEn(rs.getString("NAME_E"));
	    	 	hf.setNameAr(rs.getString("NAME_A"));
	    	 	hf.setGender(rs.getString("GENDER"));
	    	 	hf.setDesignation(rs.getString("DESIGNATION"));
	    	 	hf.setMobileNo(rs.getLong("MOBILE_NO"));
	    	 	hf.setNationality(rs.getString("NATIONALITY"));
	    	 	hf.setJobStatus(rs.getString("JOB_STATUS"));
	    	 	hf.setRelation(rs.getString("RELATION"));
	            return hf;
	    };
	    logger.info("================== REPO LAYER -> getHrFamilyDto_List -> END ================== ");
	    
	    return namedParameterJdbcTemplate.query(sql, params, rowMapper); 
	 }
	 
	 public List<SSoUserDto> get_NUTRIO_SSO_USER_details(Integer militaryId)
	 {
		 System.out.println("================== REPO LAYER -> get_sso_UserDto_List -> START ================== ");
		 System.out.println("Military Id: " + militaryId);
		 
		 String sql=loadQueryFromFile("sql/hr/nutrio_sso_user_mid.sql");
		 
		 MapSqlParameterSource params = new MapSqlParameterSource();
	     params.addValue("militaryId", militaryId);
		 
	     System.out.println("================== REPO LAYER -> get_sso_UserDto_List -> END ================== ");
	    
	    return namedParameterJdbcTemplate.query(sql, params, sso_user_RowMapper()); 
	 }
	 
	 
	 public List<HrFamilyDto> nutrio_getHrFamilyDto_List(Long militaryId)
	 {
		 logger.info("================== REPO LAYER -> nutrio_getHrFamilyDto_List -> START ================== ");
		 logger.info("Military Id: " + militaryId);
		 
		 String sql=loadQueryFromFile("sql/hr/nutrio_hr_family_mid.sql");
		 
		 MapSqlParameterSource params = new MapSqlParameterSource();
	     params.addValue("militaryId", militaryId);
		 
	     RowMapper<HrFamilyDto> rowMapper = (rs, rowNum) -> 
	     {
	    	 	HrFamilyDto  hf = new HrFamilyDto();
	    	 	hf.setPrimaryId(rs.getLong("PRIMARY_ID"));
	    	 	hf.setCivilId(rs.getLong("CIVIL_ID_NO"));
	    	 	hf.setMilitaryId(rs.getLong("MILITARY_NO"));
	    	 	hf.setNameEn(rs.getString("NAME_E"));
	    	 	hf.setNameAr(rs.getString("NAME_A"));
	    	 	hf.setGender(rs.getString("GENDER"));
	    	 	hf.setDesignation(rs.getString("DESIGNATION"));
	    	 	hf.setMobileNo(rs.getLong("MOBILE_NO"));
	    	 	hf.setNationality(rs.getString("NATIONALITY"));
	    	 	hf.setJobStatus(rs.getString("JOB_STATUS"));
	    	 	hf.setRelation(rs.getString("RELATION"));
	            return hf;
	    };
	    logger.info("================== REPO LAYER -> nutrio_getHrFamilyDto_List -> END ================== ");
	    
	    return namedParameterJdbcTemplate.query(sql, params, rowMapper); 
	 }

}
