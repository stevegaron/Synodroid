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
 * A detail with a progress bar
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
@SuppressWarnings("serial")
public class DetailProgress extends Detail {

	// The progress label
	private String label;
	// The progress value
	private int value;
	// The template resource id to use
	private int res;

	/**
	 * The default constructor
	 */
	public DetailProgress(int resP) {
		res = resP;
	}

	/**
	 * The constructor which define the name of this detail
	 * 
	 * @param nameP
	 */
	public DetailProgress(String nameP, int resP) {
		setName(nameP);
		res = resP;
	}

	/**
	 * @return the res
	 */
	public int getRes() {
		return res;
	}

	/**
	 * Set the first progress bar (label and value)
	 * 
	 * @param labelP
	 * @param valueP
	 */
	public void setProgress(String labelP, int valueP) {
		label = labelP;
		value = valueP;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
}
