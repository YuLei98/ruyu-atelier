package icu.ruiyu.framework.log.aspect;

import com.alibaba.fastjson.JSON;
import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Component
@EnableAspectJAutoProxy
@Aspect
@Log
public class RecordRequestAndResponseAspect {

    @Pointcut("@annotation(icu.ruiyu.framework.log.annotation.RecordRequestAndResponse)")
    public void LogPointcut() {}

    @Around("LogPointcut()")
    public Object recordRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.out.println(joinPoint.getSignature());
//        System.out.println(joinPoint.getKind());
//        System.out.println(joinPoint.getArgs());
//        System.out.println(joinPoint.getTarget());
//        System.out.println(joinPoint.getThis());
//        System.out.println(joinPoint.getSourceLocation());
//        System.out.println(joinPoint.getStaticPart());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            System.out.println("arg = " + arg);
        }

        Object[] newArgs = {"newArgs"};


        Object response = joinPoint.proceed(newArgs);
        log.info("request: {}, response: {}" + JSON.toJSONString(args)+JSON.toJSONString(response));
        return response;

    }
}
