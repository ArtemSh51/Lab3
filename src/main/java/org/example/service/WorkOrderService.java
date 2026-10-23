package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.WorkOrderDto;
import org.example.entity.*;
import org.example.repository.CityReportRepository;
import org.example.repository.WorkCrewRepository;
import org.example.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderRepository orderRepository;
    private final WorkCrewRepository crewRepository;
    private final CityReportRepository reportRepository;

    private static final List<OrderStatus> ACTIVE_STATUSES =
            List.of(OrderStatus.ASSIGNED, OrderStatus.IN_PROGRESS);

    public List<WorkOrder> findAll() {
        return orderRepository.findAll();
    }

    public WorkOrder findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkOrder not found with id: " + id));
    }

    @Transactional
    public WorkOrder create(WorkOrderDto dto) {
        WorkCrew crew = crewRepository.findById(dto.getCrewId())
                .orElseThrow(() -> new RuntimeException("WorkCrew not found"));
        CityReport report = reportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new RuntimeException("CityReport not found"));

        String objectType = report.getCityObject() != null ? report.getCityObject().getType() : null;
        if (objectType == null || crew.getSupportedObjectTypes() == null
                || !crew.getSupportedObjectTypes().contains(objectType)) {
            throw new RuntimeException("Crew does not support object type: " + objectType);
        }

        long activeCount = orderRepository
                .countByCrewIdAndStatusIn(crew.getId(), ACTIVE_STATUSES);
        if (activeCount >= crew.getActiveTasksLimit()) {
            throw new RuntimeException("Crew has reached the active tasks limit");
        }

        WorkOrder order = new WorkOrder();
        order.setCrew(crew);
        order.setReport(report);
        order.setDeadline(dto.getDeadline());
        order.setStatus(OrderStatus.ASSIGNED);
        order.setResultDescription(dto.getResultDescription());

        report.setStatus(ReportStatus.IN_PROGRESS);
        reportRepository.save(report);

        return orderRepository.save(order);
    }

    public WorkOrder update(Long id, WorkOrderDto dto) {
        WorkOrder order = findById(id);
        if (dto.getDeadline() != null) order.setDeadline(dto.getDeadline());
        if (dto.getResultDescription() != null) order.setResultDescription(dto.getResultDescription());
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        findById(id);
        orderRepository.deleteById(id);
    }

    @Transactional
    public WorkOrder completeOrder(Long id) {
        WorkOrder order = findById(id);
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Наряд уже завершен");
        }

        order.setStatus(OrderStatus.COMPLETED);
        CityReport report = order.getReport();
        report.setStatus(ReportStatus.RESOLVED);

        reportRepository.save(report);
        return orderRepository.save(order);
    }

    @Transactional
    public WorkOrder cancelOrder(Long id) {
        WorkOrder order = findById(id);
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Нельзя отменить уже завершенный наряд");
        }

        order.setStatus(OrderStatus.CANCELLED);
        CityReport report = order.getReport();
        report.setStatus(ReportStatus.NEW);

        reportRepository.save(report);
        return orderRepository.save(order);
    }

    @Transactional
    public WorkOrder reassignCrew(Long orderId, Long newCrewId) {
        WorkOrder order = findById(orderId);
        WorkCrew newCrew = crewRepository.findById(newCrewId)
                .orElseThrow(() -> new RuntimeException("Новая бригада не найдена"));

        String objectType = order.getReport().getCityObject().getType();
        if (newCrew.getSupportedObjectTypes() == null
                || !newCrew.getSupportedObjectTypes().contains(objectType)) {
            throw new RuntimeException("Новая бригада не поддерживает тип: " + objectType);
        }

        long activeCount = orderRepository
                .countByCrewIdAndStatusIn(newCrew.getId(), ACTIVE_STATUSES);
        if (activeCount >= newCrew.getActiveTasksLimit()) {
            throw new RuntimeException("У новой бригады превышен лимит активных работ");
        }

        order.setCrew(newCrew);
        return orderRepository.save(order);
    }
}