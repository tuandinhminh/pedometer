package pedometer.momo.common.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import pedometer.momo.common.util.CommonUtils;
import pedometer.momo.dto.ResponseDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class LoggableAspect {
    private final HttpServletRequest httpServletRequest;
    
    @Pointcut("within(pedometer.momo.controller..*) " +
            "&& (@annotation(org.springframework.web.bind.annotation.GetMapping))" +
            "|| (@annotation(org.springframework.web.bind.annotation.PostMapping))" +
            "|| (@annotation(org.springframework.web.bind.annotation.PutMapping))" +
            "|| (@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void pointcut() {
        //no need to check
    }

    @Before("pointcut()")
    public void logMethod(JoinPoint joinPoint) {
        Optional<RequestMapping> mapping = getRequestMapping(joinPoint);
        if (mapping.isEmpty()) return;
        RequestMapping requestMapping = mapping.get();
        Map<String, Object> parameters = getParameters(joinPoint);
        log.info("==> path(s): {}, method(s): {}, arguments: {} ",
                getFullURL(httpServletRequest), requestMapping.method(), CommonUtils.stringify(parameters));
    }

    @AfterReturning(pointcut = "pointcut()", returning = "entity")
    public void logMethodAfter(JoinPoint joinPoint, ResponseDTO<?> entity) {
        Optional<RequestMapping> mapping = getRequestMapping(joinPoint);
        if (mapping.isEmpty()) return;
        RequestMapping requestMapping = mapping.get();
        log.info("<== path(s): {}, method(s): {}, returning: {}",
                getFullURL(httpServletRequest), requestMapping.method(), CommonUtils.stringify(entity));
    }

    private Optional<RequestMapping> getRequestMapping(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return Arrays.stream(signature.getMethod().getAnnotations())
                .map(ano -> ano.annotationType().getAnnotation(RequestMapping.class))
                .filter(Objects::nonNull)
                .findAny();
    }

    public static String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();
        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    private Map<String, Object> getParameters(JoinPoint joinPoint) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        HashMap<String, Object> map = new HashMap<>();
        String[] parameterNames = signature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], joinPoint.getArgs()[i]);
        }
        return map;
    }
}
