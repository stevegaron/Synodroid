package com.bigpupdev.synodroid.data;

import java.util.List;

public class SharedFolderSelection {
	// The public name of the directory
	public String name;
	// The public name of the directory
	public List<Folder> childrens;

	/**
	 * A constructor which intialize all parameters
	 * 
	 * @param idP
	 * @param nameP
	 */
	public SharedFolderSelection(String cur, List<Folder> childs) {
		name = cur;
		childrens = childs;
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

