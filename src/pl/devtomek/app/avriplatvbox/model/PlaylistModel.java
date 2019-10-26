package pl.devtomek.app.avriplatvbox.model;

import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.List;
import java.util.Optional;

public class PlaylistModel {

    private List<IplaChannelModel> iplaChannelModelList;

    public PlaylistModel() {
        this.iplaChannelModelList = FXCollections.observableArrayList();
    }

    public TreeItem<IplaChannelModel> getTreeItem() {
        return new RecursiveTreeItem<>((ObservableList<IplaChannelModel>) iplaChannelModelList, RecursiveTreeObject::getChildren);
    }

    public void setIplaChannelModelList(List<IplaChannelModel> iplaChannelModelList) {
        this.iplaChannelModelList = iplaChannelModelList;
    }

    public Optional<IplaChannelModel> getIplaChannelByUrl(String url) {
        return iplaChannelModelList.stream()
                .filter(e -> e.getUrl().equalsIgnoreCase(url))
                .findFirst();
    }

}
