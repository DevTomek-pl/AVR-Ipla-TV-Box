package pl.devtomek.app.avriplatvbox.service;

import pl.devtomek.app.avriplatvbox.data.IplaUrl;
import pl.devtomek.app.avriplatvbox.model.PlaylistModel;
import pl.devtomek.app.avriplatvbox.model.ProgramInfoModel;

import java.util.Map;

/**
 * Service responsible for providing and updating EPG informations.
 *
 * @author DevTomek.pl
 */
public interface EpgIplaService {

    /**
     * Updates EPG informations.
     *
     * @param playlistModel       playlist with channels for updating EPG informations.
     * @param programInfoModelMap map with data to be updated.
     */
    void updateEpg(PlaylistModel playlistModel, Map<IplaUrl, ProgramInfoModel> programInfoModelMap);

    /**
     * Updates information on LCD (HD44780).
     *
     * @param programInfoModel information to be displayed on LCD.
     * @param isForceMode      if true, then the information on the LCD will be displayed immediately.
     */
    void updateAvrEpg(ProgramInfoModel programInfoModel, boolean isForceMode);

}
