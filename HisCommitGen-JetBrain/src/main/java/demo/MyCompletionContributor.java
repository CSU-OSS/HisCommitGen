package demo;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class MyCompletionContributor extends CompletionContributor {


    @Override
    public void fillCompletionVariants(CompletionParameters parameters, @NotNull CompletionResultSet result) {
        //获取project
        PsiElement position = parameters.getPosition();
        Project project = position.getProject();

        MyService myService = MyService.getInstance();
        List<String> commit_messages = myService.getGlobalVariable();

        // 在用户输入时提供代码补全1
        int offset = parameters.getOffset();
        String text = parameters.getEditor().getDocument().getText(TextRange.create(parameters.getEditor().getDocument().getLineStartOffset(parameters.getEditor().getDocument().getLineNumber(offset)), offset));
//        switch (text) {
//            case "f":
        for (String commitMessage : commit_messages) {
            LookupElement lookupElement = LookupElementBuilder.create(commitMessage)
                    .withPresentableText(commitMessage)
                    .withCaseSensitivity(true)
                    .bold();
            result.addElement(lookupElement);
        }
//
//
//                lookupElement = LookupElementBuilder.create(commit_messages.get(1))
//                        .withPresentableText(commit_messages.get(1))
//                        .withCaseSensitivity(true)
//                        .bold();
//                result.addElement(lookupElement);
//
//                lookupElement = LookupElementBuilder.create(commit_messages.get(2))
//                        .withPresentableText(commit_messages.get(2))
//                        .withCaseSensitivity(true)
//                        .bold();
//                result.addElement(lookupElement);
//                break;
//            case "2":
//                LookupElement lookupElement2 = LookupElementBuilder.create("第二2个".substring(2))
//                        .withPresentableText("22332222333")
//                        .withCaseSensitivity(true)
//                        .bold();
//                result.addElement(lookupElement2);
//                break;
//            default:
//                if (text.endsWith("!")) {
//                    return;
//                }
//                super.fillCompletionVariants(parameters, result);
//                WordCompletionContributor.addWordCompletionVariants(result, parameters, Collections.emptySet());
//                break;
//        }
        result.stopHere();
    }

}
