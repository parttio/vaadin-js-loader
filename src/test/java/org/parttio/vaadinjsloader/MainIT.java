package org.parttio.vaadinjsloader;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MainIT {

    static Playwright playwright = Playwright.create(); // <4>
    private String url = "http://localhost:8099/test/";
    private BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(true);

    @Test
    public void cdnjsLoads() {
        try (Browser browser = playwright.chromium().launch(options)) { // <5>
            Page page = browser.newPage();
            page.navigate(url);
            page.getByText("Load from cdnjs.com").first().click();
            assertThat(page.getByText("loaded https://cdnjs")).isVisible(); // <8>
        }
    }

    @Test
    public void unpkgLoads() {
        try (Browser browser = playwright.chromium().launch(options)) { // <5>
            Page page = browser.newPage();
            page.navigate(url);
            page.getByText("Load from unpkg.com").first().click();
            assertThat(page.getByText("loaded https://unpkg.com/")).isVisible(); // <8>
        }
    }

    @Test
    public void localLoads() {
        try (Browser browser = playwright.chromium().launch(options)) { // <5>
            Page page = browser.newPage();
            page.navigate(url);
            page.getByText("Load local library").first().click();
            assertThat(page.getByText("loaded http://localhost")).isVisible(); // <8>
        }
    }

    @Test
    public void localJavaResourceLoads() {
        try (Browser browser = playwright.chromium().launch(options)) { // <5>
            Page page = browser.newPage();
            page.navigate(url);
            page.getByText("Load library from classpath").first().click();
            assertThat(page.getByText("loaded http://localhost")).isVisible(); // <8>
        }
    }
}