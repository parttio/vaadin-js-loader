# vaadin-js-loader

Dynamic JavaScript loader for Vaadin. Can be used to dynamically load local JavaScript libraries, and also 
supports loading JavaScript dynamically from cloud services like [cdnjs](https://cdnjs.com/)https://cdnjs.com/ 
and [unpkg](https://www.unpkg.com/)https://www.unpkg.com/.

## Installation

```
<dependency>
    <groupId>org.parttio</groupId>
    <artifactId>vaadin-js-loader</artifactId>
    <version>0.9.0</version>
</dependency>
```

## Usage
Example loading [three.js](https://threejs.org/) from unpkg.com:
```
String library = "three";
String version = "0.158.0";
JSLoader.loadUnpkg(this, library, version);
```