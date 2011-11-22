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
 * A detail with 2 progress bar
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
@SuppressWarnings("serial")
public class Detail2Progress extends Detail {

	// The first progress label
	private String label1;
	// The first progress value
	private int value1;
	// The second progress label
	private String label2;
	// The second progress value
	private int value2;

	/**
	 * The default constructor
	 */
	public Detail2Progress() {
	}

	/**
	 * The constructor which define the name of this detail
	 * 
	 * @param nameP
	 */
	public Detail2Progress(String nameP) {
		setName(nameP);
	}

	/**
	 * Set the first progress bar (label and value)
	 * 
	 * @param labelP
	 * @param valueP
	 */
	public void setProgress1(String labelP, int valueP) {
		label1 = labelP;
		value1 = valueP;
	}

	/**
	 * Set the second progress bar (label and value)
	 * 
	 * @param labelP
	 * @param valueP
	 */
	public void setProgress2(String labelP, int valueP) {
		label2 = labelP;
		value2 = valueP;
	}

	/**
	 * @return the label1
	 */
	public String getLabel1() {
		return label1;
	}

	/**
	 * @return the value1
	 */
	public int getValue1() {
		return value1;
	}

	/**
	 * @return the label2
	 */
	public String getLabel2() {
		return label2;
	}

	/**
	 * @return the value2
	 */
	public int getValue2() {
		return value2;
	}

}
