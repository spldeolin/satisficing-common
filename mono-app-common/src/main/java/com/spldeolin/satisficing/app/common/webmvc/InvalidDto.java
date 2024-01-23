package com.spldeolin.satisficing.app.common.webmvc;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * （控制层）校验未通过的信息
 *
 * @author Deolin 2023-04-13
 */
@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidDto {

     String path;

     Object value;

     String reason;

}
