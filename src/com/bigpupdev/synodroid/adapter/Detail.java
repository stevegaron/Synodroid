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

import java.io.Serializable;

/**
 * A detail which consist in a name/value pair
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public abstract class Detail implements Serializable {

	private static final long serialVersionUID = 1L;

	// The name of this detail
	private String name;
	// The detail action. By default null
	private DetailAction action = null;

	/**
	 * Default constructor
	 */
	public Detail() {
	}

	/**
	 * Constructor which initialize the name
	 * 
	 * @param nameP
	 * @param valueP
	 */
	public Detail(String nameP) {
		name = nameP;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the action
	 */
	public DetailAction getAction() {
		return action;
	}

	/**
	 * @param actionP
	 *            the action to set
	 */
	public void setAction(DetailAction actionP) {
		action = actionP;
	}

	/**
	 * Execute the action if it exist
	 */
	public void executeAction() {
		if (action != null) {
			action.execute(this);
		}
	}

}
