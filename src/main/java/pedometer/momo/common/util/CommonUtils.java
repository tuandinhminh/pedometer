package pedometer.momo.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CommonUtils {

    private static ObjectMapper mapper;

    private CommonUtils() {
        initialize();
    }

    private static void initialize() {
        if (mapper != null) {
            return;
        }
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    public static String stringify(Object data) {
        initialize();
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("EXCEPTION WHEN PARSE OBJECT TO STRING {}", e.getMessage());
        }
        return null;
    }
}
