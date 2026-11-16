package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.exception.BusinessRuleException;
import com.littlehorizons.erp.exception.DataAccessException;
import com.littlehorizons.erp.exception.InputCancelledException;
import com.littlehorizons.erp.exception.InputClosedException;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.view.ConsoleView;

/** Common error handling for every controller action. */
public abstract class BaseController {

    protected final ConsoleView console = new ConsoleView();

    /** Runs one menu action, translating exceptions into clear messages and log entries. */
    protected void execute(Runnable action) {
        try {
            action.run();
        } catch (InputClosedException e) {
            throw e;
        } catch (InputCancelledException e) {
            console.cancelled();
        } catch (BusinessRuleException e) {
            console.error(e.getMessage());
        } catch (DataAccessException e) {
            AppLogger.error("Database error", e);
            console.error("Error de base de datos. Revise logs/error_log.txt para el detalle.");
        } catch (RuntimeException e) {
            AppLogger.error("Unexpected error", e);
            console.error("Error inesperado. Revise logs/error_log.txt para el detalle.");
        }
        console.pause();
    }
}
