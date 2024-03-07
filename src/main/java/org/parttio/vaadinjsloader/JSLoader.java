package org.parttio.vaadinjsloader;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.RequestHandler;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Dynamic JavaScript library loader for Vaadin.
 */
public class JSLoader {

    public static final String CDNJS = "https://cdnjs.cloudflare.com/ajax/libs/{library}/{version}/{file}";

    public static final String CDNJS_MIN = "https://cdnjs.cloudflare.com/ajax/libs/{library}/{version}/{file}";

    //Follow:  unpkg.com/:package@:version
    public static final String URL_PATTERN_UNPKGCOM = "https://unpkg.com/{library}@{version}";

    public static final String URL_PATTERN_UNPKGCOM_FILES = "https://unpkg.com/{library}@{version}/{file}";

    /** Public path used to serve the local Java classpath resources. */
    public static final String PUBLIC_JAVA_RESOURCE_PATH = "/resources/";
    private static final Map<String, String> loaded = new HashMap<>();

    /**
     * Loads a JavaScript and CSS files dynamically from given URL.
     * <p>
     * Pattern is used to construct the URL for the library. The pattern can
     * contain named placeholders <code>{library}</code>for the library name and <code>{version}</code> for the version. The
     * placeholders are replaced with the actual values before the URL is loaded.
     * <p>
     * E.g. for unpkg.com:
     * <code>https://unpkg.com/{library}@{version}/dist/{library}</code>
     *
     * @param component   the UI instance to load the library for
     * @param urlPattern  the base URL pattern to use for loading the library
     * @param libraryName the name of the library to load
     * @param version     the version of the library to load
     * @param libraryFile the JavaScript file to load or null
     * @deprecated Use {{@link #loadFiles(Component, String, String, String, String...)}} instead.
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    public static void load(Component component, String libraryName, String version, String libraryFile, String urlPattern) {
        loadFiles(component, urlPattern, libraryName, version, libraryFile);
    }

    /**
     * Loads a JavaScript and CSS files dynamically from given URL.
     * <p>
     * Pattern is used to construct the URL for the library. The pattern can
     * contain named placeholders <code>{library}</code>for the library name and <code>{version}</code> for the version. The
     * placeholders are replaced with the actual values before the URL is loaded.
     * <p>
     * E.g. for unpkg.com:
     * <code>https://unpkg.com/{library}@{version}/dist/{library}</code>
     *
     * @param component   the UI instance to load the library for
     * @param urlPattern  the base URL pattern to use for loading the library
     * @param libraryName the name of the library to load
     * @param version     the version of the library to load
     * @param file        the files of the library to load
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    public static void loadFiles(Component component, String urlPattern, String libraryName, String version, String... file) {
        assert component != null : "Component cannot be null";
        assert libraryName != null && !libraryName.isEmpty() : "Library name cannot be null or empty";
        assert urlPattern != null && !urlPattern.isEmpty() : "URL Pattern cannot be null or empty";

        // If no version was specified use 'latest'
        if (version == null || version.isEmpty()) {
            version = "latest";
        }

        // Check if the library has already been loaded for this UI
        if (isLoaded(component, libraryName)) {
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
                } else if (f.toLowerCase().endsWith(".mjs")) {
                    ui.getPage().addJsModule(fileUrl);
                    importModuleExports(ui, libraryName, fileUrl);
                } else {
                    ui.getPage().addJavaScript(fileUrl);
                }
            }
        } else {
            // Load the script and mark the library as loaded for this UI
            String scriptUrl = replacePlaceholders(urlPattern, replacements);
            if (scriptUrl.toLowerCase().endsWith(".mjs")) {
                ui.getPage().addJsModule(scriptUrl);
                importModuleExports(ui, libraryName, scriptUrl);
            } else {
                ui.getPage().addJavaScript(scriptUrl);
            }
        }
        setLoadedVersion(ui, libraryName, version);

    }

    private static void importModuleExports( UI ui, String libraryName, String fileUrl) {
        ui.getPage().executeJs("globalThis[$1]=globalThis[$1] || {}; var f = async () => { var m = await import($0); Object.keys(m).forEach(k => globalThis[$1][k] = m[k])}; return f();", fileUrl, libraryName);
    }

    /**
     * Returns the version of the library that has been loaded for the given UI.
     *
     * @param component the UI instance to check
     * @param library   the name of the library to check
     * @return the version of the library that has been loaded for the given UI
     */
    public static String getLoadedVersion(Component component, String library) {
        return loaded.get(getUI(component).getUIId()+"_"+library);
    }

    /**
     * Loads a minified JavaScript library dynamically from cdnjs.com.
     *
     * @param component   the UI instance to load the library for
     * @param libraryName the name of the library to load
     * @param version     the version of the library to load
     * @see #loadCdnjs(Component, String, String, boolean)
     */
    public static void loadCdnjs(Component component, String libraryName, String version) {
        load(component, libraryName, version, libraryName + ".min.js", CDNJS_MIN);
    }

