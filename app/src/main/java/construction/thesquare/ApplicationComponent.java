package construction.thesquare;

import dagger.Component;

/**
 * Created by gherg on 3/28/17.
 */

@Component(
        modules = ApplicationModule.class
)
interface ApplicationComponent {

    MainApplication injectApplication(MainApplication application);

}
