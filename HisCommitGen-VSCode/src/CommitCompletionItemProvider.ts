
import * as vscode from 'vscode';
import { capitalize, fetchCommitMessage ,generateCommitMessageWithDeepSeek} from './utils';


export class CommitCompletionItemProvider implements vscode.CompletionItemProvider {
    async provideCompletionItems(
        document: vscode.TextDocument, 
        position: vscode.Position, 
        token: vscode.CancellationToken, 
        context: vscode.CompletionContext
    ): Promise<vscode.CompletionItem[]> {   
        if (document.languageId !== 'scminput' || document.uri.scheme !== 'vscode-scm') { 
            return []; 
        }

        try {
            console.log("CommitCompletionItemProvider：Fetching commit message...");
            const suggestions = await generateCommitMessageWithDeepSeek(5);
            console.log("CommitCompletionItemProvider：Suggestions:", suggestions);

            return suggestions.map(suggestion => new vscode.CompletionItem(suggestion, vscode.CompletionItemKind.Text));
        } catch (exception) {
            const error = exception as Error; 
            vscode.window.showErrorMessage("commit", error.message);
            return [];
        }
    }
}

export class InlineCommitCompletionItemProvider implements vscode.InlineCompletionItemProvider {
    async provideInlineCompletionItems(
        document: vscode.TextDocument,
        position: vscode.Position, 
        context: vscode.InlineCompletionContext, 
        token: vscode.CancellationToken
    ): Promise<vscode.InlineCompletionItem[] | vscode.InlineCompletionList | null | undefined> {
        if (document.uri.scheme !== 'vscode-scm' || document.languageId !== 'scminput') {
            return [];
        }

        try {
            console.log("InlineCommitCompletionItemProvider: Fetching commit message...");
            const suggestions = await generateCommitMessageWithDeepSeek(5);
            console.log("InlineCommitCompletionItemProvider: Suggestions:", suggestions);

            return suggestions.map(suggestion => new vscode.InlineCompletionItem(suggestion));
        } catch (exception) {
            const error = exception as Error; 
            vscode.window.showErrorMessage("inline", error.message);
            return [];
        }
    }
}