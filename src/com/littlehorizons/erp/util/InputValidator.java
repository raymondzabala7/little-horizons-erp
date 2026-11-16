package com.littlehorizons.erp.util;

import com.littlehorizons.erp.exception.InputCancelledException;
import java.util.Scanner;

public class InputValidator {
    private static final Scanner scanner = new Scanner(System.in);

    public static String getString(String prompt) throws InputCancelledException {
        System.out.print(prompt + " (escribe 'cancel' para salir): ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("cancel")) {
            throw new InputCancelledException("Operación cancelada por el usuario.");
        }
        return input;
    }

    public static int getInt(String prompt) throws InputCancelledException {
        while (true) {
            String val = getString(prompt);
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número entero válido.");
            }
        }
    }
}
