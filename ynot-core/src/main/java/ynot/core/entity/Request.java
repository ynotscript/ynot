package ynot.core.entity;

import java.util.Arrays;

/**
 * This class represents a request of command.
 * 
 * @author equesada
 */
public class Request {

	/**
	 * The word of language to use.
	 */
	private String wordToUse;

	/**
	 * The provider name to find the definition of this word.
	 */
	private String definitionProviderName;

	/**
	 * The variable names to set with the return. null if no variable.
	 */
	private String[] variableNames;

	/**
	 * The parameters to give to the methods.
	 */
	private Object[] givenParameters;

	/**
	 * To indicate that the request have to be execute.
	 */
	private boolean active;

	/**
	 * The default constructor.
	 */
	public Request() {
		setActive(true);
		setGivenParameters(new Object[0]);
		setVariableNames(new String[0]);
	}

	/**
	 * The word getter.
	 * 
	 * @return the word member.
	 */
	public final String getWordToUse() {
		return wordToUse;
	}

	/**
	 * The word setter.
	 * 
	 * @param newWord
	 *            the new word.
	 */
	public final void setWordToUse(final String newWord) {
		this.wordToUse = newWord;
	}

	/**
	 * The variableName getter.
	 * 
	 * @return the variableName member.
	 */
	public final String[] getVariableNames() {
		return variableNames;
	}

	/**
	 * The variableNames setter.
	 * 
	 * @param newVariableNames
	 *            the new Variable Name.
	 */
	public final void setVariableNames(final String[] newVariableNames) {
		this.variableNames = (String[]) copy(newVariableNames);
	}

	/**
	 * The arguments getter.
	 * 
	 * @return the arguments member.
	 */
	public final Object[] getGivenParameters() {
		return givenParameters;
	}

	/**
	 * The arguments setter.
	 * 
	 * @param newGivenParameters
	 *            the new parameters.
	 */
	public final void setGivenParameters(final Object[] newGivenParameters) {
		this.givenParameters = copy(newGivenParameters);
	}

	/**
	 * The active setter.
	 * 
	 * @param newActive
	 *            the new active value.
	 */
	public final void setActive(final boolean newActive) {
		this.active = newActive;
	}

	/**
	 * To know if the request is active.
	 * 
	 * @return the active value.
	 */
	public final boolean isActive() {
		return active;
	}

	/**
	 * To set the definition provider name.
	 * 
	 * @param newDefinitionProviderName
	 *            the definition provider name.
	 */
	public final void setDefinitionProviderName(
			final String newDefinitionProviderName) {
		this.definitionProviderName = newDefinitionProviderName;
	}

	/**
	 * To get the definition provider name of this word.
	 * 
	 * @return the definitionProviderName.
	 */
	public final String getDefinitionProviderName() {
		return definitionProviderName;
	}

	/**
	 * To copy an array.
	 * 
	 * @param array
	 *            the array to copy.
	 * @return the copied array.
	 */
	private Object[] copy(final Object[] array) {
		return Arrays.copyOf(array, array.length);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (isActive()) {
			sb.append(getVariableNames());
			sb.append(" <- ");
			sb.append(getDefinitionProviderName());
			sb.append("\\");
			sb.append(getWordToUse());
			sb.append(" ( ");
			sb.append(getGivenParameters());
			sb.append(" ) ");
		} else {
			sb.append("#");
		}
		return sb.toString();
	}

}
