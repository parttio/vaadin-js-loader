alert("loaded jsresource.js from classpath");
// Because this is constant, it will cause an error
// if the script is loaded twice
const jsresource_span = document.createElement("span");
jsresource_span.id = "jsresource";
jsresource_span.appendChild(document.createTextNode("Hello from jsresource.js"));
document.body.appendChild(jsresource_span);

