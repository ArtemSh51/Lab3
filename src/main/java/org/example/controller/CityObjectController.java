package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.CityObject;
import org.example.service.CityObjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city-objects")
@RequiredArgsConstructor
public class CityObjectController {

    private final CityObjectService service;

    @GetMapping
    public List<CityObject> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CityObject getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityObject create(@RequestBody CityObject cityObject) {
        return service.create(cityObject);
    }

    @PatchMapping("/{id}")
    public CityObject update(@PathVariable Long id, @RequestBody CityObject cityObject) {
        return service.update(id, cityObject);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}