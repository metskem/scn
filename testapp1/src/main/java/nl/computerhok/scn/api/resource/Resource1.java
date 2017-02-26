package nl.computerhok.scn.api.resource;

import nl.computerhok.scn.jpa.entity.City;
import nl.computerhok.scn.jpa.repository.CityRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@RestController
@RefreshScope
public class Resource1 {
    private static final Log log = LogFactory.getLog(Resource1.class);

    @Autowired
    private CityRepository cityRepo;

    @Value("${spring.application.name:MISSING}")
    private String appName;

    @Value("${spring.config.name:MISSING}")
    private String configName;

    @Value("${prop1}")
    private String prop1;

    @Value("${prop3}")
    private String prop3;

    @RequestMapping("/props")
    @Produces("application/json")
    public String getProps() {
        return "prop1 is \"" + prop1 +
                "\"  prop3 is \"" + prop3 +
                "\"  spring.application.name is \"" + appName +
                "\"  spring.config.name is \"" + configName +"\"";
    }

    @RequestMapping(value="/headers" , method= RequestMethod.GET)
    @Produces("application/json")
    public List<String[]> headers(@Context HttpServletRequest request) {
        List<String[]> requestinfo = new ArrayList<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            requestinfo.add(new String[] {headerName , request.getHeader(headerName)});
        }
        return requestinfo;
    }

    @RequestMapping(value = "/envvars", method = RequestMethod.GET)
    @Produces("application/json")
    public Map<String,String> envvars() {
        return System.getenv();
    }

    @RequestMapping(value="/echo/{message}", method= RequestMethod.GET)
    @Produces("application/json")
    public String echo(@PathVariable String message) {
        log.info("echoing " + message);
        return "Echo: " + message;
    }

    @RequestMapping(value="/findCityByName/{name}", method= RequestMethod.GET)
    @Produces("application/json")
    public List<City> findCityByName(@PathVariable String name) {
        log.info("searching city by name=" + name);
        List<City> cities = cityRepo.findByNameStartingWith(name);
        log.info("total number of cities found: " + cities.size());
        for (City city:cities) {
            log.debug("found " + city);
        }
        return cities;
    }


    @RequestMapping(value="/findCityAll", method= RequestMethod.GET)
    @Produces("application/json")
    public List<City> findCityAll() {
        log.info("searching for all cities");
        List<City> cities = cityRepo.findAll();
        log.info("total number of cities found: " + cities.size());
        for (City city:cities) {
            log.debug("found " + city);
        }
        return cities;
    }


}
