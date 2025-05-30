package demo;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

public class AutoPopupHandler extends TypedHandlerDelegate {

    public void showCustomPopup(Project project, Editor editor) {
        // 指定弹出窗口的类型为 BASIC
        CompletionType completionType = CompletionType.BASIC;
//        Condition<? super PsiFile> condition = psiFile -> {
//            // 在这里根据需要设置条件，比如文件类型、位置等
//            // 这里只是一个示例，具体根据你的需求来设置条件
//            return true; // 这里返回 true 表示符合条件，触发代码自动提示
//        };

        // 触发代码自动提示弹出窗口，并指定弹出窗口的类型
        AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
    }
}
