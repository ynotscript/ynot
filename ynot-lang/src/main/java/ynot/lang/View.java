package ynot.lang;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ynot.util.reflect.ReflectionManager;

/**
 * To build and manage view components.
 * @author equesada
 */
public class View extends JFrame {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * All components id => view.
	 */
	private Map<String, Component> views;
	
	/**
	 * Default constructor.
	 */
	public View() {
		super();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		views = new HashMap<String, Component>();
	}
	
	/**
	 * To get a component of the view.
	 * @param viewId the id of the asked component.
	 * @return the component.
	 */
	public final Component get(final String viewId) {
		return views.get(viewId);
	}

	/**
	 * To load a JFrame from an xml file.
	 * @param viewFile the xml file name (has to be located in classpath).
	 * @throws Exception if something is wrong.
	 */
	public final void load(final String viewFile) throws Exception {
		// 0 - Clean the view list
		views.clear();
		
		// 1 - Load the frame
		getComponent(viewFile);
				
		// 2 - If there is no dimension so pack the window.
		Dimension d = getSize();
		if (d == null || d.getHeight() == 0 || d.getWidth() == 0) {
			pack();
		}
		// 3 - Center the windows on the desktop.
		
			// 3.1 - Get the size of the screen.
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

			// 3.2 - Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

			// 3.3 - Move the window
		setLocation(x, y);
	}

	/**
	 * Get the root element and fill the component with children.
	 * @param file the filename of the xml file.
	 * @return the root component.
	 * @throws Exception if something is wrong.
	 */
	private Component getComponent(final String file) throws Exception {
		// 1 - Load the xml file.
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(file);
		
        if (null == stream) {
			throw new Exception("file not found");
		}
		
		// 2 - Parse the xml file.
		DocumentBuilderFactory dbf	= DocumentBuilderFactory.newInstance();
		DocumentBuilder db			= dbf.newDocumentBuilder();
		Document doc				= db.parse(stream);

		// 3 - Get the root element.
		Node root = doc.getDocumentElement();
		root.normalize();

		// 4 - Get a component with the node filled with the children.
		return fillChildren(root);
	}

	/**
	 * Fill the node with the children components.
	 * @param root the root element.
	 * @return the root element with children.
	 * @throws Exception if something is wrong.
	 */
	private Component fillChildren(final Node root) throws Exception {		
		// 1 - Get the root element as a view component
		Component component = getComponent(root);
		
		// 2 - For each child
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			
			// 2.1 - If it's not a real node so continue
			if (child.getNodeName().equals("#text")) {
				continue;
			}
			
			// 2.2 - Get the child like a component filled with the children.
			Component oneChild = fillChildren(child);
			
			// 2.3 - Link the child to the parent
			linkWithParent(component, oneChild);
		}
		return component;
	}

	/**
	 * To attach a child component to a parent component.
	 * @param parent the parent component.
	 * @param child the child component.
	 */
	private void linkWithParent(final Component parent, final Component child) {
		if (parent instanceof Frame) {
			if (child instanceof JPanel) {
				((JFrame) parent).setContentPane((JPanel) child);
			} else if (child instanceof JMenuBar) {
				((JFrame) parent).setJMenuBar((JMenuBar) child);
			}
		} else {
			((Container) parent).add(child);
		}
	}

	/**
	 * To get a Java component from an xml node.
	 * @param xmlNode the initial xml node.
	 * @return the java component.
	 * @throws Exception if the java component doesn't exist.
	 */
	@SuppressWarnings("rawtypes")
	private Component getComponent(final Node xmlNode) throws Exception {
		// 1 - Init
		Component component = null;
		
		if (views.isEmpty()) {
			
			// 2 - Use it self
			component = this;
			
		} else {	
			
			// 2 - Determine the java component class name (Ex: JPanel...)
			String clazzname = "J"
					+ xmlNode.getNodeName().substring(0, 1).toUpperCase()
					+ xmlNode.getNodeName().substring(1);
			
			// 3 - Instantiate an object of the class.
			List<String> ns = new ArrayList<String>();
			ns.add("javax.swing");
			Class clazz = ReflectionManager.getClass(clazzname, ns);
			Constructor constructor = ReflectionManager.getConstructor(clazz,
					new Object[0]);
			component = (Container) ReflectionManager.getObject(constructor,
					new Object[0]);
		}
		
		// 4 - Make ioc to call the methods of the component.
		ioc(xmlNode, component);
		
		// 5 - Return the component.
		return component;
	}

	/**
	 * @param xmlNode the initial xml node.
	 * @param component the java component corresponding to the node.
	 * @throws Exception if the java method doesn't exist.
	 */
	private void ioc(
			final Node xmlNode, 
			final Component component) 
				throws Exception {
		
		// 1 - Set the default layout orientation.
		if (component instanceof JMenuBar) {
			((JMenuBar) component).setLayout(new BoxLayout(
					((JMenuBar) component), BoxLayout.X_AXIS));
		} else if (component instanceof Container
				&& !(component instanceof JMenuItem)) {
			((Container) component).setLayout(new BoxLayout(
					((Container) component), BoxLayout.Y_AXIS));
		}
		
		// 2 - For each attribute try to call the corresponding java method.
		NamedNodeMap attrs = xmlNode.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			
			// 2.1 - Get the name and value
			Node attr = attrs.item(i);
			String name = attr.getNodeName();
			String value = attr.getNodeValue();
			
			// 2.2 - If id attribute so use to put in list of views
			if (name.equals("id")) {
				views.put(value, component);
				continue;
			}
			
			// 2.2 - If flow attribute so use to set the orientation
			if (name.equals("flow")) {
				if (value.equals("h")) {
					((Container) component).setLayout(new BoxLayout(
							((Container) component), BoxLayout.X_AXIS));
				} else if (value.equals("v")) {
					((Container) component).setLayout(new BoxLayout(
							((Container) component), BoxLayout.Y_AXIS));
				} 
				continue;
			} 
			
			// 2.3 - Call the good function giving parameters.
			String method = "set" + name.substring(0, 1).toUpperCase()
					+ name.substring(1);
			Object[] parameters = getParameters(value);
			Method m = ReflectionManager.getMethod(component.getClass(),
					method, getClasses(parameters));
			m.invoke(component, parameters);			
		}
	}

	/**
	 * To get the Class array from a parameters array.
	 * @param parameters the initial parameters.
	 * @return the corresponding classes.
	 */
	@SuppressWarnings("rawtypes")
	private Class[] getClasses(final Object[] parameters) {
		Class[] ret = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			ret[i] = parameters[i].getClass();
		}
		return ret;
	}

	/**
	 * To get parameters values from a string.
	 * @param string the initial string.
	 * @return the corresponding parameters.
	 */
	private Object[] getParameters(final String string) {
		String[] params = string.split(",");
		Object[] ret = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			String oneParam = params[i];
			if (oneParam.startsWith("i\\")) {
				ret[i] = Integer.parseInt(oneParam.substring(2));
			} else {
				ret[i] = oneParam;
			}
		}
		return ret;
	}
}
