package com.epickur.api.utils;

import org.bson.Transformer;

/**
 * Class that allow to serialize enum into MongoDB. The enum needs to be registred that way: 
 * BSON.addEncodingHook(DishType.class, new EnumTransformer());
 * 
 * @author cph
 *
 */
public final class EnumTransformer implements Transformer {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bson.Transformer#transform(java.lang.Object)
	 */
	public Object transform(final Object o) {
		return o.toString();
	}
}