    /**
     * Loads a JavaScript library dynamically from cdnjs.com.
     *
     * @param component   the UI instance to load the library for
     * @param libraryName the name of the library to load
     * @param version     the version of the library to load
     */
    public static void loadCdnjs(Component component, String libraryName, String version, boolean minified) {
        if (minified) {
            load(component, libraryName, version, null, CDNJS_MIN);
        } else {
            load(component, libraryName, version, null, CDNJS);
        }
    }

    /**
     * Loads a JavaScript library dynamically from unpkg.com.
     *
     * @param component   the UI instance to load the library for
     * @param libraryName the name of the library to load
     * @param version     the version of the library to load
     * @param libraryFile the file(s) to load or null
     */
    public static void loadUnpkg(Component component, String libraryName, String version, String... libraryFile) {
        if (libraryFile != null && libraryFile.length > 0) {
            loadFiles(component, URL_PATTERN_UNPKGCOM_FILES, libraryName, version, libraryFile);
        } else {
            loadFiles(component, URL_PATTERN_UNPKGCOM, libraryName, version, libraryFile);
        }
    }

    /** Load library from Java resources.
     *  Useful when loading JavaScript and CSS resources e.g. from Maven's <code>src/main/resources</code> folder.
     *  Effectively uses <code>Class.getResourceAsStream()</code>.
     *
     * @param ui The UI where we are loading the resources.
     * @param cls Class to load resources from using <code>getResourceAsStream</code>.
     * @param libraryName Name of the library used as part of URL. This is also used to avoid loading twice.
     * @param files The files to load for this library. Supports .js and .css.

     * @see Class#getResourceAsStream(String)
     * @see #PUBLIC_JAVA_RESOURCE_PATH
     */
    public static void loadJavaResource(UI ui, Class<?> cls, String libraryName, String... files) {

        if (JSLoader.isLoaded(ui, libraryName)) {
            return;
        }

        // Load files
        JSLoader.loadFiles(ui, PUBLIC_JAVA_RESOURCE_PATH +"{library}/{file}", libraryName, "latest", files);

        // Handle resource load requests
        RequestHandler requestHandler = (session, request, response) -> {
            if (!request.getPathInfo().contains(PUBLIC_JAVA_RESOURCE_PATH+libraryName)) {
                return false;
            }

            String resourceName = request.getPathInfo()
                    .substring(request.getPathInfo()
                            .indexOf(PUBLIC_JAVA_RESOURCE_PATH)+(PUBLIC_JAVA_RESOURCE_PATH+libraryName+"/").length());
            if (Arrays.asList(files).contains(resourceName)) {

                // Get the resource as a stream
                InputStream resourceStream = cls.getResourceAsStream(resourceName);
                if (resourceStream == null) {
                    // Handle the case where resource is not found
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Failed to load file.");
                    return true;
                }
                try (resourceStream) {

                    response.setContentType(getContentTypeForFileExtension(resourceName));
                    response.setStatus(HttpServletResponse.SC_OK);
                    resourceStream.transferTo(response.getOutputStream());
                } catch (Exception e) {
                    try {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to load file.");
                    } catch (Exception e2) {
                        // Ignored. Likely header already sent.
                    }
                    return true;
                }

                return true;
            }
            return false;
        };

        // Vaadin's request handler (un-)registration magic
        ui.getElement().getNode().runWhenAttached(ui2 -> ui2.beforeClientResponse(ui2, ctx -> {
            ctx.getUI().getSession().addRequestHandler(requestHandler);
            ui2.getElement().getNode().addDetachListener(() -> {
                ctx.getUI().getSession().removeRequestHandler(requestHandler);
            });
        }));
    }

    /** Utility to get content type for a file extension.
     *  Currently only supports .js, .css and .txt.
     *
     * @param resourceName Name of the resource.
     * @return Content type for the resource.
     */
    private static String getContentTypeForFileExtension(String resourceName) {
        if (resourceName.endsWith(".js") || resourceName.endsWith(".mjs")) {
            return "application/javascript";
        } else if (resourceName.endsWith(".css")) {
            return "text/css";
        } else {
            return "text/plain";
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
        loaded.put(ui.getUIId()+"_"+library,version);
        ui.getPage().executeJs("window.vaadinjsloader = window.vaadinjsloader || {}; window.vaadinjsloader[$0] = $1; return window.vaadinjsloader;", library, version);
    }

    /**
     * Check if the given library has been loaded for the UI.
     *
     * @param component the UI instance to check
     * @param library   the name of the library to check
     * @return true if the given library has been loaded for the given UI
     */
    public static boolean isLoaded(Component component, String library) {
        return getLoadedVersion(component, library) != null;
    }

    /**
     * Check if the given library and version has been loaded for the UI.
     *
     * @param component the UI instance to check
     * @param library   the name of the library to check
     * @param version   the version of the library to check
     * @return true if the given library and version has been loaded for the given UI
     */
    public static boolean isLoaded(Component component, String library, String version) {
        return version != null && version.equals(getLoadedVersion(component, library));
    }
}
