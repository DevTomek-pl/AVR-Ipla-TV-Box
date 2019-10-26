package pl.devtomek.app.avriplatvbox.facade.impl;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.skins.JFXTreeTableViewSkin;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.devtomek.app.avriplatvbox.constant.FxConstant;
import pl.devtomek.app.avriplatvbox.constant.PathConstant;
import pl.devtomek.app.avriplatvbox.data.Channel;
import pl.devtomek.app.avriplatvbox.data.ChannelData;
import pl.devtomek.app.avriplatvbox.data.IplaUrl;
import pl.devtomek.app.avriplatvbox.data.Item;
import pl.devtomek.app.avriplatvbox.facade.PlaylistFacade;
import pl.devtomek.app.avriplatvbox.facade.status.PlaylistStatus;
import pl.devtomek.app.avriplatvbox.model.IplaChannelModel;
import pl.devtomek.app.avriplatvbox.model.PlaylistModel;
import pl.devtomek.app.avriplatvbox.model.ProgramInfoModel;
import pl.devtomek.app.avriplatvbox.service.EpgIplaService;
import pl.devtomek.app.utils.IOUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default implementation of {@link PlaylistFacade}.
 *
 * @author DevTomek.pl
 */
public class DefaultPlaylistFacade implements PlaylistFacade {

    private final Logger LOG = LogManager.getLogger(DefaultPlaylistFacade.class);

    private EpgIplaService epgIplaService;
    private PlaylistModel playlistModel;
    private JFXTreeTableView<IplaChannelModel> treeTableViewRef;
    private IplaChannelModel selectedIplaChannelModel;
    private AtomicInteger selectedChannelIndex;
    private StringProperty currentChannelUrl;
    private Map<IplaUrl, ProgramInfoModel> programInfoModelMap;

    public DefaultPlaylistFacade(EpgIplaService epgIplaService) {
        this.epgIplaService = epgIplaService;
    }

    @Override
    public void init(JFXTreeTableView<IplaChannelModel> treeTableViewRef) {
        this.treeTableViewRef = treeTableViewRef;
        this.currentChannelUrl = new SimpleStringProperty();
        this.programInfoModelMap = new HashMap<>();
        this.selectedChannelIndex = new AtomicInteger(0);

        loadChannelsFromJsonFile(PathConstant.CHANNELS_JSON);

        JFXTreeTableColumn<IplaChannelModel, ImageView> logoColumn = new JFXTreeTableColumn<>(FxConstant.CHANNELS_COLUMN_TITLE);
        logoColumn.setContextMenu(null);
        logoColumn.setPrefWidth(FxConstant.CHANNELS_COLUMN_WIDTH);
        logoColumn.setCellValueFactory(param -> param.getValue().getValue().iconProperty());

        JFXTreeTableColumn<IplaChannelModel, String> titleColumn = new JFXTreeTableColumn<>(FxConstant.TITLE_COLUMN_TITLE);
        titleColumn.setContextMenu(null);
        titleColumn.setPrefWidth(FxConstant.TITLE_COLUMN_WIDTH);
        titleColumn.setCellValueFactory(param -> param.getValue().getValue().getEpg().titleProperty());

        JFXTreeTableColumn<IplaChannelModel, ProgressBar> progressColumn = new JFXTreeTableColumn<>(FxConstant.PROGRESS_COLUMN_TITLE);
        progressColumn.setContextMenu(null);
        progressColumn.setPrefWidth(FxConstant.PROGRESS_COLUMN_WIDTH);
        progressColumn.setCellValueFactory(param -> param.getValue().getValue().getEpg().progressBarPropertyProperty());

        ObservableList<TreeTableColumn<IplaChannelModel, ?>> columns = treeTableViewRef.getColumns();
        columns.add(0, logoColumn);
        columns.add(1, titleColumn);
        columns.add(2, progressColumn);

        treeTableViewRef.setRoot(playlistModel.getTreeItem());
        treeTableViewRef.setShowRoot(false);
    }

