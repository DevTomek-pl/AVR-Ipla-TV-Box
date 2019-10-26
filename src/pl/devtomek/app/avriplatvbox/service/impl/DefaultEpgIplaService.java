package pl.devtomek.app.avriplatvbox.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.devtomek.app.avriplatvbox.data.IplaUrl;
import pl.devtomek.app.avriplatvbox.model.IplaChannelModel;
import pl.devtomek.app.avriplatvbox.model.PlaylistModel;
import pl.devtomek.app.avriplatvbox.model.ProgramInfoModel;
import pl.devtomek.app.avriplatvbox.service.EpgIplaService;
import pl.devtomek.app.serial.Serial;
import pl.devtomek.app.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link EpgIplaService}.
 *
 * @author DevTomek.pl
 */
public class DefaultEpgIplaService implements EpgIplaService {

    private final Logger LOG = LogManager.getLogger(DefaultEpgIplaService.class);

    private static final String IPLA_TV_URL = "https://www.ipla.tv";
    private static final String IPLA_TV_CHANNELS_URL = "https://www.ipla.tv/kanaly-tv";
    private static final String X_PATH_TO_EPG_BOXES = "app-channel-now-on-tv-list-element > a[href^='/kanaly-tv/']";
    private static final String URL_HREF_ATTR = "href";
    private static final double DEFAULT_PROGRESS_VALUE = 0.0D;

    private Serial serial;

    public DefaultEpgIplaService(Serial serial) {
        this.serial = serial;
    }

    @Override
    public void updateEpg(PlaylistModel playlistModel, Map<IplaUrl, ProgramInfoModel> programInfoModelMap) {
        try {
            final Document document = Jsoup.parse(getWebsiteContentFromUrl(IPLA_TV_CHANNELS_URL));

            for (Element epgBox : document.select(X_PATH_TO_EPG_BOXES)) {

                String url = IPLA_TV_URL + epgBox.attr(URL_HREF_ATTR);
                Optional<IplaChannelModel> iplaChannelModel = playlistModel.getIplaChannelByUrl(url);

                if (iplaChannelModel.isPresent()) {
                    final ProgramInfoModel programInfoModel = getProgramInfoModel(epgBox);

                    programInfoModelMap.put(new IplaUrl(url), programInfoModel);

                    iplaChannelModel.get().updateEpg(programInfoModel.getChannel(), programInfoModel.getTitle(),
                            programInfoModel.getDescription(), programInfoModel.getDateRange(), getProgressInPercent(programInfoModel));
                }

            }

            LOG.info("Updated EPG information for [{}] channels", programInfoModelMap.size());
        } catch (IOException e) {
            LOG.error("Can not get EPG information from website [{}]", IPLA_TV_CHANNELS_URL);
        }
    }

    @Override
    public void updateAvrEpg(ProgramInfoModel programInfoModel, boolean isForceMode) {
        if (programInfoModel != null) {
            String message = programInfoModel.getChannel() +
                    ":" +
                    programInfoModel.getTitle() +
                    "|" + // separator
                    programInfoModel.getDateRange().replace(StringUtils.SPACE, StringUtils.EMPTY) +
                    "[" +
                    programInfoModel.getProgress() +
                    "%]" +
                    "|" + // separator
                    isForceMode;
            // sample message: "Discovery Channel HD:Lotnisko|16:00-17:00[74%]|true"
            serial.send(StringUtils.normalizePolishDialect(message));
            LOG.info("Updated EPG information in AVR module");
        }
    }

    private double getProgressInPercent(ProgramInfoModel programInfoModel) {
        try {
            return Double.parseDouble(programInfoModel.getProgress()) / 100;
        } catch (NumberFormatException e) {
            LOG.error("Error during convert the string progress value to double value");
            return DEFAULT_PROGRESS_VALUE;
        }
    }

    private String getWebsiteContentFromUrl(String siteUrl) throws IOException {
        URL url = new URL(siteUrl);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return in.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private ProgramInfoModel getProgramInfoModel(Element epgBox) {
        // dirty workaround for case when the ipla DOM structure will be changed
        try {
            String channel = epgBox.select(".date").get(0).getElementsByTag("span").get(1).text();
            String title = epgBox.select(".title").get(0).text();
            String description = epgBox.select(".description").get(0).text();
            String dateRange = epgBox.select(".date__range").get(0).text();
            String progress = epgBox.select(".current-progress-bar").get(0).attr("style").replaceAll("[^\\d]", "");
//        LOG.debug("\n{}\n{}\n{}\n{}\n{}\n\n", channel, title, description, dateRange, progress);
            return new ProgramInfoModel(channel, title, description, dateRange, progress);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            LOG.error("Something went wrong during parse Ipla DOM structure for extract EPG informations [{}]", e.getMessage());
            return new ProgramInfoModel(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
        }
    }

}
