package com.softserve.edu.delivery.service.impl;

import com.softserve.edu.delivery.dto.LocationDto;
import com.softserve.edu.delivery.repository.CityRepository;
import com.softserve.edu.delivery.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public List<LocationDto> findCitiesByName(String name) {
        return cityRepository
                .findTop10ByCityNameStartingWithIgnoreCaseOrderByCityName(name)
                .stream()
                .map(LocationDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDto> findAllCities() {
        return cityRepository
                .findAll()
                .stream()
                .map(LocationDto::of)
                .collect(Collectors.toList());
    }

}
