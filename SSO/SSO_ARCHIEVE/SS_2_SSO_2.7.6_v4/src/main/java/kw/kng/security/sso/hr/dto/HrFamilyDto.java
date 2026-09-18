package kw.kng.security.sso.hr.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class HrFamilyDto {
	private Long primaryId;
	private Long civilId;
	private Long militaryId;
	private String nameEn;
	private String nameAr;
	private String gender;
	private LocalDate dob;
	private Long mobileNo;
	private String designation;
	private String nationality;
	private String jobStatus;
	private String relation;
}
