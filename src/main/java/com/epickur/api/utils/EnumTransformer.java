package com.epickur.api.utils;

import org.bson.Transformer;

/**
 * Class that allow to serialize enum into MongoDB. The enum needs to be registred that way: BSON.addEncodingHook(DishType.class, new
 * EnumTransformer());
 * 
 * @author cph
 *
 */
public final class EnumTransformer implements Transformer {

	@Override
	public Object transform(final Object o) {
		return o.toString();
	}
}