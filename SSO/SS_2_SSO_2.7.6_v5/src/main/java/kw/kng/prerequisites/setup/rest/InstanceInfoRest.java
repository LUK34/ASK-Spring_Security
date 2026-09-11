package kw.kng.prerequisites.setup.rest;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class InstanceInfoRest
{
	  @GetMapping("/instance")
	    public String getInstance() throws UnknownHostException 
	  {

	        InetAddress host = InetAddress.getLocalHost();

	        return "HOST=" + host.getHostName()
	                + ", IP=" + host.getHostAddress();
	    }

}
