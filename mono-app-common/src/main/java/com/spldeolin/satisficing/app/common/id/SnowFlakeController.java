package com.spldeolin.satisficing.app.common.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spldeolin.satisficing.api.common.javabean.RequestResult;

/**
 * @author Deolin 2023-07-03
 */
@RestController
public class SnowFlakeController {

    @Autowired
    private SnowFlake snowFlake;

    /**
     * 用于自检本服务当前部署节点的Snow Flake worker ID
     */
    @PostMapping("/getWorkerId")
    public RequestResult<Long> getWorkerId() {
        return RequestResult.success(snowFlake.getWorkerId());
    }

}