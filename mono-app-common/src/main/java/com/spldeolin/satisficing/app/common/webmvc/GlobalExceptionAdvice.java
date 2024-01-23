package com.spldeolin.satisficing.app.common.webmvc;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.spldeolin.satisficing.api.common.ErrorCodeAncestor;
import com.spldeolin.satisficing.api.common.javabean.RequestResult;
import com.spldeolin.satisficing.app.common.exception.AuthException;
import com.spldeolin.satisficing.app.common.exception.AuthcException;
import com.spldeolin.satisficing.app.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于统一异常处理的ControllerAdvice
 *
 * @author Deolin 2023-04-13
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionAdvice {

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * C4 Not Found
     * 只有spring.mvc.throw-exception-if-no-handler-found=true和spring.resources.add-mappings=false时，才会抛出这个异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public RequestResult<?> handler(NoHandlerFoundException e) {
        log.warn(e.getMessage());
        httpServletResponse.addHeader("X-Trace-Id", MDC.get("traceId"));
        httpServletResponse.addHeader("X-Span-Id", MDC.get("spanId"));
        return RequestResult.failure(new ErrorCodeAncestor() {
            @Override
            public String getResultCode() {
                return "C4";
            }

            @Override
            public String getErrMsg() {
                return null;
            }
        });
    }

    /**
     * C1外部错误：请求动词错误
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public RequestResult<?> handler(HttpRequestMethodNotSupportedException e) {
        String supportedMethods = Arrays.toString(e.getSupportedMethods());
        log.warn(e.getMessage() + " " + supportedMethods);
        httpServletResponse.addHeader("X-Trace-Id", MDC.get("traceId"));
        httpServletResponse.addHeader("X-Span-Id", MDC.get("spanId"));
        return RequestResult.failure(new ErrorCodeAncestor() {
            @Override
            public String getResultCode() {
                return "C1";
            }

            @Override
            public String getErrMsg() {
                return null;
            }
        });
    }

    /**
     * C1外部错误：请求Content-Type错误。往往是因为后端的@RequestBody和前端的application/json没有同时指定或同时不指定导致的
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public RequestResult<?> handler(HttpMediaTypeNotSupportedException e) {
        log.warn(e.getMessage() + " " + " [application/json]");
        httpServletResponse.addHeader("X-Trace-Id", MDC.get("traceId"));
        httpServletResponse.addHeader("X-Span-Id", MDC.get("spanId"));
        return RequestResult.failure(new ErrorCodeAncestor() {
            @Override
            public String getResultCode() {
                return "C1";
            }

            @Override
            public String getErrMsg() {
                return null;
            }
        });
    }

    /**
     * C1外部错误：请求Body格式错误
     * <pre>
     * 以下情况时，会被捕获：
     * alkdjfaldfjlalkajkdklf               可能是因为Body不是合法的JSON格式
     * (空)                                 JSON不存在
     * {"userAge"="notNumberValue"}         JSON中字段类型错误
     * </pre>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public RequestResult<?> httpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("message={}", e.getMessage());
        httpServletResponse.addHeader("X-Trace-Id", MDC.get("traceId"));
        httpServletResponse.addHeader("X-Span-Id", MDC.get("spanId"));
        return RequestResult.failure(new ErrorCodeAncestor() {
            @Override
            public String getResultCode() {
                return "C1";
            }

            @Override
            public String getErrMsg() {
                return null;
            }
        });
    }

    /**
     * C1外部错误：请求Body内字段没有通过注解校验（通过参数级@Valid 启用的参数校验）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RequestResult<?> handle(MethodArgumentNotValidException e) {
        Collection<InvalidDto> invalids = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new InvalidDto().setPath(error.getField()).setValue(error.getRejectedValue())
                        .setReason(error.getDefaultMessage())).collect(Collectors.toList());
        log.warn("invalids={}", invalids);
        httpServletResponse.addHeader("X-Trace-Id", MDC.get("traceId"));
        httpServletResponse.addHeader("X-Span-Id", MDC.get("spanId"));
        return RequestResult.failure(new ErrorCodeAncestor() {
            @Override
            public String getResultCode() {
                return "C1";
            }

            @Override
            public String getErrMsg() {
                return invalids.toString();
            }
        });
    }

    /**
     * C2未认证
     */
    @ExceptionHandler(AuthcException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 依然指定了Http Code是因为兼容前端
    public RequestResult<?> handle(AuthcException e) {
        httpServletResponse.addHeader("X-Trace-Id", MDC.get("traceId"));
        httpServletResponse.addHeader("X-Span-Id", MDC.get("spanId"));
        return RequestResult.failure(new ErrorCodeAncestor() {
            @Override
            public String getResultCode() {
                return e.getCode();
            }

            @Override
            public String getErrMsg() {
                return e.getMessage();
            }
        });
    }

    /**
     * C3未授权
     */
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 依然指定了Http Code是因为兼容前端
    public RequestResult<?> handle(AuthException e) {
        httpServletResponse.addHeader("X-Trace-Id", MDC.get("traceId"));
        httpServletResponse.addHeader("X-Span-Id", MDC.get("spanId"));
        return RequestResult.failure(new ErrorCodeAncestor() {
            @Override
            public String getResultCode() {
                return "C3";
            }

            @Override
            public String getErrMsg() {
                return "没有权限";
            }
        });
    }

    /**
     * C6业务逻辑异常
     */
    @ExceptionHandler(BizException.class)
    public RequestResult<?> handle(BizException e) {
        httpServletResponse.addHeader("X-Trace-Id", MDC.get("traceId"));
        httpServletResponse.addHeader("X-Span-Id", MDC.get("spanId"));
        return RequestResult.failure(new ErrorCodeAncestor() {
            @Override
            public String getResultCode() {
                return e.getCode();
            }

            @Override
            public String getErrMsg() {
                return e.getMessage();
            }
        });
    }

    /**
     * C5内部错误
     */
    @ExceptionHandler(Throwable.class)
    public RequestResult<?> handle(Throwable e) {
        String traceId = MDC.get("traceId");
        httpServletResponse.addHeader("X-Trace-Id", traceId);
        httpServletResponse.addHeader("X-Span-Id", MDC.get("spanId"));
        log.error("AgentHR_Log_Reminder! 系统错误", e);

        return RequestResult.failure(new ErrorCodeAncestor() {
            @Override
            public String getResultCode() {
                return "C5";
            }

            @Override
            public String getErrMsg() {
                return String.format("服务异常，请稍后重试（错误代码：%s）", MDC.get("traceId"));
            }
        });
    }

}