package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.PersonName;
import com.littlehorizons.erp.util.InputValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Shared console rendering and prompting helpers used by every view. */
public class ConsoleView {

    public void banner(String title, String subtitle) {
        String line = "=".repeat(54);
        System.out.println("\n" + line);
        System.out.println("  " + title);
        if (subtitle != null) {
            System.out.println("  " + subtitle);
        }
        System.out.println(line);
    }

    /** Prints a numbered menu (0 = back) and returns the chosen option. */
    public int menu(String title, String... options) {
        banner(title, "Escriba 'cancelar' en cualquier campo para abortar la operacion");
        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (i + 1) + ". " + options[i]);
        }
        System.out.println("  0. Volver");
        return InputValidator.readInt("\nSeleccione una opcion: ", 0, options.length);
    }

    public void success(String message) { System.out.println("\n  [OK] " + message); }

    public void error(String message) { System.out.println("\n  [ERROR] " + message); }

    public void info(String message) { System.out.println("  " + message); }

    public void cancelled() { System.out.println("\n  Operacion cancelada."); }

    public void pause() { InputValidator.pause(); }

    public void table(List<String> headers, List<List<String>> rows) {
        if (rows.isEmpty()) {
            System.out.println("\n  (sin resultados)");
            return;
        }
        int[] widths = new int[headers.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = headers.get(i).length();
        }
        for (List<String> row : rows) {
            for (int i = 0; i < widths.length; i++) {
                widths[i] = Math.max(widths[i], row.get(i).length());
            }
        }
        System.out.println();
        printRow(headers, widths);
        System.out.println("  " + "-".repeat(Arrays.stream(widths).sum() + 3 * (widths.length - 1)));
        rows.forEach(r -> printRow(r, widths));
        System.out.println("\n  " + rows.size() + " registro(s).");
    }

    private void printRow(List<String> cells, int[] widths) {
        StringBuilder sb = new StringBuilder("  ");
        for (int i = 0; i < widths.length; i++) {
            sb.append(String.format("%-" + widths[i] + "s", cells.get(i)));
            if (i < widths.length - 1) {
                sb.append(" | ");
            }
        }
        System.out.println(sb);
    }

    /** Asks for the four parts of a Colombian name; middle and second last name are optional. */
    protected PersonName promptName() {
        String first = InputValidator.readText("Primer nombre: ", 100);
        String middle = InputValidator.readOptionalText("Segundo nombre (ENTER si no tiene): ", 100);
        String last = InputValidator.readText("Primer apellido: ", 100);
        String secondLast = InputValidator.readOptionalText("Segundo apellido (ENTER si no tiene): ", 100);
        return new PersonName(first, middle, last, secondLast);
    }

    protected static List<String> row(Object... cells) {
        List<String> list = new ArrayList<>();
        for (Object cell : cells) {
            list.add(cell == null ? "-" : cell.toString());
        }
        return list;
    }

    protected static String money(BigDecimal value) {
        return String.format(Locale.US, "$%,.2f", value);
    }
}
