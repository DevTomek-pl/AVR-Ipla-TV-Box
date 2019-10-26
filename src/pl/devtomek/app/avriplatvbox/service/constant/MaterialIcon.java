package pl.devtomek.app.avriplatvbox.service.constant;

/**
 * Material icons for snackbar component.
 *
 * NOTE: List of all available icons: https://material.io/tools/icons
 *
 * @author DevTomek.pl
 */
public enum MaterialIcon {

    VOLUME_OFF,
    VOLUME_UP;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
