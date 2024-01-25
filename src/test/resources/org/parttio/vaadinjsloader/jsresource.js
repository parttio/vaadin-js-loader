alert("loaded jsresource.js from classpath");
const span = document.createElement("span");
span.id = "jsresource";
span.appendChild(document.createTextNode("Hello from jsresource.js"));
document.body.appendChild(span);
