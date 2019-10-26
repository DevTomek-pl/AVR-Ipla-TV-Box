package pl.devtomek.app.avriplatvbox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.devtomek.app.avriplatvbox.constant.FxConstant;
import pl.devtomek.app.avriplatvbox.constant.PathConstant;
import pl.devtomek.app.avriplatvbox.controller.MainController;
import pl.devtomek.app.utils.IOUtils;
import pl.devtomek.app.utils.PropertiesUtils;
import pl.devtomek.app.utils.constant.Property;

import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;

/**
 * AVR Ipla TV Box Application.
 *
 * @author DevTomek.pl
 */
public class AvrIplaTvBoxApp extends Application {

    private static final Logger LOG = LogManager.getLogger(AvrIplaTvBoxApp.class);

    private ConfigurableApplicationContext springContext;
    private FXMLLoader fxmlLoader;

    public static void main(String[] args) {
        LOG.info("Starting {} application...", FxConstant.APP_TITLE);
        LOG.info("\u001b[0;34m" + IOUtils.readTextFromResources(PathConstant.SPLASH_SCREEN, false) + "\u001b[m ");

        try {
            System.setProperty("java.library.path", "/usr/lib/jni");
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (NoSuchFieldException e) {
            LOG.error("Not found 'sys_paths' field [{}]", e.getMessage());
        } catch (IllegalAccessException e) {
            LOG.error("Can not set value for 'sys_paths' field [{}]", e.getMessage());
        }

        launch(args);
    }

    @Override
    public void init() {
        springContext = new ClassPathXmlApplicationContext("spring-beans.xml");
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(springContext::getBean);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL mainViewResource = getClass().getClassLoader().getResource("view/MainView.fxml");
        LOG.info("Loading main view from [{}]", mainViewResource);
        fxmlLoader.setLocation(mainViewResource);

        Parent root = fxmlLoader.load();

        registerStageCloseEvent(primaryStage, fxmlLoader);

        preparePrimaryStage(primaryStage, root);

        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        springContext.stop();
    }

    private void preparePrimaryStage(Stage primaryStage, Parent root) throws AWTException {
        LOG.info("Preparing primary stage...");

        int offsetX = PropertiesUtils.getIntegerProperty(Property.OFFSET_X);
        primaryStage.setX(offsetX);

        int offsetY = PropertiesUtils.getIntegerProperty(Property.OFFSET_Y);
        primaryStage.setY(offsetY);

        double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight() - (2 * offsetY);

        Scene scene = new Scene(root, FxConstant.APP_WIDTH, height);

        addCssStyleAndStageIcon(primaryStage, scene);

        if (PropertiesUtils.isProductionMode()) {
            LOG.info("Application started in production mode");
            primaryStage.setMinWidth(FxConstant.APP_WIDTH);
            primaryStage.setMinHeight(height);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            new Robot().mouseMove(((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()), ((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
        } else {
            LOG.info("Application started in development mode");
        }

        primaryStage.setScene(scene);

        primaryStage.setTitle(FxConstant.APP_TITLE);

        primaryStage.setOpacity(PropertiesUtils.getDoubleProperty(Property.OPACITY, 1.0));

        primaryStage.setAlwaysOnTop(true);
    }

    private void addCssStyleAndStageIcon(Stage primaryStage, Scene scene) {
        ClassLoader classLoader = getClass().getClassLoader();

        if (classLoader != null) {

            URL cssStyleResource = classLoader.getResource(PathConstant.JAVAFX_CSS_STYLE);

            if (cssStyleResource != null) {
                scene.getStylesheets().add(cssStyleResource.toExternalForm());
            } else {
                LOG.error("Not found [{}]", PathConstant.JAVAFX_CSS_STYLE);
            }

            InputStream stageIconResourceStream = IOUtils.getResourcesAsStream(PathConstant.APP_ICON);

            if (stageIconResourceStream != null) {
                primaryStage.getIcons().add(new Image(stageIconResourceStream));
            } else {
                LOG.error("Not found [{}]", PathConstant.APP_ICON);
            }

        } else {
            LOG.error("Unable to get class loader. CSS style and stage icon can not be set.");
        }
    }

    private void registerStageCloseEvent(Stage primaryStage, FXMLLoader fxmlLoader) {
        MainController mainController = fxmlLoader.getController();
        mainController.setPrimaryStage(primaryStage);
        primaryStage.setOnCloseRequest(mainController::stageCloseEvent);
    }

}
