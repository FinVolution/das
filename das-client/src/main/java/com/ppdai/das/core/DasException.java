package com.ppdai.das.core;

import java.sql.SQLException;

public class DasException extends SQLException {
    private static final long serialVersionUID = 1L;
    private ErrorCode errorCode;

    public DasException(String reason) {
        super(reason);
    }

    public DasException(String reason, Throwable e) {
        super(reason, e);
    }

    public DasException(ErrorCode error) {
        super(error.getMessage());
        this.errorCode = error;
    }

    public DasException(ErrorCode error, Throwable e) {
        super(error.getMessage() + e.getMessage(), e);
        this.errorCode = error;
    }

    public DasException(ErrorCode error, Object... args) {
        super(String.format(error.getMessage(), args));
        this.errorCode = error;
    }

    public DasException(ErrorCode error, Throwable e, Object... args) {
        super(String.format(error.getMessage(), args), e);
        this.errorCode = error;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode.getCode();
    }

    public static SQLException wrap(Throwable e) {
        return e instanceof SQLException ? (SQLException)e : e instanceof DasException ? (DasException) e
                : e.getCause() instanceof DasException ? (DasException) e.getCause()
                : new DasException(ErrorCode.Unknown, e, e.getMessage());
    }

    public static DasException wrap(ErrorCode defaultError, Throwable e) {
        return e instanceof DasException ? (DasException) e : new DasException(defaultError, e);
    }

    public static void handleError(String msg, Throwable e) throws SQLException {
        if(e == null)
            return;

        // Just make sure error is not swallowed by us
        DasConfigureFactory.getLogger().error(msg, e);
        throw wrap(e);
    }
}
