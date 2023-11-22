package org.parttio.vaadinjsloader;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

@Route
public class TestView extends VerticalLayout {

    public TestView() {
        add(new H1("Addon test"));
        add(new Button("Load from cdnjs.com", this::loadLibraryCdnJs));
        add(new Button("Load from unpkg.com", this::loadLibraryUnpkg));
        add(new Button("Load local", this::loadLibraryCustom));
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

        // Check that the script is indeed loaded
        UI.getCurrent()
                .getPage()
                .executeJs("return document.querySelector('script[src*=\"" + library + "\"]').src")
                .then(src -> add(new Paragraph("script loaded "+src.asString())));
    }
}
