package kw.kng.hr.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SsoDetailsDto 
{
    private String username;
    private List<HrFamilyDto> hrFamilyList;
}