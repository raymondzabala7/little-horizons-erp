package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.Jdbc;
import com.littlehorizons.erp.dao.LoanDao;
import com.littlehorizons.erp.dao.ResourceDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.exception.BusinessRuleException;
import com.littlehorizons.erp.model.Catalog;
import com.littlehorizons.erp.model.Loan;
import com.littlehorizons.erp.model.Resource;

import java.time.LocalDate;
import java.util.List;

/** Resource inventory and loans (library, laboratories, sports, devices). */
public class ResourceService {

    private final ResourceDao resourceDao;
    private final LoanDao loanDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;

    public ResourceService(ResourceDao resourceDao, LoanDao loanDao, StudentDao studentDao, TeacherDao teacherDao) {
        this.resourceDao = resourceDao;
        this.loanDao = loanDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
    }

    public List<Resource> listResources() {
        return resourceDao.findAll();
    }

    public List<Resource> availableResources() {
        return resourceDao.findAll().stream().filter(r -> "Disponible".equals(r.status())).toList();
    }

    public Resource getByCode(String code) {
        return resourceDao.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BusinessRuleException("No existe un recurso con codigo " + code + "."));
    }

    public int register(String code, String name, String category, String location) {
        if (!Catalog.RESOURCE_CATEGORIES.contains(category)) {
            throw new BusinessRuleException("Categoria invalida: " + category);
        }
        if (resourceDao.findByCode(code.toUpperCase()).isPresent()) {
            throw new BusinessRuleException("El codigo " + code + " ya esta en uso.");
        }
        return resourceDao.insert(code.toUpperCase(), name, category, location);
    }

    public void changeStatus(String code, String status) {
        Resource resource = getByCode(code);
        if (!Catalog.RESOURCE_STATUSES.contains(status) || "Prestado".equals(status)) {
            throw new BusinessRuleException("Use la opcion de prestamo para marcar un recurso como prestado.");
        }
        if ("Prestado".equals(resource.status())) {
            throw new BusinessRuleException("El recurso esta prestado; registre primero su devolucion.");
        }
        resourceDao.updateStatus(resource.id(), status);
    }

    public void lend(String code, String borrowerType, int borrowerId, LocalDate dueDate) {
        Resource resource = getByCode(code);
        if (!"Disponible".equals(resource.status())) {
            throw new BusinessRuleException("El recurso no esta disponible (estado: " + resource.status() + ").");
        }
        if (!dueDate.isAfter(LocalDate.now())) {
            throw new BusinessRuleException("La fecha de devolucion debe ser posterior a hoy.");
        }
        boolean exists = Loan.STUDENT.equals(borrowerType)
                ? studentDao.findById(borrowerId).filter(s -> "Activo".equals(s.status())).isPresent()
                : teacherDao.findById(borrowerId).isPresent();
        if (!exists) {
            throw new BusinessRuleException(borrowerType + " " + borrowerId + " no existe o no esta activo.");
        }
        Jdbc.transaction(() -> {
            loanDao.insert(resource.id(), borrowerType, borrowerId, LocalDate.now(), dueDate);
            resourceDao.updateStatus(resource.id(), "Prestado");
            return null;
        });
    }

    public void returnResource(String code) {
        Resource resource = getByCode(code);
        if (!"Prestado".equals(resource.status())) {
            throw new BusinessRuleException("El recurso no esta prestado.");
        }
        Jdbc.transaction(() -> {
            loanDao.markReturned(resource.id(), LocalDate.now());
            resourceDao.updateStatus(resource.id(), "Disponible");
            return null;
        });
    }

    public List<Loan> openLoans() {
        return loanDao.findOpen();
    }

    /** Persists the overdue state and returns the overdue loans. */
    public List<Loan> overdueLoans() {
        loanDao.markOverdue();
        return loanDao.findOverdue();
    }

    public List<Loan> loansOf(String borrowerType, int borrowerId) {
        return loanDao.findByBorrower(borrowerType, borrowerId);
    }
}
