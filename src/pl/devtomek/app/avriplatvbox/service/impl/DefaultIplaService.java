package pl.devtomek.app.avriplatvbox.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pl.devtomek.app.avriplatvbox.constant.PathConstant;
import pl.devtomek.app.avriplatvbox.data.IplaUrl;
import pl.devtomek.app.avriplatvbox.facade.PlaylistFacade;
import pl.devtomek.app.avriplatvbox.model.ProgramInfoModel;
import pl.devtomek.app.avriplatvbox.service.IplaService;
import pl.devtomek.app.avriplatvbox.service.constant.MaterialIcon;
import pl.devtomek.app.utils.IOUtils;
import pl.devtomek.app.utils.PropertiesUtils;
import pl.devtomek.app.utils.constant.Property;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default implementation of {@link IplaService}.
 *
 * @author DevTomek.pl
 */
public class DefaultIplaService implements IplaService {

    private static final String IPLA_LOG_IN_URL = "https://www.ipla.tv/uzytkownik/zaloguj";
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 20;

    private final Logger LOG = LogManager.getLogger(DefaultIplaService.class);

    private PlaylistFacade playlistFacade;

    private String javaScriptContent;
    private String currentUrl;
    private WebDriver driver;
    private int brightness;
    private boolean isUserLoggedIn;
    private AtomicBoolean isLoadChannelInProgress;

    public DefaultIplaService(PlaylistFacade playlistFacade) {
        this.playlistFacade = playlistFacade;
    }

    @Override
    public void initialize() {
        initializeWebBrowserDriver();

        loadDefaultPage();

        loadJavaScriptContent();

        brightness = 100;
        isUserLoggedIn = false;
        isLoadChannelInProgress = new AtomicBoolean();
    }

    private void loadJavaScriptContent() {
        javaScriptContent = IOUtils.readTextFromResources(PathConstant.SCRIPT_JS, false)
                .replace("${HEADER_BIG_FONT_SIZE}", PropertiesUtils.getProperty(Property.HEADER_BIG_FONT_SIZE, "32"))
                .replace("${HEADER_SMALL_FONT_SIZE}", PropertiesUtils.getProperty(Property.HEADER_SMALL_FONT_SIZE, "26"))
                .replace("${OPACITY}", PropertiesUtils.getProperty(Property.OPACITY, "0.86"));
    }

    private void initializeWebBrowserDriver() {
        final String driverPath = IOUtils.getRootPath() + PathConstant.CHROME_DRIVER;

        File file = new File(driverPath);

        if (file.exists()) {

            if (!file.canExecute()) {
                LOG.info("Sets executable [{}] from chrome driver file [{}]", file.setExecutable(true), driverPath);
            }

            System.setProperty("webdriver.chrome.driver", driverPath);
            LOG.info("Loaded web browser driver from [{}]", driverPath);

        } else {
            LOG.error("Not found chrome driver [{}]", driverPath);
        }

        driver = new ChromeDriver(getChromeOptions());
    }

    private void loadDefaultPage() {
        String pathToDefaultHtmlPage = "file://" + IOUtils.getRootPath() + PathConstant.DEFAULT_HTML_START_PAGE;
        LOG.info("Get to URL: {}", pathToDefaultHtmlPage);
        driver.get(pathToDefaultHtmlPage);
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments(
                "--autoplay-policy=no-user-gesture-required",
                "--disable-notifications",
                "disable-infobars"
        );

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

        if (PropertiesUtils.isProductionMode()) {
            options.addArguments("--start-fullscreen");
        }

        return options;
    }

