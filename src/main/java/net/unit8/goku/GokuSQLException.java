package net.unit8.goku;

import java.sql.SQLException;
import java.util.Locale;

/**
 * @author kawasima
 */
public class GokuSQLException extends SQLException {
    private static final String ORA600 = "おめぇ〜つえ〜な〜、おらわくわくしてきたぞ";
    public GokuSQLException(SQLException orig) {
        super(orig.getMessage(), orig.getSQLState(), orig.getErrorCode(), orig);
    }

    private String convertToGokuDialect(String s, String sqlState) {
        if (!sqlState.equals("99999")) {
            s = s.replaceAll("^ORA-\\d{5}: ", "");
        }
        return s.replaceAll("です", "だぞ")
                .replaceAll("います", "っぞ")
                .replaceAll("あります", "あっぞ")
                .replaceAll("(あり)?ません", "ねーぞ")
                .replaceAll("ました", "たぞ");
    }

    @Override
    public String getMessage() {
        int errorCode = getErrorCode();
        if (errorCode > 0) {
            return String.format(Locale.JAPAN, "オッス ORA-%05d %s", errorCode,
                    errorCode == 600 ? ORA600 : convertToGokuDialect(super.getMessage(), getSQLState()));
        } else {
            return super.getMessage();
        }
    }
}
