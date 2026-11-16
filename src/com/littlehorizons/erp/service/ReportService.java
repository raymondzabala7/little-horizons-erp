package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.ClubDao;
import com.littlehorizons.erp.dao.EventDao;
import com.littlehorizons.erp.dao.PaymentDao;
import com.littlehorizons.erp.dao.ResourceDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.EventParticipation;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.Resource;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentAverage;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Statistics built with streams and collectors. */
public class ReportService {

    private final StudentDao studentDao;
    private final AcademicService academicService;
    private final ClubDao clubDao;
    private final EventDao eventDao;
    private final PaymentDao paymentDao;
    private final ResourceDao resourceDao;

    public ReportService(StudentDao studentDao, AcademicService academicService, ClubDao clubDao,
                         EventDao eventDao, PaymentDao paymentDao, ResourceDao resourceDao) {
        this.studentDao = studentDao;
        this.academicService = academicService;
        this.clubDao = clubDao;
        this.eventDao = eventDao;
        this.paymentDao = paymentDao;
        this.resourceDao = resourceDao;
    }

    public Map<String, Long> activeStudentsPerGrade() {
        return studentDao.findAll().stream()
                .filter(s -> "Activo".equals(s.status()))
                .collect(Collectors.groupingBy(Student::gradeLabel, TreeMap::new, Collectors.counting()));
    }

    private List<StudentAverage> averagesOfYear(int year) {
        return studentDao.findAll().stream()
                .filter(s -> "Activo".equals(s.status()))
                .map(s -> {
                    OptionalDouble avg = academicService.yearAverage(s.id(), year);
                    return avg.isPresent() ? new StudentAverage(s.id(), s.fullName(), s.gradeLabel(), avg.getAsDouble()) : null;
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public List<StudentAverage> topStudents(int year, int limit) {
        return averagesOfYear(year).stream()
                .sorted(Comparator.comparingDouble(StudentAverage::average).reversed())
                .limit(limit).toList();
    }

    /** Students whose year average is below the threshold: they need extra support. */
    public List<StudentAverage> studentsNeedingSupport(int year, double threshold) {
        return averagesOfYear(year).stream()
                .filter(a -> a.average() < threshold)
                .sorted(Comparator.comparingDouble(StudentAverage::average))
                .toList();
    }

    public Map<String, Long> participationPerEvent() {
        return eventDao.findAllParticipations().stream()
                .collect(Collectors.groupingBy(EventParticipation::eventName, TreeMap::new, Collectors.counting()));
    }

    public List<Club> clubOccupancy() {
        return clubDao.findAll().stream()
                .sorted(Comparator.comparingInt(Club::activeMembers).reversed())
                .toList();
    }

    public Map<String, BigDecimal> collectedPerMethod() {
        return paymentDao.findAll().stream()
                .filter(Payment::isPaid)
                .collect(Collectors.groupingBy(Payment::method, TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Payment::amount, BigDecimal::add)));
    }

    public Map<String, BigDecimal> collectedPerConcept() {
        return paymentDao.findAll().stream()
                .filter(Payment::isPaid)
                .collect(Collectors.groupingBy(Payment::conceptName, TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Payment::amount, BigDecimal::add)));
    }

    public BigDecimal totalPending() {
        return paymentDao.findPending().stream().map(Payment::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, Long> resourcesPerStatus() {
        return resourceDao.findAll().stream()
                .collect(Collectors.groupingBy(Resource::status, TreeMap::new, Collectors.counting()));
    }
}
