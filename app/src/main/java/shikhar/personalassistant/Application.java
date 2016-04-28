package shikhar.personalassistant;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "Raleway-Light.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Lato-Semibold.ttf");
        FontsOverride.setDefaultFont(this, "SANS", "Lato-Regular.ttf");
    }
}
