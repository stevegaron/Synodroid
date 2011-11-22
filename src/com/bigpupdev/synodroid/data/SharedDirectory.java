/**
 * 
 */
package com.bigpupdev.synodroid.data;

/**
 * A container for a shared directory
 * 
 * @author Eric Taix
 */
public class SharedDirectory {

	// The public name of the directory
	public String name;
	// The description of the directory
	public String description;
	// Is this directory is the current global shared directory ?
	public boolean isCurrent = false;

	/**
	 * Constructor with define the public name
	 */
	public SharedDirectory(String nameP) {
		name = nameP;
	}

	/**
	 * A constructor which intialize all parameters
	 * 
	 * @param nameP
	 * @param descriptionP
	 */
	public SharedDirectory(String nameP, String descriptionP) {
		name = nameP;
		description = descriptionP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SharedDirectory other = (SharedDirectory) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
