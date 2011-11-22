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
package com.bigpupdev.synodroid.protocol;

import java.net.URLEncoder;

/**
 * Utility class which help to create a query string
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class QueryBuilder {

	private static final String SEPARATOR = "&";
	// The final query
	private StringBuilder query = new StringBuilder();

	/**
	 * Constructor
	 */
	public QueryBuilder() {
	}

	/**
	 * Add a new key / value pair
	 * 
	 * @param keyP
	 * @param valueP
	 * @return
	 */
	public QueryBuilder add(String keyP, String valueP) {
		// If not empty then add the separator
		if (query.length() != 0) {
			query.append(SEPARATOR);
		}
		// Append the parameter
		String val = URLEncoder.encode(valueP);
		String key = URLEncoder.encode(keyP);
		query.append(key).append("=").append(val);
		return this;
	}

	/**
	 * Return the final query
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return query.toString();
	}
}
