package pl.devtomek.app.avriplatvbox.facade;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.value.ChangeListener;
import pl.devtomek.app.avriplatvbox.data.IplaUrl;
import pl.devtomek.app.avriplatvbox.model.IplaChannelModel;
import pl.devtomek.app.avriplatvbox.model.ProgramInfoModel;

/**
 * Facade responsible for playlist control represented as a {@link JFXTreeTableView} component.
 *
 * @author DevTomek.pl
 */
public interface PlaylistFacade {

    void init(JFXTreeTableView<IplaChannelModel> treeTableViewRef);

    void addVideoIdListener(ChangeListener<String> listener);

    void previousVideo();

    void nextVideo();

    void playVideo();

    void selectNextVideo();

    void selectPreviousVideo();

    void updateEpg();

    ProgramInfoModel getProgramInfoForUrl(IplaUrl url);

}
