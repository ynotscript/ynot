package ynot.impl.provider.definition;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import ynot.core.entity.Definition;
import ynot.core.exception.parser.UnparsableDefinitionException;
import ynot.core.exception.provider.UnprovidableDefinitionException;
import ynot.core.listener.provider.definition.DefinitionProviderListener;
import ynot.core.parser.definition.DefinitionParser;
import ynot.core.provider.definition.DefinitionProvider;

/**
 * The dictionary, the definition provider used in the runtime.
 * 
 * @author equesada
 */
public class PropertiesDefinitionProvider implements DefinitionProvider<String> {

	// Member(s)

	/**
	 * The logger.
	 */
	private static Logger logger = Logger
			.getLogger(PropertiesDefinitionProvider.class);

	/**
	 * To know if the provider is initialized.
	 */
	private Boolean initialized;

	/**
	 * The available words.
	 */
	private Map<String, Definition> words;

	/**
	 * The properties files used to get words.
	 */
	private List<Properties> propertiesList;

	/**
	 * The parser used to parse properties value.
	 */
	private DefinitionParser<String> parser;

	/**
	 * The provider name.
	 */
	private final String providerName;

	/**
	 * The listeners of the provider.
	 */
	private final List<DefinitionProviderListener> listeners;

	// Constructor(s)

	/**
	 * The main constructor.
	 * 
	 * @param newProviderName
	 *            the name of the provider.
	 */
	public PropertiesDefinitionProvider(final String newProviderName) {
		super();
		initialized = false;
		providerName = newProviderName;
		words = new HashMap<String, Definition>();
		listeners = new ArrayList<DefinitionProviderListener>();
		propertiesList = new ArrayList<Properties>();
	}

	// Getter(s)/Setter(s)

	/**
	 * To get all the words of the dictionary.
	 * 
	 * @return the words member.
	 */
	public final Map<String, Definition> getWords() {
		return words;
	}

	/**
	 * To set new words all in once.
	 * 
	 * @param newWords
	 *            the new words.
	 */
	public final void setWords(final Map<String, Definition> newWords) {
		this.words = toLowerCase(newWords);
	}

	/**
	 * To have the definition in lowerCase.
	 * 
	 * @param newWords
	 *            the concerned definitions.
	 * @return the definition in lowerCase.
	 */
	private Map<String, Definition> toLowerCase(
			final Map<String, Definition> newWords) {
		Map<String, Definition> ret = new HashMap<String, Definition>();
		for (Entry<String, Definition> entry : newWords.entrySet()) {
			String key = entry.getKey().toLowerCase();
			Definition value = entry.getValue();
			ret.put(key, value);
		}
		return ret;
	}

	/**
	 * @param propertiesFile
	 *            the properties to set
	 * @throws IOException
	 *             if not able to open properties file.
	 */
	public final void setProperties(final List<String> propertiesFile)
			throws IOException {
		for (String oneFile : propertiesFile) {
			Properties lex = new Properties();
			InputStream stream = this.getClass().getClassLoader()
					.getResourceAsStream(oneFile.trim());
			try {
				lex.load(stream);
			} finally {
				stream.close();
			}
			propertiesList.add(lex);
		}
	}

	/**
	 * @return the propertiesList.
	 */
	public final List<Properties> getPropertiesList() {
		return propertiesList;
	}

	/**
	 * @param newParser
	 *            the parser to set.
	 */
	public final void setParser(final DefinitionParser<String> newParser) {
		this.parser = newParser;
	}

	/**
	 * @return the parser
	 */
	public final DefinitionParser<String> getParser() {
		return parser;
	}

	// Other functions

	/**
	 * To load the word from the properties using the parser.
	 */
	public final void init() {
		if (initialized) {
			return;
		}
		for (Properties lex : propertiesList) {
			Enumeration<Object> cmds = lex.keys();
			while (cmds.hasMoreElements()) {

				// 1 - the name of the command
				String key = (String) cmds.nextElement();
				String word = key;

				// 2 - get Definition
				Definition def = null;
				try {
					def = parser.parse(lex.getProperty(key).trim());
				} catch (UnparsableDefinitionException e) {
					logger.error("UnparsableDefinitionException", e);
					continue;
				}
				addWord(word, def);
			}
		}
		initialized = true;
	}

	/**
	 * To add a new word with his definition in the dictionary.
	 * 
	 * @param word
	 *            The word to add.
	 * @param definition
	 *            The associated definition.
	 */
	public final void addWord(final String word, final Definition definition) {
		words.put(word.toLowerCase(), definition);
	}

	/**
	 * To remove a word of the dictionary.
	 * 
	 * @param word
	 *            the word to delete.
	 */
	public final void removeWord(final String word) {
		words.remove(word.toLowerCase());
	}

	/**
	 * To get the definition of a word.
	 * 
	 * @param word
	 *            the word of the definition
	 * @return the definition of the given word
	 */
	public final Definition getDefinition(final String word) {
		return words.get(word.toLowerCase());
	}

	/**
	 * To get the list of available words.
	 * 
	 * @return the list of words.
	 */
	public final Set<String> getAvailableWords() {
		return words.keySet();
	}

	/**
	 * To get a definition from the word.
	 * 
	 * @param word
	 *            the word.
	 * @return the resource.
	 * @throws UnprovidableDefinitionException
	 *             if the definition doesn't exist.
	 */
	@Override
	public final Definition get(final String word)
			throws UnprovidableDefinitionException {
		init();
		Definition def = getDefinition(word.toLowerCase());
		def = preNoticeListeners(def);
		if (!postNoticeListeners(def)) {
			def = null;
		}
		if (null == def) {
			throw new UnprovidableDefinitionException("<" + providerName + "\\"
					+ word + ">");
		}
		return def;
	}

	/**
	 * Call listener before.
	 * 
	 * @param def
	 *            the current definition.
	 * @return the modified (or not) definition.
	 */
	private Definition preNoticeListeners(final Definition def) {
		Definition newDef = def;
		for (DefinitionProviderListener listener : listeners) {
			newDef = listener.preNotice(newDef);
		}
		return def;
	}

	/**
	 * Call listener after.
	 * 
	 * @param def
	 *            the current definition.
	 * @return the modified (or not) definition.
	 */
	private boolean postNoticeListeners(final Definition def) {
		for (DefinitionProviderListener listener : listeners) {
			if (!listener.postNotice(def)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Not used.
	 * 
	 * @return null.
	 */
	@Override
	public final Definition getNext() {
		return null;
	}

	/**
	 * Not used.
	 * 
	 * @return false.
	 */
	@Override
	public final boolean hasNext() {
		return false;
	}

	@Override
	public final String getName() {
		return providerName;
	}

	@Override
	public final void setListeners(
			final List<DefinitionProviderListener> newListeners) {
		for (DefinitionProviderListener listener : newListeners) {
			this.listeners.add(listener);
		}
	}

}
