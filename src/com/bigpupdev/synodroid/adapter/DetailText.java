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
package com.bigpupdev.synodroid.adapter;

/**
 * A detail with a simple text value
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
@SuppressWarnings("serial")
public class DetailText extends Detail {

	// The value of this detail
	private String value;

	/**
	 * Default constructor
	 */
	public DetailText() {
	}

	/**
	 * Constructor which initialize the name
	 * 
	 * @param nameP
	 * @param valueP
	 */
	public DetailText(String nameP) {
		setName(nameP);
	}

	/**
	 * Constructor which initialize the name/value
	 * 
	 * @param nameP
	 * @param valueP
	 */
	public DetailText(String nameP, String valueP) {
		setName(nameP);
		value = valueP;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		if (value != null) {
			return value;
		}
		return "";
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
