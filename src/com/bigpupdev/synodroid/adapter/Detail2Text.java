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
public class Detail2Text extends Detail {

	// The value 1 of this detail
	private String value1;
	// The value 2 of this detail
	private String value2;

	/**
	 * Default constructor
	 */
	public Detail2Text() {
	}

	/**
	 * Constructor which initialize the name
	 * 
	 * @param nameP
	 * @param valueP
	 */
	public Detail2Text(String nameP) {
		setName(nameP);
	}

	/**
	 * Constructor which initialize the name/value
	 * 
	 * @param nameP
	 * @param valueP
	 */
	public Detail2Text(String nameP, String valueP) {
		setName(nameP);
		value1 = valueP;
	}

	/**
	 * @return the value
	 */
	public String getValue1() {
		if (value1 != null) {
			return value1;
		}
		return "";
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue1(String value) {
		this.value1 = value;
	}

	/**
	 * @return the value2
	 */
	public String getValue2() {
		return value2;
	}

	/**
	 * @param value2
	 *            the value2 to set
	 */
	public void setValue2(String value2) {
		this.value2 = value2;
	}

}
