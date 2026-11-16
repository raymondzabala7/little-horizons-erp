package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Loan;

import java.time.LocalDate;
import java.util.List;

public interface LoanDao {
    List<Loan> findOpen();
    List<Loan> findOverdue();
    List<Loan> findByBorrower(String borrowerType, int borrowerId);
    int insert(int resourceId, String borrowerType, int borrowerId, LocalDate loanDate, LocalDate dueDate);
    int markReturned(int resourceId, LocalDate returnDate);
    int markOverdue();
}
