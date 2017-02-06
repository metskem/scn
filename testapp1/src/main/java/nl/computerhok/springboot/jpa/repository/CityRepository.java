package nl.computerhok.springboot.jpa.repository;

import nl.computerhok.springboot.jpa.entity.City;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CityRepository extends Repository<City, Long> {
    List<City> findByName(@Param("name") String name);
    List<City> findByNameStartingWith(@Param("name") String name);
    List<City> findAll();
}