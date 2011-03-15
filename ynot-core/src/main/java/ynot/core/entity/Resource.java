package ynot.core.entity;

/**
 * This class is the object given by a resource provider.
 * 
 * @author equesada
 */
public class Resource {

    // fields

    /**
     * The provider that gave this resource.
     */
    private String providerName;

    /**
     * The name of the resource.
     */
    private String resourceName;

    /**
     * The resource.
     */
    private Object content;

    // constructors

    /**
     * Constructor using fields.
     * 
     * @param provider
     *            the provider used to have the resource.
     * @param name
     *            the name of the resource.
     * @param newContent
     *            the resource object.
     */
    public Resource(final String provider, final String name,
            final Object newContent) {
        super();
        this.providerName = provider;
        this.resourceName = name;
        this.content = newContent;
    }

    // getters/setters

    /**
     * @return the provider
     */
    public final String getProviderName() {
        return providerName;
    }

    /**
     * @param provider
     *            the provider to set
     */
    public final void setProviderName(final String provider) {
        this.providerName = provider;
    }

    /**
     * @return the name
     */
    public final String getResourceName() {
        return resourceName;
    }

    /**
     * @param name
     *            the name to set
     */
    public final void setResourceName(final String name) {
        this.resourceName = name;
    }

    /**
     * @return the content
     */
    public final Object getContent() {
        return content;
    }

    /**
     * @param newContent
     *            the content to set
     */
    public final void setContent(final Object newContent) {
        this.content = newContent;
    }

}
