package pl.devtomek.app.avriplatvbox.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ProgressBar;
import pl.devtomek.app.avriplatvbox.constant.FxConstant;
import pl.devtomek.app.utils.StringUtils;

public class EpgModel {

    private String channel;
    private String description;
    private String dateRange;
    private StringProperty title;
    private ProgressBar progressBar;
    private SimpleObjectProperty<ProgressBar> progressBarProperty;

    public EpgModel() {
        this.channel = StringUtils.EMPTY;
        this.title = new SimpleStringProperty(StringUtils.EMPTY);
        this.description = StringUtils.EMPTY;
        this.dateRange = StringUtils.EMPTY;
        progressBar = new ProgressBar();
        progressBar.setProgress(0);
        progressBar.setPrefWidth(FxConstant.PROGRESS_COLUMN_WIDTH);
        this.progressBarProperty = new SimpleObjectProperty<>(progressBar);
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }

    public SimpleObjectProperty<ProgressBar> progressBarPropertyProperty() {
        return progressBarProperty;
    }

}

