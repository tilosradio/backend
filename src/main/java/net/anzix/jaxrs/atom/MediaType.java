package net.anzix.jaxrs.atom;


import java.util.*;

/**
 * An abstraction for a media type. Instances are immutable.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1 section 3.7</a>
 * @since 1.0
 */
@SuppressWarnings("JavaDoc")
public class MediaType {

    private String type;
    private String subtype;
    private Map<String, String> parameters;

    /**
     * The media type {@code charset} parameter name.
     */
    public static final String CHARSET_PARAMETER = "charset";
    /**
     * The value of a type or subtype wildcard "{@value #MEDIA_TYPE_WILDCARD}".
     */
    public static final String MEDIA_TYPE_WILDCARD = "*";
    // Common media type constants
    /**
     * A {@code String} constant representing wildcard "{@value #WILDCARD}" media type .
     */
    public final static String WILDCARD = "*/*";
    /**
     * A {@link MediaType} constant representing wildcard "{@value #WILDCARD}" media type.
     */
    public final static MediaType WILDCARD_TYPE = new MediaType();
    /**
     * A {@code String} constant representing "{@value #APPLICATION_XML}" media type.
     */
    public final static String APPLICATION_XML = "application/xml";
    /**
     * A {@link MediaType} constant representing "{@value #APPLICATION_XML}" media type.
     */
    public final static MediaType APPLICATION_XML_TYPE = new MediaType("application", "xml");
    /**
     * A {@code String} constant representing "{@value #APPLICATION_ATOM_XML}" media type.
     */
    public final static String APPLICATION_ATOM_XML = "application/atom+xml";
    /**
     * A {@link MediaType} constant representing "{@value #APPLICATION_ATOM_XML}" media type.
     */
    public final static MediaType APPLICATION_ATOM_XML_TYPE = new MediaType("application", "atom+xml");
    /**
     * A {@code String} constant representing "{@value #APPLICATION_XHTML_XML}" media type.
     */
    public final static String APPLICATION_XHTML_XML = "application/xhtml+xml";
    /**
     * A {@link MediaType} constant representing "{@value #APPLICATION_XHTML_XML}" media type.
     */
    public final static MediaType APPLICATION_XHTML_XML_TYPE = new MediaType("application", "xhtml+xml");
    /**
     * A {@code String} constant representing "{@value #APPLICATION_SVG_XML}" media type.
     */
    public final static String APPLICATION_SVG_XML = "application/svg+xml";
    /**
     * A {@link MediaType} constant representing "{@value #APPLICATION_SVG_XML}" media type.
     */
    public final static MediaType APPLICATION_SVG_XML_TYPE = new MediaType("application", "svg+xml");
    /**
     * A {@code String} constant representing "{@value #APPLICATION_JSON}" media type.
     */
    public final static String APPLICATION_JSON = "application/json";
    /**
     * A {@link MediaType} constant representing "{@value #APPLICATION_JSON}" media type.
     */
    public final static MediaType APPLICATION_JSON_TYPE = new MediaType("application", "json");
    /**
     * A {@code String} constant representing "{@value #APPLICATION_FORM_URLENCODED}" media type.
     */
    public final static String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    /**
     * A {@link MediaType} constant representing "{@value #APPLICATION_FORM_URLENCODED}" media type.
     */
    public final static MediaType APPLICATION_FORM_URLENCODED_TYPE = new MediaType("application", "x-www-form-urlencoded");
    /**
     * A {@code String} constant representing "{@value #MULTIPART_FORM_DATA}" media type.
     */
    public final static String MULTIPART_FORM_DATA = "multipart/form-data";
    /**
     * A {@link MediaType} constant representing "{@value #MULTIPART_FORM_DATA}" media type.
     */
    public final static MediaType MULTIPART_FORM_DATA_TYPE = new MediaType("multipart", "form-data");
    /**
     * A {@code String} constant representing "{@value #APPLICATION_OCTET_STREAM}" media type.
     */
    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    /**
     * A {@link MediaType} constant representing "{@value #APPLICATION_OCTET_STREAM}" media type.
     */
    public final static MediaType APPLICATION_OCTET_STREAM_TYPE = new MediaType("application", "octet-stream");
    /**
     * A {@code String} constant representing "{@value #TEXT_PLAIN}" media type.
     */
    public final static String TEXT_PLAIN = "text/plain";
    /**
     * A {@link MediaType} constant representing "{@value #TEXT_PLAIN}" media type.
     */
    public final static MediaType TEXT_PLAIN_TYPE = new MediaType("text", "plain");
    /**
     * A {@code String} constant representing "{@value #TEXT_XML}" media type.
     */
    public final static String TEXT_XML = "text/xml";
    /**
     * A {@link MediaType} constant representing "{@value #TEXT_XML}" media type.
     */
    public final static MediaType TEXT_XML_TYPE = new MediaType("text", "xml");
    /**
     * A {@code String} constant representing "{@value #TEXT_HTML}" media type.
     */
    public final static String TEXT_HTML = "text/html";
    /**
     * A {@link MediaType} constant representing "{@value #TEXT_HTML}" media type.
     */
    public final static MediaType TEXT_HTML_TYPE = new MediaType("text", "html");


