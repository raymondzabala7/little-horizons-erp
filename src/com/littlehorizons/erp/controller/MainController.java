package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.AcademicRecordDao;
import com.littlehorizons.erp.dao.AcademicRecordDaoImpl;
import com.littlehorizons.erp.dao.AttendanceDao;
import com.littlehorizons.erp.dao.AttendanceDaoImpl;
import com.littlehorizons.erp.dao.ClubDao;
import com.littlehorizons.erp.dao.ClubDaoImpl;
import com.littlehorizons.erp.dao.EventDao;
import com.littlehorizons.erp.dao.EventDaoImpl;
import com.littlehorizons.erp.dao.GuardianDao;
import com.littlehorizons.erp.dao.GuardianDaoImpl;
import com.littlehorizons.erp.dao.InvoiceDao;
import com.littlehorizons.erp.dao.InvoiceDaoImpl;
import com.littlehorizons.erp.dao.LoanDao;
import com.littlehorizons.erp.dao.LoanDaoImpl;
import com.littlehorizons.erp.dao.PaymentDao;
import com.littlehorizons.erp.dao.PaymentDaoImpl;
import com.littlehorizons.erp.dao.ResourceDao;
import com.littlehorizons.erp.dao.ResourceDaoImpl;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.dao.StudentDaoImpl;
import com.littlehorizons.erp.dao.SubjectDao;
import com.littlehorizons.erp.dao.SubjectDaoImpl;
import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.dao.TeacherDaoImpl;
import com.littlehorizons.erp.exception.InputCancelledException;
import com.littlehorizons.erp.service.AcademicService;
import com.littlehorizons.erp.service.ExtracurricularService;
import com.littlehorizons.erp.service.FinanceService;
import com.littlehorizons.erp.service.InvoiceTextWriter;
import com.littlehorizons.erp.service.ReportService;
import com.littlehorizons.erp.service.ResourceService;
import com.littlehorizons.erp.service.StudentService;
import com.littlehorizons.erp.service.TeacherService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.ConsoleView;

/** Composition root: wires DAOs, services and controllers (constructor injection) and runs the main menu. */
public class MainController {

    private final ConsoleView console = new ConsoleView();
    private final StudentController students;
    private final AcademicController academic;
    private final TeacherController teachers;
    private final ExtracurricularController extracurricular;
    private final FinanceController finance;
    private final ResourceController resources;
    private final ReportController reports;

    public MainController() {
        StudentDao studentDao = new StudentDaoImpl();
        GuardianDao guardianDao = new GuardianDaoImpl();
        TeacherDao teacherDao = new TeacherDaoImpl();
        SubjectDao subjectDao = new SubjectDaoImpl();
        AcademicRecordDao recordDao = new AcademicRecordDaoImpl();
        AttendanceDao attendanceDao = new AttendanceDaoImpl();
        ClubDao clubDao = new ClubDaoImpl();
        EventDao eventDao = new EventDaoImpl();
        PaymentDao paymentDao = new PaymentDaoImpl();
        InvoiceDao invoiceDao = new InvoiceDaoImpl();
        ResourceDao resourceDao = new ResourceDaoImpl();
        LoanDao loanDao = new LoanDaoImpl();

        StudentService studentService = new StudentService(studentDao, guardianDao);
        AcademicService academicService = new AcademicService(subjectDao, studentDao, teacherDao, recordDao, attendanceDao);
        TeacherService teacherService = new TeacherService(teacherDao, subjectDao);
        ExtracurricularService extracurricularService = new ExtracurricularService(clubDao, eventDao, studentDao, teacherDao);
        FinanceService financeService = new FinanceService(paymentDao, invoiceDao, studentDao, guardianDao, new InvoiceTextWriter());
        ResourceService resourceService = new ResourceService(resourceDao, loanDao, studentDao, teacherDao);
        ReportService reportService = new ReportService(studentDao, academicService, financeService, clubDao, eventDao, resourceDao);

        this.students = new StudentController(studentService);
        this.academic = new AcademicController(academicService);
        this.teachers = new TeacherController(teacherService, academicService);
        this.extracurricular = new ExtracurricularController(extracurricularService);
        this.finance = new FinanceController(financeService);
        this.resources = new ResourceController(resourceService);
        this.reports = new ReportController(reportService);
    }

    public void run() {
        while (true) {
            try {
                console.banner("ERP Colegio Little Horizons", "Sistema de Gestion Educativa");
                System.out.println("""
                          1. Gestion de Estudiantes
                          2. Gestion Academica
                          3. Gestion de Profesores
                          4. Actividades Extracurriculares
                          5. Gestion Financiera
                          6. Gestion de Recursos
                          7. Reportes y Estadisticas
                          8. Salir
                        """);
                int option = InputValidator.readInt("Seleccione una opcion: ", 1, 8);
                switch (option) {
                    case 1 -> students.run();
                    case 2 -> academic.run();
                    case 3 -> teachers.run();
                    case 4 -> extracurricular.run();
                    case 5 -> finance.run();
                    case 6 -> resources.run();
                    case 7 -> reports.run();
                    default -> {
                        console.info("Hasta pronto.");
                        return;
                    }
                }
            } catch (InputCancelledException e) {
                console.cancelled();
            }
        }
    }
}
