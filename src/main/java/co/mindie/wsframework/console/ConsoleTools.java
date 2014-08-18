/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.console
// Tools.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 8, 2014 at 10:33:36 AM
////////

package co.mindie.wsframework.console;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.corsin.javatools.string.Strings;
import co.mindie.wsframework.component.WSComponent;
import co.mindie.wsframework.exception.WSFrameworkException;

public class ConsoleTools {

	////////////////////////
	// VARIABLES
	////////////////

	protected InterpreterProtocol protocol;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ConsoleTools(InterpreterProtocol protocol) {
		this.protocol = protocol;
	}

	////////////////////////
	// METHODS
	////////////////

	protected void print(String message) {
		this.protocol.getConnection().send(message);
	}

	protected void println(String message) {
		this.protocol.getConnection().send(message + "\n");
	}

	protected void println() {
		this.println("");
	}

	////////////////////////
	// INNER CLASSES
	////////////////

	public static class Class extends ConsoleTools {

		public Class(InterpreterProtocol protocol) {
			super(protocol);
		}

		public String showMethods(Object obj) {
			StringBuilder sb = new StringBuilder();
			Method[] methods = obj.getClass().getMethods();

			for (Method method : methods) {
				if (method.getDeclaringClass() != Object.class) {
					sb.append(method.toString());
					sb.append("\n");
				}
			}

			return sb.toString();
		}

		public java.lang.Class<?> forName(String name) {
			try {
				return java.lang.Class.forName(name);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static class Collection extends ConsoleTools  {

		public Collection(InterpreterProtocol protocol) {
			super(protocol);
		}

		public Object get(Object[] items, int idx) {
			return items[idx];
		}

		public Object get(List<?> list, int idx) {
			return list.get(idx);
		}

		public Object get(Map<?, ?> map, Object key) {
			return map.get(key);
		}

	}

	public static class Help extends ConsoleTools {

		private boolean printingHelp;

		public Help(InterpreterProtocol protocol) {
			super(protocol);
		}

		@Override
		public String toString() {
			if (this.printingHelp) {
				return "Your helper :)";
			}

			this.printingHelp = true;
			String str = Strings.format(""
					+ "Here are the values that lie in your context:\n"
					+ "[#0.keySet->key:"
					+ "\t\"{key}\" of value \"{#0.get(key)}\"\n"
					+ "]"
					+ "", this.protocol.getContext().getContextMap());

			this.printingHelp = false;

			return str;
		}

	}

	public static class Component extends ConsoleTools {

		public Component(InterpreterProtocol protocol) {
			super(protocol);
		}

		public Object find(Object obj, String componentKind) {
			if (obj instanceof WSComponent) {
				WSComponent component = (WSComponent) obj;
				List<Object> objs = new ArrayList<>();
				for (Object childComponent : component.getComponentContext().getComponents()) {
					if (childComponent.getClass().getSimpleName().contains(componentKind)) {
						objs.add(childComponent);
					}
				}

				if (objs.size() == 0) {
					return null;
				}
				if (objs.size() == 1) {
					return objs.get(0);
				}
				return objs;
			} else {
				throw new WSFrameworkException("This object " + obj + " needs to be a WSComponent in order to use findComponent on it.");
			}
		}

		public Object find(Object obj, java.lang.Class<?> componentClass) {
			if (obj instanceof WSComponent) {
				WSComponent component = (WSComponent) obj;
				return component.getComponentContext().findComponent(componentClass);
			} else {
				throw new WSFrameworkException("This object " + obj + " needs to be a WSComponent in order to use findComponent on it.");
			}
		}

		public Object show(Object obj) {
			if (obj instanceof WSComponent) {
				WSComponent component = (WSComponent) obj;
				return component.getComponentContext().getComponents();
			} else {
				throw new WSFrameworkException("This object " + obj + " needs to be a WSComponent in order to use findComponent on it.");
			}
		}

	}
}
