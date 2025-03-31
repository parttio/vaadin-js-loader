# Dynamic JavaScript loader for Vaadin

The Vaadin Dynamic JavaScript Loader enables the import of JavaScript libraries from the Java classpath, web resources, and external URLs. It particularly supports integration with cloud services like [cdnjs](https://cdnjs.com/) and [unpkg](https://www.unpkg.com/) in Vaadin applications.

## Why Use It?
This tool serves as a complementary feature to Vaadin's `@JavaScript` annotation, with several key benefits:

1. **Built-in Bookkeeping:** Ensures a library is loaded only once per UI and checks the version.

2. **URL Template-Based Syntax:** Allows atomic loading of multiple files or resources from a single library.

3. **Fully Dynamic Loading:** Enables runtime configuration and loading for greater flexibility.

4. **Load Script from Classpath:** Unlike `@JavaScript`, which only supports web resources, this tool can also load scripts from Java resources and the classpath.

5. **Component Subclassing:** Due to its dynamic nature, it is possible to modify and override loading behavior in Java component subclasses, particularly useful for updating JavaScript libraries.

6. **Built-in CDN Support:** Supports loading from cdnjs.com and unpkg.com, ideal for quick prototyping.

7. **CSS Loading Support:** Capable of importing CSS resources from the same library, in addition to JavaScript.


## Installation

```
<dependency>
    <groupId>org.parttio</groupId>
    <artifactId>vaadin-js-loader</artifactId>
    <version>2.3.2</version>
</dependency>
```

## Usage examples

### Loading from unpkg.com
Example loading [three.js](https://threejs.org/) from unpkg.com:
```
String library = "three";
String version = "0.158.0";
JSLoader.loadUnpkg(ui, library, version);
```

### Loading from cdnjs.com
Example loading [jquery](https://jquery.com/) from cdnjs.com:
```
String library = "jquery";
String version = "3.7.1";
JSLoader.loadCdnjs(ui, library, version);
```
### Local web resource
Example usage loading script and css file from Java classpath. Typically in `src/main/webapp`. 
```
String library = "mylib";
String version = "1.0";
String file = "mylib.js";
String urlPattern = "/{library}-{version}/{file}";
JSLoader.loadFiles(ui, urlPattern, library, version, file);
```
In this case URL pattern points to folder `src/main/webapp/mylib-1.0` which contains the specified resources.

### Java Classpath
Example usage loading script and css file from Java classpath. Typically in `src/main/resources`.
```
JSLoader.loadJavaResource(ui, MyClass.class, "myScript","myscript.js", "myscript.css");
```
For example in a Maven project if the `MyClass.java` is in package `org.vaadin.example` then the files are loaded from folder `src/main/resources/org/vaadin/example`.

