package info.yalamanchili.office.service;

import info.yalamanchili.jpa.AbstractEntity;
import info.yalamanchili.office.service.exception.ServiceException;
import info.yalamanchili.office.service.exception.ServiceException.StatusCode;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * this interceptor will handle invocation validates the input params to process
 * the valdiation errors to aggregate them(ServiceMessages), also handle other
 * errors during invocation and captures them and delegates them to
 * corresponding exception mapper (ServiceExceptionMapper)
 * 
 */
@Aspect
@Component
public class ServiceInterceptor {
	@Autowired
	protected ServiceMessages serviceMessages;

	@Around("execution(* info.yalamanchili.office.jrs..*.*(..))")
	public void aroundInvoke(ProceedingJoinPoint joinPoint) throws Throwable {
		for (Object arg : joinPoint.getArgs()) {
			if (arg instanceof AbstractEntity) {
				System.out.println("aaaaaaaaaaaaaaaaaaaaaaa abstract entity");

			}
		}
		try {
			joinPoint.proceed();
		} catch (Exception e) {
			throw new ServiceException(StatusCode.INTERNAL_SYSTEM_ERROR, e.getLocalizedMessage(), e.getMessage());
		}
	}
}
