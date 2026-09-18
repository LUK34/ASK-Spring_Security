package kw.kng.sso.hr.service;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kw.kng.sso.hr.dto.HrFamilyDto;

@Service
public class SessionServiceImpl implements SessionService 
{
	private static final Logger logger =  LoggerFactory.getLogger(SessionServiceImpl.class);

	@Override
	public void printSessionAttributes(HttpSession session) 
	{
		logger.info("============= SESSION DEBUG START =============");
	    logger.info("SESSION ID: {}", session.getId());

	    Enumeration<String> attributeNames = session.getAttributeNames();

	    while (attributeNames.hasMoreElements())
	    {
	        String name = attributeNames.nextElement();
	        Object value = session.getAttribute(name);

	        logger.info("SESSION KEY   : {}", name);
	        logger.info("SESSION TYPE  : {}", (value != null ? value.getClass().getName() : "null"));

	        if (value instanceof List)
	        {
	            List<?> list = (List<?>) value;

	            logger.info("SESSION LIST SIZE : {}", list.size());

	            for (Object obj : list)
	            {
	                if (obj instanceof HrFamilyDto)
	                {
	                    HrFamilyDto dto = (HrFamilyDto) obj;

	                    logger.info("---- HR FAMILY DTO ----");
	                    logger.info("PrimaryId   : {}", dto.getPrimaryId());
	                    logger.info("CivilId     : {}", dto.getCivilId());
	                    logger.info("MilitaryId  : {}", dto.getMilitaryId());
	                    logger.info("NameEn      : {}", dto.getNameEn());
	                    logger.info("NameAr      : {}", dto.getNameAr());
	                    logger.info("Designation : {}", dto.getDesignation());
	                    logger.info("Nationality : {}", dto.getNationality());
	                    logger.info("JobStatus   : {}", dto.getJobStatus());
	                    logger.info("Relation    : {}", dto.getRelation());
	                }
	            }
	        }
	        else
	        {
	            logger.info("SESSION VALUE : {}", value);
	        }

	        logger.info("-----------------------------------------------");
	    }

	    logger.info("============= SESSION DEBUG END =============");

	}

}
