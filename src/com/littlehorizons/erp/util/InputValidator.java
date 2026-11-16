package com.littlehorizons.erp.util;

import com.littlehorizons.erp.exception.InputCancelledException;
import com.littlehorizons.erp.exception.InputClosedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console input helper. Every prompt loops until the value is valid.
 * Typing the cancel keyword aborts the current operation (InputCancelledException).
 */
public final class InputValidator {

    public static final String CANCEL_KEYWORD = "cancelar";
    private static final Scanner SCANNER = new Scanner(System.in);

    private InputValidator() { }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) {
            throw new InputClosedException();
        }
        String line = SCANNER.nextLine().trim();
        if (line.equalsIgnoreCase(CANCEL_KEYWORD)) {
            throw new InputCancelledException();
        }
        return line;
    }

    public static String readText(String prompt, int maxLength) {
        while (true) {
            String value = readLine(prompt);
            if (value.isEmpty()) {
                System.out.println("  [!] Este campo no puede estar vacio.");
            } else if (value.length() > maxLength) {
                System.out.println("  [!] Maximo " + maxLength + " caracteres.");
            } else {
                return value;
            }
        }
    }

    /** Optional text: empty input returns null. */
    public static String readOptionalText(String prompt, int maxLength) {
        while (true) {
            String value = readLine(prompt);
            if (value.isEmpty()) {
                return null;
            }
            if (value.length() <= maxLength) {
                return value;
            }
            System.out.println("  [!] Maximo " + maxLength + " caracteres.");
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readLine(prompt);
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                }
                System.out.println("  [!] Ingrese un numero entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Numero invalido.");
            }
        }
    }

    public static BigDecimal readDecimal(String prompt, BigDecimal min, BigDecimal max, int scale) {
        while (true) {
            String value = readLine(prompt).replace(',', '.');
            try {
                BigDecimal number = new BigDecimal(value);
                if (number.scale() > scale) {
                    System.out.println("  [!] Use como maximo " + scale + " decimal(es).");
                } else if (number.compareTo(min) < 0 || number.compareTo(max) > 0) {
                    System.out.println("  [!] Ingrese un valor entre " + min + " y " + max + ".");
                } else {
                    return number;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Numero invalido.");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            String value = readLine(prompt + " (yyyy-MM-dd): ");
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Fecha invalida. Ejemplo: 2026-03-15");
            }
        }
    }

    public static LocalTime readTime(String prompt) {
        while (true) {
            String value = readLine(prompt + " (HH:mm): ");
            try {
                return LocalTime.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Hora invalida. Ejemplo: 08:00");
            }
        }
    }

    public static String readEmail(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
                return value;
            }
            System.out.println("  [!] Correo electronico invalido.");
        }
    }

    public static String readPhone(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value.matches("^\\+?[0-9]{7,15}$")) {
                return value;
            }
            System.out.println("  [!] El telefono debe tener entre 7 y 15 digitos.");
        }
    }

    public static String readDocument(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value.matches("^[0-9]{5,15}$")) {
                return value;
            }
            System.out.println("  [!] El documento debe tener entre 5 y 15 digitos.");
        }
    }

    /** Lets the user pick one of the options by number; returns the chosen option text. */
    public static String readChoice(String prompt, List<String> options) {
        for (int i = 0; i < options.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + options.get(i));
        }
        int index = readInt(prompt, 1, options.size());
        return options.get(index - 1);
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt + " (s/n): ").toLowerCase();
            if (value.equals("s") || value.equals("si")) {
                return true;
            }
            if (value.equals("n") || value.equals("no")) {
                return false;
            }
            System.out.println("  [!] Responda s o n.");
        }
    }

    public static void pause() {
        System.out.print("\nPresione ENTER para continuar...");
        if (SCANNER.hasNextLine()) {
            SCANNER.nextLine();
        }
    }
}
