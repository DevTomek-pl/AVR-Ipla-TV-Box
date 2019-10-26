package pl.devtomek.app.avriplatvbox.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;
import pl.devtomek.app.utils.StringUtils;

public class IplaChannelModel extends RecursiveTreeObject<IplaChannelModel> {
    private SimpleObjectProperty<ImageView> icon;
    private SimpleObjectProperty<EpgModel> epg;
    private StringProperty url;
    private StringProperty name;

    public IplaChannelModel() {
        this.icon = new SimpleObjectProperty<>(new ImageView());
        this.epg = new SimpleObjectProperty<>(new EpgModel());
        this.url = new SimpleStringProperty(StringUtils.EMPTY);
        this.name = new SimpleStringProperty(StringUtils.EMPTY);
    }

    public IplaChannelModel(String name, ImageView icon, String url) {
        this.name = new SimpleStringProperty(name);
        this.icon = new SimpleObjectProperty<>(icon);
        this.epg = new SimpleObjectProperty<>(new EpgModel());
        this.url = new SimpleStringProperty(url);
    }

    public String getName() {
        return name.get();
    }

    public String getUrl() {
        return url.get();
    }

    public SimpleObjectProperty<ImageView> iconProperty() {
        return icon;
    }

    public EpgModel getEpg() {
        return epg.get();
    }

    public void updateEpg(String channel, String title, String description, String dateRange, double progress) {
        this.epg.getValue().setChannel(channel);
        this.epg.getValue().setTitle(title);
        this.epg.getValue().setDescription(description);
        this.epg.getValue().setDateRange(dateRange);
        this.epg.getValue().setProgress(progress);
    }

}

