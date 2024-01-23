package com.spldeolin.satisficing.api.common;

/**
 * errorCode接口，业务errorCode枚举需要继承这个接口
 *
 * @author wangl 2023-03-03
 */
public interface ErrorCodeAncestor {

    String getResultCode();

    String getErrMsg();

}
