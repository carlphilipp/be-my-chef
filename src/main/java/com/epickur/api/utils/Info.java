/**
 * Copyright 2013 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epickur.api.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * This class get the general info from a property file
 * 
 * @author cph
 * @version 1.0
 */
public final class Info {

	/** Constructor **/
	private Info() {
	}

	/** Access in a static way to the property file **/
	static {
		Properties prop = Utils.getEpickurProperties();
		NAME = prop.getProperty("name");
		ADDRESS = prop.getProperty("address");
		FOLDER = prop.getProperty("folder");
		WEB_ADDRESS = prop.getProperty("epickur.web.address");
		admins = Collections.unmodifiableList(Arrays.asList(prop.getProperty("admins").split(";")));
	}

	/** Name of the webapp **/
	public static final String NAME;
	/** Current address of the webapp **/
	public static final String ADDRESS;
	/** Current folder after the address **/
	public static final String FOLDER;
	/** Web address **/
	public static final String WEB_ADDRESS;
	/** Admins **/
	public static List<String> admins;

}
