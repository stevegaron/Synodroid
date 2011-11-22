/**
 * Copyright 2010 Eric Taix
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 */
package com.bigpupdev.synodroid.preference;

import java.util.Properties;

/**
 * A simple interface which is able to do something when a server is loaded from the SharedPreference. The intend of this interface is to reuse the loading preference algorythme but without any coupling to action on the server.
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public interface PreferenceProcessor {

	/**
	 * Process the current server read from the SharedPreference
	 * 
	 * @param idP
	 *            The unique ID of the current read server
	 * @param keyP
	 *            The unique key used in the shared preferences
	 * @param propretiesP
	 *            The properties of the current server
	 * @param summaryP
	 * @return TRUE if the loop must be break: it is the case when a server connection is found in a WLAN
	 */
	public void process(int idP, String keyP, Properties propertiesP);
}
