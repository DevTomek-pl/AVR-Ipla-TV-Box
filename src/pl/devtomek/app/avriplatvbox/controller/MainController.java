package pl.devtomek.app.avriplatvbox.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.devtomek.app.avriplatvbox.constant.ButtonConstant;
import pl.devtomek.app.avriplatvbox.constant.FxConstant;
import pl.devtomek.app.avriplatvbox.facade.PlaylistFacade;
import pl.devtomek.app.avriplatvbox.model.EpgModel;
import pl.devtomek.app.avriplatvbox.model.IplaChannelModel;
import pl.devtomek.app.avriplatvbox.service.IplaService;
import pl.devtomek.app.avriplatvbox.service.SoundService;
import pl.devtomek.app.avriplatvbox.service.constant.MaterialIcon;
import pl.devtomek.app.serial.Serial;
import pl.devtomek.app.utils.PropertiesUtils;
import pl.devtomek.app.utils.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller for GUI.
 *
 * @author DevTomek.pl
 */
public class MainController implements Initializable {

    private final Logger LOG = LogManager.getLogger(MainController.class);

    private static final String SHUTDOWN_SYSTEM_COMMAND = "poweroff";

    @FXML
    private JFXTreeTableView<IplaChannelModel> playlistView;

    @FXML
    private JFXTextArea descriptionTextArea;

    @FXML
    private AnchorPane bottomAnchorPane;

    private PlaylistFacade playlistFacade;
    private IplaService iplaService;
    private SoundService soundService;
    private Serial serial;

    private Stage primaryStage;

    public MainController(PlaylistFacade playlistFacade, IplaService iplaService, SoundService soundService, Serial serial) {
        this.playlistFacade = playlistFacade;
        this.iplaService = iplaService;
        this.soundService = soundService;
        this.serial = serial;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playlistFacade.init(playlistView);
        playlistFacade.addVideoIdListener((observable, oldValue, newValue) -> loadChannelByUrl(newValue));

        playlistView.setOnMouseClicked(this::loadChannelByMouseClickedEventHandler);
        playlistView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> displayEpgInfo(newValue.getValue()));

        registerSerialCommunicationCommands();

        if (PropertiesUtils.isProductionMode()) {
            setAnchorPaneHeight(bottomAnchorPane, 0);
            iplaService.login();
        }
    }

    private void registerSerialCommunicationCommands() {
        serial.register(ButtonConstant.OK, soundService::playClick, playlistFacade::playVideo);
        serial.register(ButtonConstant.DOWN, soundService::playSelect, playlistFacade::selectNextVideo);
        serial.register(ButtonConstant.UP, soundService::playSelect, playlistFacade::selectPreviousVideo);
        serial.register(ButtonConstant.YELLOW, soundService::playClick, playlistFacade::updateEpg, iplaService::displayInfo);
        serial.register(ButtonConstant.GREEN, soundService::playClick, iplaService::login);
        serial.register(ButtonConstant.POP_UP, soundService::playSwipe, iplaService::fullscreen);
        serial.register(ButtonConstant.PLAY, soundService::playClick, iplaService::play);
        serial.register(ButtonConstant.PAUSE, soundService::playClick, iplaService::pause);
        serial.register(ButtonConstant.STOP, soundService::playClick, iplaService::goToNow);
        serial.register(ButtonConstant.TUNING_PLUS, soundService::playClick, iplaService::forward);
        serial.register(ButtonConstant.TUNING_MINUS, soundService::playClick, iplaService::rewind);
        serial.register(ButtonConstant.PRESET_PLUS, soundService::playClick, iplaService::fastForward);
        serial.register(ButtonConstant.PRESET_MINUS, soundService::playClick, iplaService::fastRewind);
        serial.register(ButtonConstant.OPTIONS, soundService::playClick, iplaService::changeResolution);
        serial.register(ButtonConstant.TV_VOL_UP, soundService::playClick, iplaService::incrementBrightness);
        serial.register(ButtonConstant.TV_VOL_DOWN, soundService::playClick, iplaService::decrementBrightness);
        serial.register(ButtonConstant.TV_VOL_MUTE, soundService::playClick, iplaService::resetBrightness);
        serial.register(ButtonConstant.RIGHT, soundService::playSwipe, this::showApp);
        serial.register(ButtonConstant.LEFT, soundService::playSwipe, this::hideApp);
        serial.register(ButtonConstant.BLUE, soundService::playSwipe, this::toggleEpgDescriptionPanel);
        serial.register(ButtonConstant.TVI_ON_OFF, soundService::playClick, this::shutdownSystem);
        serial.register(ButtonConstant.RED, this::soundMute, soundService::playClick);
    }

    private void soundMute() {
        soundService.mute();
        iplaService.displayIconMessage(soundService.isMuted() ? MaterialIcon.VOLUME_OFF : MaterialIcon.VOLUME_UP);
    }

    private void shutdownSystem() {
        try {
            LOG.info("Shutting down the system...");
            Runtime.getRuntime().exec(SHUTDOWN_SYSTEM_COMMAND);
        } catch (IOException e) {
            LOG.error("Something went wrong while shutdown the system");
        }
    }

    private void toggleEpgDescriptionPanel() {
        setAnchorPaneHeight(bottomAnchorPane, bottomAnchorPane.getHeight() != 0 ? 0 : FxConstant.EPG_DESCRIPTION_HEIGHT);
    }

    private void setAnchorPaneHeight(AnchorPane anchorPane, double height) {
        anchorPane.setMinHeight(height);
        anchorPane.setMaxHeight(height);
    }

    private void displayEpgInfo(IplaChannelModel iplaChannelModel) {
        StringBuilder epgContent = new StringBuilder();
        EpgModel epgModel = iplaChannelModel.getEpg();
        epgContent
                .append(epgModel.getDateRange())
                .append(" | ")
                .append(epgModel.getTitle())
                .append(StringUtils.NEW_LINE)
                .append(StringUtils.NEW_LINE)
                .append(epgModel.getDescription());

        descriptionTextArea.setText(epgContent.toString());
    }

    private void showApp() {
        Platform.runLater(() -> {
            primaryStage.setIconified(true);
            primaryStage.setIconified(false);
        });
    }

    private void hideApp() {
        Platform.runLater(() -> {
            primaryStage.setIconified(false);
            primaryStage.setIconified(true);
        });
    }

    private void loadChannelByMouseClickedEventHandler(MouseEvent event) {
        if (event.getClickCount() == 2 && event.getSource() instanceof JFXTreeTableView) {

            final Object selectedItem = ((JFXTreeTableView) event.getSource()).getSelectionModel().getSelectedItem();
            if (selectedItem instanceof RecursiveTreeItem) {

                final Object iplaChannelModel = ((RecursiveTreeItem) selectedItem).getValue();
                if (iplaChannelModel instanceof IplaChannelModel) {
                    loadChannelByUrl(((IplaChannelModel) iplaChannelModel).getUrl());
                }

            }

        }
    }

    private void loadChannelByUrl(String channelUrl) {
        iplaService.loadChannel(channelUrl);
    }

    public void stageCloseEvent(WindowEvent event) {
        iplaService.close();
        if (serial != null) {
            serial.close();
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

}