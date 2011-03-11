package com.myapp.games.schnellen;

import java.lang.reflect.*;

class Util {
	
	static String fromR(int value) {
		try {
			return searchRecursively(R.class, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("error while lookup constant in R: ", e);
		}
	}
	
	private static String searchRecursively(Class<?> c, final int v) 
												throws IllegalAccessException {
		Field[] fields = c.getFields();
		
		for (Field f : fields) {
			int modifiers = f.getModifiers();
			
			if (f.getType() == int.class 
					&& Modifier.isPublic(modifiers) 
					&& Modifier.isStatic(modifiers)) {
				Integer fieldValue = (Integer) f.get(null);
				if (fieldValue == null) continue;
				if (fieldValue.equals(v)) return f.getName();
			}
		}
		
		Class<?>[] classes = c.getClasses();

		for (Class<?> clazz : classes) {
			int modifiers = clazz.getModifiers();
			
			if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
				String fromSubclass = searchRecursively(clazz, v);
				if (fromSubclass != null) return fromSubclass;
			}
		}
		
		return null;
	}
}
