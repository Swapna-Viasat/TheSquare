package construction.thesquare;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gherg on 3/28/17.
 */

@Module
final class ActivityModule {

    private final Activity activity;

    ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    Activity activity() {
        return activity;
    }
}
