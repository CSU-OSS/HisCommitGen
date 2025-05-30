package demo;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "MyPluginSettings",
        storages = {@Storage("cmgPlungingSettings.xml")}
)
@Service(Service.Level.APP)
public final class MyPluginSettings implements PersistentStateComponent<MyPluginSettings.State> {

    private State myState = new State();

    @Override
    public @Nullable MyPluginSettings.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.myState = state;
    }

    static class State {
        public Boolean useHistoryOrNot = false;

        public Boolean needRec = false;

        public String temperature = "0.9";

        public String maxToken = "100";

    }

    public static MyPluginSettings getInstance() {
        return ApplicationManager.getApplication().getService(MyPluginSettings.class);
    }
}
