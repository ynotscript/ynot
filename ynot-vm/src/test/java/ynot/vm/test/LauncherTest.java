package ynot.vm.test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import ynot.core.entity.Shell;
import ynot.core.parser.request.RequestParser;
import ynot.impl.provider.request.InputStreamRequestProvider;

/**
 * Class to test the launcher.
 * 
 * @author equesada
 * 
 */
public class LauncherTest {

	/**
	 * The application context to use.
	 */
	//private ApplicationContext context;

	/**
	 * Set up the shell before each call.
	 */
	@Before
	public final void setUp() {
		
	}

	/**
	 * To test the httpClient example.
	 */
	@Test
	public final void httpCLientTest() {
//		InputStreamRequestProvider requestProvider = new InputStreamRequestProvider("p");
//		requestProvider.setInputStream(new ByteArrayInputStream("echo(\"lo\")".getBytes()));
//		
//		BeanFactory factory = new XmlBeanFactory(new FileSystemResource("src/test/resources/shell.xml"));
//		
//		//create parent BeanFactory
//		DefaultListableBeanFactory parentBeanFactory = new DefaultListableBeanFactory(factory);
//		//register your pre-fabricated object in it
//		BeanDefinition bd = parentBeanFactory.getBeanDefinition("requestParser")
//		parentBeanFactory.
//		//requestProvider.setParser((RequestParser<String>) parentBeanFactory.getBean("requestParser"));
//		Shell shell = (Shell) parentBeanFactory.getBean("shell");
//		shell.run();
		
	}
}
