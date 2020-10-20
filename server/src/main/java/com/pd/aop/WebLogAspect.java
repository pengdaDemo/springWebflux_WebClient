package com.pd.aop;

import com.pd.target.MyAnnotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class WebLogAspect {
    private final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    /**
     * 日志管理
     */
    @Pointcut("execution(public * com.pd.controller..*.*(..))")//切入点描述 这个是controller包的切入点
    public void controllerLog() {};
    @Pointcut("execution(public * com.pd.uiController..*.*(..))")//切入点描述，这个是uiController包的切入点
    public void uiControllerLog(){}
    @Before("controllerLog() || uiControllerLog()")//在切入点的方法run之前要干的
    public void logBeforeController(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        // 记录下请求内容
        logger.info("################URL : " + request.getRequestURL().toString());
        logger.info("################HTTP_METHOD : " + request.getMethod());
        logger.info("################IP : " + request.getRemoteAddr());
        logger.info("################THE ARGS OF THE CONTROLLER : " + Arrays.toString(joinPoint.getArgs()));

        //下面这个getSignature().getDeclaringTypeName()是获取包+类名的   然后后面的joinPoint.getSignature.getName()获取了方法名
        logger.info("################CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        //方法参数
        logger.info("################ARGS : " + Arrays.toString(joinPoint.getArgs()));
        //logger.info("################TARGET: " + joinPoint.getTarget());//返回的是需要加强的目标类的对象
        //logger.info("################THIS: " + joinPoint.getThis());//返回的是经过加强后的代理类的对象
    }

    //定义增强，pointcut连接点使用@annotation（xxx）进行定义
    @Around(value = "@annotation(around)") //around 与 下面参数名around对应
    public Object processAuthority(ProceedingJoinPoint point, MyAnnotation around) throws Throwable{
        System.out.println("ANNOTATION welcome");
        System.out.println("ANNOTATION 调用方法："+ around.methodName());
        System.out.println("ANNOTATION 调用类：" + point.getSignature().getDeclaringTypeName());
        System.out.println("ANNOTATION 调用类名" + point.getSignature().getDeclaringType().getSimpleName());
        System.out.println("ANNOTATION 调用参数" + Arrays.toString(point.getArgs()));
        Object o = point.proceed(); //调用目标方法
        System.out.println("ANNOTATION login success");
        return o;
    }

    //后置异常通知
    @AfterThrowing(throwing = "ex", pointcut = "controllerLog()")
    public void throwss(JoinPoint jp, Exception ex){
        System.out.println("方法异常时执行....."+ex.getMessage());
    }
}
