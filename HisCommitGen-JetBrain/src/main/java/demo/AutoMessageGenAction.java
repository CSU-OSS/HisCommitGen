package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diff.impl.patch.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.history.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler;
import com.intellij.vcsUtil.VcsUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AutoMessageGenAction extends AnAction {

    String urlPerfix = "http://47.119.169.38:8000/";

    //用户配置变量
    private boolean useHistoryMsg = false;
    private boolean needRec = false;
    private String temperature  = "0.9";
    private String maxToken = "100";

    public Map<String, List<String>> getPatchStringPerFile(Project project, Collection<Change> selectedChanges) throws VcsException {
        String basePath = project.getBasePath();
        List<TextFilePatch> patches = IdeaTextPatchBuilder.buildPatch(project, selectedChanges, Path.of(basePath), false, true).stream()
                .filter(TextFilePatch.class::isInstance)
                .map(TextFilePatch.class::cast)
                .collect(Collectors.toList());

        Map<String, List<String>> patchStringsPerFile = new HashMap<>();
        for (TextFilePatch patch : patches) {
            String fileName = patch.getBeforeName(); // 获取变更代码所在的文件名
            if (!patchStringsPerFile.containsKey(fileName)) {
                patchStringsPerFile.put(fileName, new ArrayList<>());
            }
            List<String> patchStrings = patchStringsPerFile.get(fileName);

            StringBuilder patchString = new StringBuilder();
            for (PatchHunk hunk : patch.getHunks()) {
                for (PatchLine line : hunk.getLines()) {
                    switch (line.getType()) {
                        case CONTEXT:
                            patchString.append("<ide> ").append(line.getText()).append("\n");
                            break;
                        case ADD:
                            patchString.append("<add> ").append(line.getText()).append("\n");
                            break;
                        case REMOVE:
                            patchString.append("<del> ").append(line.getText()).append("\n");
                            break;
                    }
                }
            }
            patchStrings.add(patchString.toString());
        }

        return patchStringsPerFile;
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取当前项目
        Project project = e.getProject();
        //获取editor
        Editor[] allEditors = EditorFactory.getInstance().getAllEditors();
        Editor thisEditor = null;
        for (Editor editor : allEditors) {
            String placeholderText = (String) ((EditorImpl) editor).getPlaceholder();
            if ("Commit Message".equals(placeholderText)) {
                thisEditor = editor;
            }
            else if(("提交消息").equals(placeholderText)){
                thisEditor = editor;
            }
        }


        getUserConfiguration();
        //获取改变
        AbstractCommitWorkflowHandler<?, ?> commitWorkflowHandler = (AbstractCommitWorkflowHandler<?, ?>) e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER);
        Change[] changes = commitWorkflowHandler.getUi().getIncludedChanges().toArray(new Change[0]);

        List<String> history_commits = new ArrayList<>();
        //获取历史消息
        if(useHistoryMsg){
            ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance(project);
            AbstractVcs[] vcss = vcsManager.getAllActiveVcss();
            for (AbstractVcs vcs : vcss) {
                if ("Git".equals(vcs.getName())) {
                    VcsHistoryProvider historyProvider = vcs.getVcsHistoryProvider();
                    String basePath = project.getBasePath();
                    VirtualFile baseDir = LocalFileSystem.getInstance().findFileByPath(basePath);
                    if (baseDir == null) {
                        throw new IllegalStateException("Project base directory not found");
                    }
                    FilePath filePath = VcsUtil.getFilePath(baseDir);

                    VcsHistorySession session = null;
                    try {
                        session = historyProvider.createSessionFor(filePath);
                    } catch (VcsException ex) {
                        throw new RuntimeException(ex);
                    }

                    List<VcsFileRevision> revisions = session.getRevisionList();

                    StringBuilder log = new StringBuilder();
                    for (VcsFileRevision revision : revisions) {
                        log.append(revision.getRevisionNumber()).append(": ").append(revision.getCommitMessage()).append("\n");
                        history_commits.add(revision.getCommitMessage());
                    }
                }
            }
        }

        //获取用户配置信息

        //post消息
        // 这里假设你有一个方法来修改文本框的内容，你需要根据你的实际需求替换这个方法
        //获取修改,并发送post请求
        Document document = thisEditor.getDocument();
        Map<String, List<String>> patchStringsPerFile = null;
//        Map<String, List<String>> patchStringsPerFileWithHistory = null;
        try {
            //发送请求
            patchStringsPerFile = getPatchStringPerFile(project, List.of(changes));
            postWithDiff(patchStringsPerFile,history_commits);
//            getWithDiff(patchStringsPerFile,history_commits);
        } catch (VcsException ex) {
            throw new RuntimeException(ex);
        }


        int offset = thisEditor.getCaretModel().getOffset();

        moveCursor(thisEditor);

        try {
            Robot robot = new Robot();
            ApplicationManager.getApplication().runWriteAction(() -> {
                document.setText("");
            });

            // 模拟用户在编辑器中键入代码
//            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_1);
            robot.keyRelease(KeyEvent.VK_1);
//            robot.keyRelease(KeyEvent.VK_SHIFT);

            // 等待一段时间以确保代码补全已经被调用
            Thread.sleep(500);

        } catch (AWTException | InterruptedException ex) {
            ex.printStackTrace();
        }



    }

    private void postWithDiff(Map<String,List<String>> payload,List<String> historyCommits){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String to;
            // 将请求数据转换为 JSON 字符串
            String jsonRequestData = mapToJson(payload);
            Map<String, Object> diff = new HashMap<>();
            diff.put("diff",jsonRequestData);
            to = urlPerfix+"cmg";
            if (useHistoryMsg){
//                to = urlPerfix+"cmg";
                //放入历史记录
                diff.put("historyMessage", historyCommits);
            }
//                to = urlPerfix+"cmg";
            diff.put("needRec", needRec?"True":"False");

            if (temperature != null && !maxToken.isEmpty())
                diff.put("temperature",Float.parseFloat(temperature));
            if (maxToken != null && !maxToken.isEmpty())
                diff.put("max_tokens",Integer.parseInt(maxToken));

            HttpPost httpPost = new HttpPost(to);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(diff);
            // 设置请求体
            StringEntity entity = new StringEntity(jsonString);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            // 处理响应
            handleResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void getWithDiff(Map<String,List<String>> payload,List<String> historyCommits){
        String to = urlPerfix+"cmg";
        String jsonRequestData = mapToJson(payload);
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            URI uri = new URIBuilder(to)
                    .setParameter("diff",jsonRequestData)
                    .setParameter("needRec","1")
                    .setParameter("max_token","100")
                    .setParameter("temperature","0.9")
                    .build();
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 处理响应
            handleResponse(response);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }



    private void handleResponse(HttpResponse response) throws IOException, JSONException {
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String responseBody = entity != null ? EntityUtils. toString(entity) : "";
        List<String> commitMessages = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(responseBody);
        if (jsonObject.get("commit_message") instanceof JSONArray) {
            JSONArray jsonArray = jsonObject.getJSONArray("commit_message");
            for (int i = 0; i < jsonArray.length(); i++) {
                commitMessages.add(jsonArray.getString(i));
            }
        } else if (jsonObject.get("commit_message") instanceof String) {
            commitMessages.add(jsonObject.getString("commit_message"));
        }

        MyService myService = MyService.getInstance();
        myService.setGlobalVariable(commitMessages);
//        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
//        String commit_message = jsonObject.get("commit_message").getAsString();
    }

    private String mapToJson(Map<String,List<String>> payload) {
        Gson gson = new Gson();
        return gson.toJson(payload);
    }

    private String mapToJson2(Map<String,String> payload) {
        Gson gson = new Gson();
        return gson.toJson(payload);
    }
    public void moveCursor(Editor editor){
        // 将目标编辑器的光标位置设置为与源编辑器相同的偏移量
        JComponent contentComponent = editor.getContentComponent();
        contentComponent.requestFocusInWindow();
    }

    public void getUserConfiguration(){
        // 获取用户设置
        MyPluginSettings myPluginSettings = MyPluginSettings.getInstance();
        MyPluginSettings.State state = myPluginSettings.getState();

        if (state != null) {
            useHistoryMsg = state.useHistoryOrNot;
            needRec = state.needRec;
            temperature = state.temperature;
            maxToken = state.maxToken;
        }
    }

}
