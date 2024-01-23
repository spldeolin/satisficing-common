package com.spldeolin.satisficing.api.common.javabean;

import com.spldeolin.satisficing.api.common.ErrorCodeAncestor;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求结果
 *
 * @author Deolin 2023-04-13
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public final class RequestResult<T> {

    /**
     * 代表请求是否成功
     */
    Boolean flag;

    /**
     * 结果码
     * <p>
     * C0成功；C1外部错误；C2未认证；C3未授权；C4 Not Found；C5内部错误；C6业务逻辑异常
     */
    String errorCode;

    /**
     * 请求成功的返回数据
     * <p>
     * 有些API没有返回数据，但errorCode非C0时data必定为null
     */
    T data;

    /**
     * 错误信息
     * <pre>
     * errorCode为C1时，若是参数校验未通过引起的，会有结构化的校验结果信息，需要调用方先行确保参数均符合本API的校验项再行请求；
     * errorCode为C5、C6时，会有具体的、用户友好的错误信息；
     * 其他情况无需errorMsg
     * </pre>
     */
    String errorMsg;

    private RequestResult() {
    }

    public static <T> RequestResult<T> success() {
        return success(null);
    }

    public static <T> RequestResult<T> success(T data) {
        RequestResult<T> instance = new RequestResult<>();
        instance.setFlag(true);
        instance.setErrorCode("C0");
        instance.setData(data);
        return instance;
    }

    public static <T> RequestResult<T> failure(ErrorCodeAncestor errorCode) {
        if ("C0".equals(errorCode.getResultCode())) {
            log.warn("errorCode不应是C0");
        }
        RequestResult<T> instance = new RequestResult<>();
        instance.setFlag(false);
        instance.setErrorCode(errorCode.getResultCode());
        instance.setErrorMsg(errorCode.getErrMsg());
        return instance;
    }

}
