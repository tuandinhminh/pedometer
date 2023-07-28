package pedometer.momo.common.util;

import pedometer.momo.dto.ResponseDTO;
import pedometer.momo.dto.Status;

public class ResponseMapper {

    public static <T> ResponseDTO<T> toResponseDto(T from) {
        return ResponseDTO.<T>builder()
                .data(from)
                .status(Status.ok())
                .build();
    }

    private ResponseMapper() {
    }
}
