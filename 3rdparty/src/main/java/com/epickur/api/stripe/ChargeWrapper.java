package com.epickur.api.stripe;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import lombok.NonNull;

import java.util.Map;

public class ChargeWrapper {

	public Charge createCharge(@NonNull final Map<String, Object> properties) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
		return Charge.create(properties);
	}
}
