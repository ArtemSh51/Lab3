package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CityReportDto;
import org.example.entity.CityObject;
import org.example.entity.CityReport;
import org.example.entity.ReportStatus;
import org.example.entity.User;
import org.example.repository.CityObjectRepository;
import org.example.repository.CityReportRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityReportService {

    private final CityReportRepository repository;
    private final CityObjectRepository cityObjectRepository;
    private final UserRepository userRepository;

    public List<CityReport> findAll() {
        return repository.findAll();
    }

    public CityReport findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("CityReport not found with id: " + id));
    }

    public CityReport create(CityReportDto dto) {
        CityReport report = new CityReport();
        report.setDescription(dto.getDescription());
        report.setCreatedAt(LocalDateTime.now());

        report.setStatus(ReportStatus.NEW);

        CityObject obj = cityObjectRepository.findById(dto.getCityObjectId())
                .orElseThrow(() -> new RuntimeException("CityObject not found"));
        report.setCityObject(obj);

        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        report.setAuthor(author);

        return repository.save(report);
    }

    public CityReport update(Long id, CityReportDto dto) {
        CityReport report = findById(id);

        if (dto.getDescription() != null) report.setDescription(dto.getDescription());

        if (dto.getCityObjectId() != null) {
            CityObject obj = cityObjectRepository.findById(dto.getCityObjectId())
                    .orElseThrow(() -> new RuntimeException("CityObject not found"));
            report.setCityObject(obj);
        }
        if (dto.getAuthorId() != null) {
            User author = userRepository.findById(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            report.setAuthor(author);
        }

        return repository.save(report);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    @Transactional
    public CityReport rejectReport(Long id) {
        CityReport report = findById(id);

        if (report.getStatus() == ReportStatus.RESOLVED) {
            throw new RuntimeException("Нельзя отклонить обращение, так как проблема уже решена (RESOLVED)");
        }
        if (report.getStatus() == ReportStatus.REJECTED) {
            throw new RuntimeException("Обращение уже было отклонено ранее");
        }

        report.setStatus(ReportStatus.REJECTED);
        return repository.save(report);
    }
}