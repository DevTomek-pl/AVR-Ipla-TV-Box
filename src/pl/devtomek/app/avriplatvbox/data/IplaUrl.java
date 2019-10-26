package pl.devtomek.app.avriplatvbox.data;

import java.util.Objects;

public class IplaUrl {

    private String url;

    public IplaUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IplaUrl iplaUrl = (IplaUrl) o;
        return Objects.equals(url, iplaUrl.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
    
}
