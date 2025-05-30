package demo;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

import java.util.List;

@Service
public final class MyService {
    private List<String> commit_messages;

    public List<String> getGlobalVariable() {
        return commit_messages;
    }

    public void setGlobalVariable(List<String> commit_messages) {
        this.commit_messages = commit_messages;
    }

    public static MyService getInstance() {
        return ApplicationManager.getApplication().getService(MyService.class);
    }}