    private void loadChannelsFromJsonFile(String pathToFile) {
        playlistModel = new PlaylistModel();
        List<IplaChannelModel> iplaChannelModelList = FXCollections.observableArrayList();

        List<ChannelData> channelList = IOUtils.mapToJson(pathToFile, ChannelData.class, true);

        for (ChannelData channelData : channelList) {
            for (Channel channel : channelData.getChannels()) {
                Item item = channel.getItem();
                iplaChannelModelList.add(new IplaChannelModel(item.getName(), getIconFromImageFile(item.getIcon()), item.getUrl()));
            }
        }

        playlistModel.setIplaChannelModelList(iplaChannelModelList);
    }

    private ImageView getIconFromImageFile(String pathToIcon) {
        InputStream iconStream = IOUtils.getExternalResourcesAsStream(pathToIcon);
        return new ImageView(new Image(iconStream));
    }

    @Override
    public void addVideoIdListener(ChangeListener<String> listener) {
        currentChannelUrl.addListener(listener);
    }

    private PlaylistStatus selectItem(int index) {

        final int minPlaylistIndex = 0;
        final int maxPlaylistIndex = treeTableViewRef.getCurrentItemsCount() - 1;

        PlaylistStatus playlistStatus = PlaylistStatus.SUCCESS;

        if (maxPlaylistIndex < minPlaylistIndex) {
            return PlaylistStatus.PLAYLIST_IS_EMPTY;
        }

        if (index > maxPlaylistIndex) {
            index = 0;
            playlistStatus = PlaylistStatus.CHANNEL_NOT_FOUND;
        }

        if (index < minPlaylistIndex) {
            index = maxPlaylistIndex;
            playlistStatus = PlaylistStatus.CHANNEL_NOT_FOUND;
        }

        treeTableViewRef.getSelectionModel().select(index);

        int firstVisibleCellIndex = ((VirtualFlow) ((JFXTreeTableViewSkin) treeTableViewRef.getSkin()).getChildren().get(1)).getFirstVisibleCell().getIndex();
        int lastVisibleCellIndex = ((VirtualFlow) ((JFXTreeTableViewSkin) treeTableViewRef.getSkin()).getChildren().get(1)).getLastVisibleCell().getIndex();

        if (lastVisibleCellIndex <= index || firstVisibleCellIndex >= index) {
            treeTableViewRef.scrollTo(index);
        }

        selectedIplaChannelModel = treeTableViewRef.getSelectionModel().getSelectedItem().getValue();
        selectedChannelIndex.set(index);

        return playlistStatus;
    }

    private void selectItemAndPlayChannel(int index) {
        final PlaylistStatus STATUS = selectItem(index);
        if (STATUS == PlaylistStatus.SUCCESS || STATUS == PlaylistStatus.CHANNEL_NOT_FOUND) {
            currentChannelUrl.setValue(selectedIplaChannelModel.getUrl());
            epgIplaService.updateAvrEpg(getProgramInfoForUrl(new IplaUrl(currentChannelUrl.get())), true);
            LOG.info("Loaded channel: {}", selectedIplaChannelModel.getUrl());
        }
    }

    @Override
    public void previousVideo() {
        selectItemAndPlayChannel(selectedChannelIndex.decrementAndGet());
    }

    @Override
    public void nextVideo() {
        selectItemAndPlayChannel(selectedChannelIndex.incrementAndGet());
    }

    @Override
    public void playVideo() {
        selectItemAndPlayChannel(selectedChannelIndex.get());
    }

    @Override
    public void selectNextVideo() {
        Platform.runLater(() -> selectItem(selectedChannelIndex.incrementAndGet()));
    }

    @Override
    public void selectPreviousVideo() {
        Platform.runLater(() -> selectItem(selectedChannelIndex.decrementAndGet()));
    }

    @Override
    public void updateEpg() {
        epgIplaService.updateEpg(playlistModel, programInfoModelMap);
        epgIplaService.updateAvrEpg(getProgramInfoForUrl(new IplaUrl(currentChannelUrl.get())), false);
    }

    @Override
    public ProgramInfoModel getProgramInfoForUrl(IplaUrl url) {
        return programInfoModelMap.get(url);
    }

}
