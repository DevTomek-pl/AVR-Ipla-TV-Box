package pl.devtomek.app.avriplatvbox.model;

public class ProgramInfoModel {

    private String channel;
    private String title;
    private String description;
    private String dateRange;
    private String progress;

    public ProgramInfoModel(String channel, String title, String description, String dateRange, String progress) {
        this.channel = channel;
        this.title = title;
        this.description = description;
        this.dateRange = dateRange;
        this.progress = progress;
    }

    public String getChannel() {
        return channel;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDateRange() {
        return dateRange;
    }

    public String getProgress() {
        return progress;
    }
    
}
