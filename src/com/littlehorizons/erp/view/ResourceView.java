package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Loan;
import com.littlehorizons.erp.model.Resource;

import java.util.List;

public class ResourceView extends ConsoleView {

    public int menu() {
        return menu("GESTION DE RECURSOS", "Listar recursos", "Listar recursos disponibles", "Registrar recurso",
                "Registrar prestamo", "Registrar devolucion", "Prestamos abiertos", "Prestamos vencidos",
                "Cambiar estado (mantenimiento / baja / disponible)", "Prestamos de un estudiante o profesor");
    }

    public void showResources(List<Resource> list) {
        table(List.of("Codigo", "Nombre", "Categoria", "Estado", "Ubicacion"),
                list.stream().map(r -> row(r.code(), r.name(), r.category(), r.status(), r.location())).toList());
    }

    public void showLoans(List<Loan> list) {
        table(List.of("Recurso", "Nombre", "Prestatario", "Tipo", "Prestado", "Devolver", "Devuelto", "Estado"),
                list.stream().map(l -> row(l.resourceCode(), l.resourceName(), l.borrowerName(), l.borrowerType(),
                        l.loanDate(), l.dueDate(), l.returnDate(), l.status())).toList());
    }
}