    private static TreeMap<String, String> createParametersMap(Map<String, String> initialValues) {
        final TreeMap<String, String> map = new TreeMap<String, String>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        if (initialValues != null) {
            for (Map.Entry<String, String> e : initialValues.entrySet()) {
                map.put(e.getKey().toLowerCase(), e.getValue());
            }
        }
        return map;
    }

    /**
     * Creates a new instance of {@code MediaType} with the supplied type, subtype and
     * parameters.
     *
     * @param type       the primary type, {@code null} is equivalent to
     *                   {@link #MEDIA_TYPE_WILDCARD}.
     * @param subtype    the subtype, {@code null} is equivalent to
     *                   {@link #MEDIA_TYPE_WILDCARD}.
     * @param parameters a map of media type parameters, {@code null} is the same as an
     *                   empty map.
     */
    public MediaType(String type, String subtype, Map<String, String> parameters) {
        this(type, subtype, null, createParametersMap(parameters));
    }

    /**
     * Creates a new instance of {@code MediaType} with the supplied type and subtype.
     *
     * @param type    the primary type, {@code null} is equivalent to
     *                {@link #MEDIA_TYPE_WILDCARD}
     * @param subtype the subtype, {@code null} is equivalent to
     *                {@link #MEDIA_TYPE_WILDCARD}
     */
    public MediaType(String type, String subtype) {
        this(type, subtype, null, null);
    }

    /**
     * Creates a new instance of {@code MediaType} with the supplied type, subtype and
     * "{@value #CHARSET_PARAMETER}" parameter.
     *
     * @param type    the primary type, {@code null} is equivalent to
     *                {@link #MEDIA_TYPE_WILDCARD}
     * @param subtype the subtype, {@code null} is equivalent to
     *                {@link #MEDIA_TYPE_WILDCARD}
     * @param charset the "{@value #CHARSET_PARAMETER}" parameter value. If {@code null} or empty
     *                the "{@value #CHARSET_PARAMETER}" parameter will not be set.
     */
    public MediaType(String type, String subtype, String charset) {
        this(type, subtype, charset, null);
    }

    /**
     * Creates a new instance of {@code MediaType}, both type and subtype are wildcards.
     * Consider using the constant {@link #WILDCARD_TYPE} instead.
     */
    public MediaType() {
        this(MEDIA_TYPE_WILDCARD, MEDIA_TYPE_WILDCARD, null, null);
    }

