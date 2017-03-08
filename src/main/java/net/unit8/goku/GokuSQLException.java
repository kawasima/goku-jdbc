package net.unit8.goku;

import java.sql.SQLException;
import java.util.Locale;

/**
 * @author kawasima
 */
public class GokuSQLException extends SQLException {
    public GokuSQLException(SQLException orig) {
        super(orig.getMessage(), orig.getSQLState(), orig.getErrorCode());
    }

    @Override
    public String getMessage() {
        int errorCode = getErrorCode();
        if (errorCode > 0) {
            return String.format(Locale.JAPAN, "オッス ORA-%05d", getErrorCode());
        } else {
            return getMessage();
        }
    }
}
