alert("loaded mylib.js from web resources");
const span = document.createElement("span");
span.id = "mylib";
span.appendChild(document.createTextNode("Hello from mylib.js"));
document.body.appendChild(span);
