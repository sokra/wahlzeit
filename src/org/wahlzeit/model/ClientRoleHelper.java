package org.wahlzeit.model;


public class ClientRoleHelper {
	public static String getRoleName(ClientRole role) {
		String name = role.getClass().getSimpleName();
		name = name.substring(0, name.length() - "Role".length());
		name = name.substring(0, 1).toLowerCase() + name.substring(1);
		return name;
	}
	
	/**
	 * 
	 */
	public static ClientRole constructRoleFromName(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1) + "Role";
		name = ClientRoleHelper.class.getPackage().getName() + "." + name;
		try {
			Class<?> loadClass = ClientRoleHelper.class.getClassLoader().loadClass(name);
			if(!ClientRole.class.isAssignableFrom(loadClass))
				return null;
			@SuppressWarnings("unchecked")
			Class<? extends ClientRole> roleClass = (Class<? extends ClientRole>) loadClass;
			return roleClass.newInstance();
		} catch (ClassNotFoundException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}
}
