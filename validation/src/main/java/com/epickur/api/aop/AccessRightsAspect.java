package com.epickur.api.aop;

import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.validation.CatererValidation;
import com.epickur.api.validation.DishValidation;
import com.epickur.api.validation.UserValidation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public abstract class AccessRightsAspect {

	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected UserValidation userValidation;
	@Autowired
	protected DishValidation dishValidation;
	@Autowired
	protected CatererValidation catererValidation;
	@Autowired
	protected CatererDAO catererDAO;

	protected Method getMethodFromJointPoint(final JoinPoint joinPoint) {
		final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		return signature.getMethod();
	}
}
