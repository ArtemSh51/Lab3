package org.example.controller;

import org.example.dto.CityReportDto;
import org.example.entity.CityReport;
import org.example.service.CityReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city-reports")
public class CityReportController {

    @Autowired
    private CityReportService service;

    @GetMapping
    public List<CityReport> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CityReport getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityReport create(@RequestBody CityReportDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{id}/reject")
    public CityReport reject(@PathVariable Long id) {
        return service.rejectReport(id);
    }

    @PatchMapping("/{id}")
    public CityReport update(@PathVariable Long id, @RequestBody CityReportDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}