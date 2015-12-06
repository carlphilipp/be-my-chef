package com.epickur.api.aop;

import com.epickur.api.enumeration.EndpointType;
import com.epickur.api.enumeration.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ValidateRequest {

	Operation operation();

	EndpointType endpoint();
}
