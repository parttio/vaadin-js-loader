package org.parttio.vaadinjsloader;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class TestView extends VerticalLayout {

    public TestView() {
        add(new H1("Addon test"));
        add(new Button("Load from cdnjs.com", this::loadLibraryCdnJs));
        add(new Button("Load from unpkg.com", this::loadLibraryUnpkg));
        add(new Button("Load local library", this::loadLibraryCustom));
        add(new Button("Load local module", this::loadLocalModule));
        add(new Button("Load library from classpath", this::loadLibraryFromClasspath));
    }


    private void loadLibraryCdnJs(ClickEvent<Button> buttonClickEvent) {
        // Load some script
        String library = "jquery";
        String version = "3.7.1";
        JSLoader.loadCdnjs(this, library, version);

        // Check that the script is indeed loaded
        UI.getCurrent()
                .getPage()
                .executeJs("return document.querySelector('script[src*=\"" + library + "\"]').src")
                .then(src -> add(new Paragraph("loaded "+src.asString())));
    }

    private void loadLibraryUnpkg(ClickEvent<Button> buttonClickEvent) {
        // Load some script
        String library = "three";
        String version = "0.158.0";
        JSLoader.loadUnpkg(this, library, version);

        // Check that the script is indeed loaded
        UI.getCurrent()
                .getPage()
                .executeJs("return document.querySelector('script[src*=\"" + library + "\"]').src")
                .then(src -> add(new Paragraph("script loaded "+src.asString())));
    }

    private void loadLibraryCustom(ClickEvent<Button> buttonClickEvent) {
        // Load some script
        String library = "mylib";
        String version = "1.0";
        String file = "mylib.js";
        String urlPattern = "/{library}-{version}/{file}";
        JSLoader.loadFiles(this, urlPattern, library, version, file);

        // Check that the script tag is included
        UI.getCurrent()
                .getPage()
                .executeJs("return document.querySelector('script[src*=\"" + library + "\"]').src")
                .then(src -> add(new Paragraph("script loaded "+src.asString())));

        // Check that the script is actually loaded and executed
        UI.getCurrent()
                .getPage()
                .executeJs("return document.querySelector('#mylib').textContent")
                .then(t -> add(new Paragraph("script said: "+t.asString())));

    }

    private void loadLocalModule(ClickEvent<Button> buttonClickEvent) {
        // Load some script
        String library = "mymodule";
        String version = "1.0";
        String file = "mymodule.mjs";
        String urlPattern = "/{library}-{version}/{file}";
        JSLoader.loadFiles(this, urlPattern, library, version, file);
        UI.getCurrent()
                .getPage()
                .executeJs("new mymodule.SampleClass2(); return 'mymodule ok';")
                .then(t -> add(new Paragraph("script said: "+t.asString())));

    }


    private void loadLibraryFromClasspath(ClickEvent<Button> buttonClickEvent) {
        // Load some script
        String libraryName = "jsresource";
        String file = "jsresource.js";
        JSLoader.loadJavaResource(UI.getCurrent(), TestView.class, libraryName, file);

        // Check that the script tag is included
        UI.getCurrent()
                .getPage()
                .executeJs("return document.querySelector('script[src*=\"" + libraryName + "\"]').src")
                .then(src -> add(new Paragraph("script loaded "+src.asString())));

        // Check that the script is actually loaded and executed
        UI.getCurrent()
                .getPage()
                .executeJs("return document.querySelector('#jsresource').textContent")
                .then(t -> add(new Paragraph("script said: "+t.asString())));

    }
}
