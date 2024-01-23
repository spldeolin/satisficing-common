package com.spldeolin.satisficing.app.common.lock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.spldeolin.satisficing.app.common.lock.StdLock;

/**
 * 方法级标准分布式锁（StdLockM, Standard distributed Lock for Method）
 * <pre>
 * 用于声明在方法上，为整个方法加锁，所搭配的切面内部对锁的操作由{@link StdLock}实现
 *
 * 锁的key格式为 lock:{constantPart}:{variablePart}
 * {constantPart}由注解的keyConstantPart属性指定
 * {variablePart}由注解的keyVariablePart属性指定的构造策略，构造而成
 *
 * 特别注意：
 * - 请确保被声明方法运行时不会发生「永远结束不了」的情况，一旦发生，将会造成死锁
 * - 支持与事务注解同时生效
 * </pre>
 *
 * @author Deolin 2021-12-11
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StdLockM {

    /**
     * 锁的key，支持SpEL表达式，支持在SpEL表达式中通过参数名访问到所声明方法的实际参数
     */
    String key();

    /**
     * 等待锁的最大秒数，小于等于0时代表不等待
     */
    int waitSeconds() default 0;

}
