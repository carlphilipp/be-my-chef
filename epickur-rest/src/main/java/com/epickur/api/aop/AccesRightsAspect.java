package com.epickur.api.aop;

import com.epickur.api.dao.mongo.CatererDAO;
import com.epickur.api.validator.CatererValidator;
import com.epickur.api.validator.DishValidator;
import com.epickur.api.validator.UserValidator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public abstract class AccesRightsAspect {

	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected UserValidator userValidator;
	@Autowired
	protected DishValidator dishValidator;
	@Autowired
	protected CatererValidator catererValidator;
	@Autowired
	protected CatererDAO catererDAO;

	protected Method getMethodFromJointPoint(final JoinPoint joinPoint) {
		final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		return signature.getMethod();
	}
}
