package com.spldeolin.satisficing.app.common.exception;

import com.spldeolin.satisficing.api.common.ErrorCodeAncestor;

/**
 * 这个异常代表Non Authentication（未认证）
 * <p>
 * 抛出时机由各security模块实现
 *
 * @author Deolin 2023-04-20
 */
public class AuthcException extends RuntimeException {

    private static final long serialVersionUID = -7261591441649526637L;

    private String code;

    public AuthcException(ErrorCodeAncestor errorCode) {
        super(errorCode.getErrMsg());
        this.code = errorCode.getResultCode();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}