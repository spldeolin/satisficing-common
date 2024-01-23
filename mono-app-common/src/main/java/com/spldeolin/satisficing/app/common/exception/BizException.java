package com.spldeolin.satisficing.app.common.exception;


import com.spldeolin.satisficing.api.common.ErrorCodeAncestor;

/**
 * 业务异常
 *
 * @author Deolin 2023-04-13
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = -4104806330438981374L;

    private String code;

    public BizException(String errMsg) {
        super(errMsg);
        this.code = "C6";
    }

    public BizException(ErrorCodeAncestor errorCode) {
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