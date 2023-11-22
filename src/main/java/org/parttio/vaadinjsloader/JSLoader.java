package org.parttio.vaadinjsloader;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;

import java.util.HashMap;
import java.util.Map;

/** Dynamic JavaScript library loader for Vaadin.
 *
 */
public class JSLoader {

    public static final String CDNJS = "https://cdnjs.cloudflare.com/ajax/libs/{library}/{version}/{file}";

    public static final String CDNJS_MIN = "https://cdnjs.cloudflare.com/ajax/libs/{library}/{version}/{file}";

    //Follow:  unpkg.com/:package@:version
    public static final String URL_PATTERN_UNPKGCOM = "https://unpkg.com/{library}@{version}";

    public static final String URL_PATTERN_UNPKGCOM_FILES = "https://unpkg.com/{library}@{version}/{file}";

    /**
     * Loads a JavaScript and CSS files dynamically from given CDN URL.
     *
     *  Pattern is used to construct the URL for the library. The pattern can
     *  contain named placeholders <code>{library}</code>for the library name and <code>{version}</code> for the version. The
     *  placeholders are replaced with the actual values before the URL is
     *  loaded.
     *
     *  E.g. for unpkg.com:
     *  <code>https://unpkg.com/{library}@{version}/dist/{library}</code>
     *
     * @deprecated Use {{@link #loadFiles(Component, String, String, String, String...)}} instead.
     *
     * @param component       the UI instance to load the library for
     * @param urlPattern  the base URL pattern to use for loading the library
     * @param libraryName     the name of the library to load
     * @param version         the version of the library to load
     * @param libraryFile     the JavaScript file to load or null
     */
    public static void load(Component component, String libraryName, String version,  String libraryFile, String urlPattern) {
        loadFiles(component,urlPattern,libraryName, version, libraryFile);
    }

    /**
     * Loads a JavaScript and CSS files dynamically from given CDN URL.
     *
     *  Pattern is used to construct the URL for the library. The pattern can
     *  contain named placeholders <code>{library}</code>for the library name and <code>{version}</code> for the version. The
     *  placeholders are replaced with the actual values before the URL is
     *  loaded.
     *
     *  E.g. for unpkg.com:
     *  <code>https://unpkg.com/{library}@{version}/dist/{library}</code>
     *
     *
     * @param component       the UI instance to load the library for
     * @param urlPattern  the base URL pattern to use for loading the library
     * @param libraryName     the name of the library to load
     * @param version         the version of the library to load
     * @param file            the files of the library to load
     */
    public static void loadFiles(Component component, String urlPattern, String libraryName, String version, String... file) {
        assert component != null : "Component cannot be null";
        assert libraryName != null && !libraryName.isEmpty() : "Library name cannot be null or empty";
        assert urlPattern != null && !urlPattern.isEmpty(): "URL Pattern cannot be null or empty";

        // If no version was specified use 'latest'
        if (version == null || version.isEmpty()) {
            version = "latest";
        }

        // Check if the library has already been loaded for this UI
        if (isLoaded(component,libraryName)) {
            return;
        }

        // Replace placeholders in the base URL pattern
        Map<String, String> replacements = new HashMap<>();
        replacements.put("library", libraryName);
        replacements.put("version", version);

        UI ui = getUI(component);
        if (file != null && file.length > 0) {
            for (String f : file) {
                replacements.put("file", f);
                String fileUrl = replacePlaceholders(urlPattern, replacements);
                if (f.toLowerCase().endsWith(".css")) {
                    ui.getPage().addStyleSheet(fileUrl);
                } else {
                    ui.getPage().addJavaScript(fileUrl);
                }
            }
        } else {
            // Load the script and mark the library as loaded for this UI
            String scriptUrl = replacePlaceholders(urlPattern, replacements);
            ui.getPage().addJavaScript(scriptUrl);
        }
        setLoadedVersion(ui,libraryName,version);

    }

    /** Returns the version of the library that has been loaded for the given UI.
     *
     * @param component the UI instance to check
     * @param library the name of the library to check
     * @return the version of the library that has been loaded for the given UI
     */
    public static String getLoadedVersion(Component component, String library) {
        String propertyKey = "loaded-"+library;
        return getUI(component).getElement().getProperty(propertyKey);
    }

    /**
     * Loads a minified JavaScript library dynamically from cdnjs.com.
     *
     * @see #loadCdnjs(Component, String, String, boolean)
     *
     * @param component              the UI instance to load the library for
     * @param libraryName     the name of the library to load
     * @param version         the version of the library to load
     */
    public static void loadCdnjs(Component component, String libraryName, String version) {
        load(component,libraryName,version,libraryName+".min.js", CDNJS_MIN);
    }

    /**
     * Loads a JavaScript library dynamically from cdnjs.com.
     *
     * @param component              the UI instance to load the library for
     * @param libraryName     the name of the library to load
     * @param version         the version of the library to load
     */
    public static void loadCdnjs(Component component, String libraryName, String version, boolean minified) {
        if (minified) {
            load(component,libraryName,version,null, CDNJS_MIN);
        } else {
            load(component,libraryName,version,null, CDNJS);
        }
    }

    /**
     * Loads a JavaScript library dynamically from unpkg.com.
     *
     * @param component       the UI instance to load the library for
     * @param libraryName     the name of the library to load
     * @param version         the version of the library to load
     * @param libraryFile     the file(s) to load or null
     */
    public static void loadUnpkg(Component component, String libraryName, String version, String... libraryFile) {
        if (libraryFile != null && libraryFile.length > 0) {
            loadFiles(component,URL_PATTERN_UNPKGCOM_FILES,libraryName,version,libraryFile);
        } else {
            loadFiles(component,URL_PATTERN_UNPKGCOM,libraryName,version,libraryFile);
        }
    }

    private static String replacePlaceholders(String baseUrlPattern, Map<String, String> replacements) {
        String result = baseUrlPattern;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            if (entry.getValue() != null)
                result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    private static UI getUI(Component component) {
        if (component instanceof UI) return (UI) component;
        return component.getUI().orElse(UI.getCurrent());  // Use ThreadLocal as the last resort
    }

    private static void setLoadedVersion(UI ui, String library, String version) {
        String propertyKey = "loaded-"+library;
        ui.getElement().setProperty(propertyKey, version);
    }

    /** Check if the given library has been loaded for the UI.
     *
     * @param component the UI instance to check
     * @param library the name of the library to check
     * @return true if the given library has been loaded for the given UI
     */
    public static boolean isLoaded(Component component, String library) {
        return getLoadedVersion(component,library) != null;
    }

    /** Check if the given library and version has been loaded for the UI.
     *
     * @param component the UI instance to check
     * @param library the name of the library to check
     * @param version the version of the library to check
     * @return true if the given library and version has been loaded for the given UI
     */
    public static boolean isLoaded(Component component, String library, String version) {
        return version != null && version.equals(getLoadedVersion(component,library));
    }
}
