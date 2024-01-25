# Dynamic JavaScript loader for Vaadin

The Vaadin Dynamic JavaScript Loader imports JavaScript libraries from the Java classpath, web resources, and external URLs. It also supports integration of JavaScript from cloud services like [cdnjs](https://cdnjs.com/) and [unpkg](https://www.unpkg.com/) in Vaadin applications.

Why? This is complentary and similar to Vaadin's built in `@JavaScript` annotation with few differences and benefits: 

- **Built-in bookkeeping**: Loads a library only once per UI and check the version of the loaded library.
- **URL template-based syntax**: Atomically load multiple files/resources from a single library.
- **Fully dynamic loading**: Allows runtime configuration, loading, and checks in the application. You can even let users choose what to load and when (but be careful with that, please...)
- **Load script from classpath**: In addition to public web resources, can load scripts also from Java resources and classpath (`@JavaScript` support only web resources)
- **Component subclassing**: Due to the fully dynamic nature, it is possible to change and override the loading behavior in Java component subclasses. This is handy e.g. if there is an update to the loaded JavaScript library.
- **Built-in CDN support**: In addition to local resources, load from cdnjs.com and unpkg.com. Handy for quick prototypes.
- **CSS loading support**: In addition to JS, import CSS resources from the same library.


## Installation

```
<dependency>
    <groupId>org.parttio</groupId>
    <artifactId>vaadin-js-loader</artifactId>
    <version>2.2.1</version>
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

