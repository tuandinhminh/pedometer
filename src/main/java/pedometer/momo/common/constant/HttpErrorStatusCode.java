package pedometer.momo.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpErrorStatusCode {
    public static final String PARAMS_VALIDATION_ERROR = "ERROR_001";
    public static final String INTERNAL_SERVER_ERROR = "ERROR_002";
}
