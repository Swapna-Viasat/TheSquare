package construction.thesquare;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gherg on 3/28/17.
 */

@Module
final class ApplicationModule {

    private final Application application;

    ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Application application() {
        return application;
    }
}
