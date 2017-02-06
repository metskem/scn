package nl.computerhok.scn.jpa.repository;

import nl.computerhok.scn.jpa.entity.City;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CityRepository extends Repository<City, Long> {
    List<City> findByName(@Param("name") String name);
    List<City> findByNameStartingWith(@Param("name") String name);
    List<City> findAll();
}