    private MediaType(String type, String subtype, String charset, Map<String, String> parameterMap) {

        this.type = type == null ? MEDIA_TYPE_WILDCARD : type;
        this.subtype = subtype == null ? MEDIA_TYPE_WILDCARD : subtype;

        if (parameterMap == null) {
            parameterMap = new TreeMap<String, String>(new Comparator<String>() {

                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
        }

        if (charset != null && !charset.isEmpty()) {
            parameterMap.put(CHARSET_PARAMETER, charset);
        }
        this.parameters = Collections.unmodifiableMap(parameterMap);
    }

    /**
     * Getter for primary type.
     *
     * @return value of primary type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Checks if the primary type is a wildcard.
     *
     * @return true if the primary type is a wildcard.
     */
    public boolean isWildcardType() {
        return this.getType().equals(MEDIA_TYPE_WILDCARD);
    }

    /**
     * Getter for subtype.
     *
     * @return value of subtype.
     */
    public String getSubtype() {
        return this.subtype;
    }

    /**
     * Checks if the subtype is a wildcard.
     *
     * @return true if the subtype is a wildcard.
     */
    public boolean isWildcardSubtype() {
        return this.getSubtype().equals(MEDIA_TYPE_WILDCARD);
    }

    /**
     * Getter for a read-only parameter map. Keys are case-insensitive.
     *
     * @return an immutable map of parameters.
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Create a new {@code MediaType} instance with the same type, subtype and parameters
     * copied from the original instance and the supplied "{@value #CHARSET_PARAMETER}" parameter.
     *
     * @param charset the "{@value #CHARSET_PARAMETER}" parameter value. If {@code null} or empty
     *                the "{@value #CHARSET_PARAMETER}" parameter will not be set or updated.
     * @return copy of the current {@code MediaType} instance with the "{@value #CHARSET_PARAMETER}"
     *         parameter set to the supplied value.
     * @since 2.0
     */
    public MediaType withCharset(String charset) {
        return new MediaType(this.type, this.subtype, charset, createParametersMap(this.parameters));
    }

    /**
     * Check if this media type is compatible with another media type. E.g.
     * image/* is compatible with image/jpeg, image/png, etc. Media type
     * parameters are ignored. The function is commutative.
     *
     * @param other the media type to compare with.
     * @return true if the types are compatible, false otherwise.
     */
    public boolean isCompatible(MediaType other) {
        return other != null && // return false if other is null, else
                (type.equals(MEDIA_TYPE_WILDCARD) || other.type.equals(MEDIA_TYPE_WILDCARD) || // both are wildcard types, or
                        (type.equalsIgnoreCase(other.type) && (subtype.equals(MEDIA_TYPE_WILDCARD)
                                || other.subtype.equals(MEDIA_TYPE_WILDCARD))) || // same types, wildcard sub-types, or
                        (type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype))); // same types & sub-types
    }

    /**
     * Compares {@code obj} to this media type to see if they are the same by comparing
     * type, subtype and parameters. Note that the case-sensitivity of parameter
     * values is dependent on the semantics of the parameter name, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1</a>}.
     * This method assumes that values are case-sensitive.
     * <p/>
     * Note that the {@code equals(...)} implementation does not perform
     * a class equality check ({@code this.getClass() == obj.getClass()}). Therefore
     * any class that extends from {@code MediaType} class and needs to override
     * one of the {@code equals(...)} and {@link #hashCode()} methods must
     * always override both methods to ensure the contract between
     * {@link Object#equals(java.lang.Object)} and {@link Object#hashCode()} does
     * not break.
     *
     * @param obj the object to compare to.
     * @return true if the two media types are the same, false otherwise.
     */
    @SuppressWarnings("UnnecessaryJavaDocLink")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MediaType)) {
            return false;
        }

        MediaType other = (MediaType) obj;
        return (this.type.equalsIgnoreCase(other.type)
                && this.subtype.equalsIgnoreCase(other.subtype)
                && this.parameters.equals(other.parameters));
    }

    /**
     * Generate a hash code from the type, subtype and parameters.
     * <p/>
     * Note that the {@link #equals(java.lang.Object)} implementation does not perform
     * a class equality check ({@code this.getClass() == obj.getClass()}). Therefore
     * any class that extends from {@code MediaType} class and needs to override
     * one of the {@link #equals(Object)} and {@code hashCode()} methods must
     * always override both methods to ensure the contract between
     * {@link Object#equals(java.lang.Object)} and {@link Object#hashCode()} does
     * not break.
     *
     * @return a generated hash code.
     */
    @SuppressWarnings("UnnecessaryJavaDocLink")
    @Override
    public int hashCode() {
        return (this.type.toLowerCase() + this.subtype.toLowerCase()).hashCode() + this.parameters.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getType()).append('/').append(getSubtype());
        Iterator i$ = getParameters().entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry e = (Map.Entry)i$.next();
            b.append("; ").append((String)e.getKey()).append('=');
            appendQuotedIfNonToken(b, (String) e.getValue());
        }

        return b.toString();
    }


    public static void appendQuotedIfNonToken(StringBuilder b, String value) {
        b.append(value);


    }


}