    @Override
    public void login() {

        if (isUserLoggedIn) {
            LOG.warn("The user is logged in and can not log in again");
            return;
        }

        final Runnable loginTask = () -> {
            try {
                LOG.info("Starting log in to Ipla service...");
                driver.get(IPLA_LOG_IN_URL);

                WebElement acceptRodoButton = new WebDriverWait(driver, DEFAULT_TIMEOUT_IN_SECONDS)
                        .until(ExpectedConditions.visibilityOfElementLocated(By.className("rodo-button")));

                if (acceptRodoButton.isDisplayed()) {
                    acceptRodoButton.click();
                }

                final String login = PropertiesUtils.getProperty(Property.LOGIN);
                driver.findElement(By.id("native-login-input")).sendKeys(login);

                final String password = PropertiesUtils.getProperty(Property.PASSWORD);
                driver.findElement(By.id("native-password-input")).sendKeys(password);

                Thread.sleep(1000);

                WebElement loginButton = new WebDriverWait(driver, DEFAULT_TIMEOUT_IN_SECONDS)
                        .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//main/app-login-page/app-login/div/div/form/button")));

                if (loginButton.isDisplayed()) {
                    loginButton.click();
                }

                isUserLoggedIn = true;
                LOG.info("Successfully logged in to Ipla service");
            } catch (TimeoutException e) {
                LOG.error("Timeout while login to Ipla account");
            } catch (NoSuchElementException e) {
                LOG.error("Not found login button");
            } catch (ElementNotVisibleException e) {
                LOG.error("Login button is not visible");
            } catch (InterruptedException e) {
                LOG.error("Interrupted exception while login to Ipla account");
            }
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(loginTask);
        executorService.shutdown();
    }

    @Override
    public void close() {
        try {
            driver.close();
        } catch (WebDriverException e) {
            LOG.error("Can not close Chrome browser, because the browser is not reachable");
        }
    }

    @Override
    public void loadChannel(String url) {
        if (isLoadChannelInProgress.get()) {
            LOG.warn("Load channel is in progress, please wait until it finish");
            return;
        }

        final Runnable loadChannelTask = () -> {
            try {
                LOG.info("Started loading channel... [{}]", url);
                isLoadChannelInProgress.set(true);
                setCurrentUrl(url);
                driver.get(url);

                if (isUserLoggedIn) {

                    WebElement document = new WebDriverWait(driver, DEFAULT_TIMEOUT_IN_SECONDS)
                            .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/app-default-layout/div/main/app-channel-list-page/app-player/div/div/div/div/div/div[1]/video")));

                    if (document.isDisplayed()) {
                        runScript("document.innerHTML = " + populateJavaScript(javaScriptContent));
                        displaySnackbarMessageWithCurrentProgram(new IplaUrl(url));
                    }

                    LOG.info("Loaded TV channel [{}]", url);
                }

            } catch (TimeoutException e) {
                LOG.error("Timeout while loading the channel [{}]", url);
            } finally {
                isLoadChannelInProgress.set(false);
            }
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(loadChannelTask);
        executorService.shutdown();
    }

    private String populateJavaScript(String javaScriptContent) {
        return javaScriptContent.replace("${BRIGHTNESS}", String.valueOf(brightness));
    }

    @Override
    public void displayInfo() {
        displaySnackbarMessageWithCurrentProgram(new IplaUrl(currentUrl));
    }

    @Override
    public void play() {
        runScript("play();");
    }

    @Override
    public void pause() {
        runScript("pause();");
    }

    @Override
    public void fullscreen() {
        runScript("toggleFullscreen();");
    }

    @Override
    public void goToNow() {
        runScript("goToNow();");
    }

    @Override
    public void rewind() {
        runScript("changePlayerTime(-10);");
    }

    @Override
    public void forward() {
        runScript("changePlayerTime(10);");
    }

    @Override
    public void fastRewind() {
        runScript("changePlayerTime(-300);");
    }

    @Override
    public void fastForward() {
        runScript("changePlayerTime(300);");
    }

    @Override
    public void incrementBrightness() {
        brightness += 2;
        runScript("setBrightness(" + brightness + ");\n" +
                "showSnackbar('<h1>Brightness: " + brightness + "%</h1>')");
    }

    @Override
    public void decrementBrightness() {
        brightness -= 2;
        runScript("setBrightness(" + brightness + ");\n" +
                "showSnackbar('<h1>Brightness: " + brightness + "%</h1>')");
    }

    @Override
    public void resetBrightness() {
        brightness = 100;
        runScript("setBrightness(" + brightness + ");\n" +
                "showSnackbar('<h1>Brightness: " + brightness + "%</h1>')");
    }

    @Override
    public void displayMessage(String message) {
        runScript("showSnackbar('<h1>" + message + "</h1>');");
    }

    @Override
    public void displayIconMessage(MaterialIcon icon) {
        runScript("displayIcon('" + icon + "');");
    }

    @Override
    public void changeResolution() {
        runScript("changeResolution();");
    }

    private void runScript(String script) {
        try {
            LOG.debug("Running script: [{}]", script);
            Object result = ((JavascriptExecutor) driver).executeScript(script);
            LOG.debug("Result: [{}]", result);
        } catch (Exception e) {
            LOG.error("Something went wrong during run script [{}] \n [{}]", script, e.getMessage());
        }
    }

    private void displaySnackbarMessageWithCurrentProgram(IplaUrl url) {
        ProgramInfoModel programInfoForUrl = playlistFacade.getProgramInfoForUrl(url);

        if (programInfoForUrl != null) {
            runScript("displaySnackbarMessageWithCurrentProgram('" +
                    programInfoForUrl.getChannel() + "','" +
                    programInfoForUrl.getDateRange() + "','" +
                    programInfoForUrl.getProgress() + "','" +
                    programInfoForUrl.getTitle() + "')");
        }
    }

    synchronized private void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

}
