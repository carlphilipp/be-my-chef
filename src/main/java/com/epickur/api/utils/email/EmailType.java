package com.epickur.api.utils.email;

/**
 * This enum represents the type of email we want to send.
 * 
 * @author cph
 *
 */
public enum EmailType {

	// Registration
	/** Email sent to the user on new registration **/
	REGISTRATION_USER,
	/** Email sent to the admins on new registration **/
	REGISTRATION_ADMIN,

	// Order
	/** Email sent to the user when new order **/
	ORDER_USER_NEW,
	/** Email sent to the admins when new order **/
	ORDER_ADMINS_NEW,
	/** Email sent to the caterer when new order **/
	ORDER_CATERER_NEW,

	/** Email sent to the user when the caterer declined the order **/
	ORDER_USER_DECLINED,
	/** Email sent to the admins when a caterer declined an order **/
	ORDER_ADMINS_DECLINED,

	/** Email sent to the user when the caterer accepted the order and when he has been charged **/
	ORDER_USER_SUCCESS,
	/** Email sent to the caterer when the user has been charged **/
	ORDER_CATERER_SUCCESS,
	/** Email sent to the admins when the caterer accepted the order and when he has been charged **/
	ORDER_ADMINS_SUCCESS,

	/** Email sent to the user when the caterer accepted the order but the charge failed **/
	ORDER_USER_FAIL,
	/** Email sent to the caterer when the charge has failed **/
	ORDER_CATERER_FAIL,
	/** Email sent to the admins when the caterer accepted the order but the charge failed **/
	ORDER_ADMINS_FAIL,
	
	/** Email sent to the user when the caterer did not accept on time the order **/
	ORDER_USER_CANCEL,
	/** Email sent to the caterer when the caterer did not accept on time the order **/
	ORDER_CATERER_CANCEL,
	/** Email sent to the admins when the caterer did not accept on time the order **/
	ORDER_ADMINS_CANCEL,
	
	// Reset password
	/** Email sent to the user to reset its password **/
	RESET_USER_PASSWORD

}
