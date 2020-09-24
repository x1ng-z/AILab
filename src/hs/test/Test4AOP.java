package hs.test;

import hs.Bean.ModlePin;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/22 15:47
 */
//
//@Component
//@Aspect
public class Test4AOP {

    @Pointcut("execution(public void hs.test.Test4filter.update(..))&&target(test4filter)&&args(value)")
    public void filterpointcut(double value, Test4filter test4filter) {

    }

    @AfterReturning(
            pointcut = "filterpointcut(value,test4filter)",
            returning = "retVal")
    public void doAccessCheck(double value, Test4filter test4filter, Object retVal) {
        System.out.println("in AfterReturning,the value="+value);

        System.out.println("in AfterReturning,class="+test4filter.getClass());

        System.out.println("in AfterReturning,the hashcode="+test4filter.hashCode());
    }

}
