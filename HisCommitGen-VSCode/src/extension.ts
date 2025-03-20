// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';
import fetch from "node-fetch"; // 需要安装 node-fetch

import { documentFilters, getMainRepostiory, fetchCommitMessage, capitalize, getCodeChanges, generateCommitMessageWithDeepSeek } from './utils';
import { InlineCommitCompletionItemProvider, CommitCompletionItemProvider } from './CommitCompletionItemProvider';


const generateCommitMessage = async () => { 
    const repository = getMainRepostiory();
    let commitMessages: string[] = [];
    try {
        const changes = await getCodeChanges(); // 获取代码变更
        commitMessages = await generateCommitMessageWithDeepSeek(5);
    } catch (exception) {
        const error = exception as Error; 
        vscode.window.showErrorMessage(error.message);
        return "Error,Please test again.";
    }
	if (commitMessages.length > 0) {
        console.log(commitMessages);
        repository.inputBox.value = capitalize(commitMessages[0]); // 取第一条作为最终提交信息
    } else {
        vscode.window.showErrorMessage("生成提交信息失败");
    }
}


// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed
export function activate(context: vscode.ExtensionContext) {

	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	console.log('Congratulations, your extension "cmg" is now active!');

	let disposables = [
		
		vscode.commands.registerCommand("cmg-vscode.autofill", generateCommitMessage),
		vscode.languages.registerCompletionItemProvider(documentFilters, new CommitCompletionItemProvider()),
		vscode.languages.registerInlineCompletionItemProvider(documentFilters, new InlineCommitCompletionItemProvider()),
	];
	disposables.forEach(x => {context.subscriptions.push(x);}
	);
}

// This method is called when your extension is deactivated
export function deactivate() {}
