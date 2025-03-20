import * as vscode from "vscode";
import { GitExtension, Repository,Commit } from "./api/git";
import fetch from 'node-fetch';

export const getMainRepostiory = () => {
  const gitExtensions = vscode.extensions.getExtension<GitExtension>("vscode.git");
  if (!gitExtensions || !gitExtensions.exports) {
    throw new Error("Git extension not found! Git extension must be enabled for this plugin to work");
  }

  const gitApi = gitExtensions.exports.getAPI(1);
  return gitApi.repositories[0];
};

export const getCommitHistory = async (maxCount: number): Promise<Commit[]> => {
  const repository = getMainRepostiory();
  const commits = await repository.log({ maxEntries: maxCount });
  return commits;
};

/**
 * 获取当前 Git 仓库的代码变更（diff）
 * @returns {Promise<string>} 返回代码变更的字符串
 */
export const getCodeChanges = async (): Promise<string> => {
  try {
    // 获取当前 Git 仓库
    const repository = getMainRepostiory();

    // 获取当前仓库的代码变更（diff）
    const changes = await repository.diff(true); // true 表示暂存区的变更
   
    // 如果没有变更，抛出错误
    if (changes === '') {
      throw new Error("No changes found, please make some changes before generating the commit message.");
    }

    // 返回代码变更
    return changes;
  } catch (error) {
    if (error instanceof Error) {
      vscode.window.showErrorMessage(error.message);
    } else {
      vscode.window.showErrorMessage("Failed to get code changes.");
    }
    throw error; // 抛出错误以便上层函数处理
  }
};

export const showCommitMessages = async () => {
  try {
    const commits = await getCommitHistory(10); // 获取最近 10 条提交记录
    commits.forEach(commit => {
      console.log(`Message: ${commit.message}`);
      console.log(`Author: ${commit.authorName}`);
      console.log(`Date: ${commit.commitDate}`);
    });
  } catch (error) {
    console.error(error);
  }
};

export const generateCommitMessageWithDeepSeek = async (predictionCount: number = 5): Promise<string[]> => {
  try {
    // 获取代码变更
    const changes = await getCodeChanges();
    if (!changes.trim()) {
      vscode.window.showErrorMessage("No changes found, please make some changes before generating the commit message");
      return ["无变更，无法生成提交信息"];
    }

    // 读取插件配置中的 API 地址
    let serverEndpoint: string = vscode.workspace.getConfiguration('cmg').get('serverEndpoint') || "http://47.119.169.38:8000/cmg";
    console.log("API 请求地址:", serverEndpoint);

    // 构造请求体
    const payload = {
      diff: changes,
      needRec: predictionCount > 1,  // 确保后端支持返回多条
      historyMessage: vscode.workspace.getConfiguration('cmg').get('AccordingHistoryOrNot') ? await getCommitHistory(10) : [],
      temperature: vscode.workspace.getConfiguration('cmg').get('temperature') ?? 0.9,
      max_tokens: vscode.workspace.getConfiguration('cmg').get('max_tokens') ?? 400
    };

    console.log("请求体:", JSON.stringify(payload, null, 2));

    const response = await fetch(serverEndpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      vscode.window.showErrorMessage(`提交信息生成失败1: ${response.statusText}`);
      console.log("API 响应状态:", response.status, response.statusText);
      return ["提交信息生成失败"]; // 避免抛出异常导致程序崩溃
    }

    const result = await response.json();
    console.log("API 响应:", result);

    let suggestions: string[] = [];

    // **解析 commit_message**
    if (Array.isArray(result.commit_message)) {
      suggestions = result.commit_message;
    } else if (typeof result.commit_message === "string") {
      // **如果返回的是换行分隔的字符串，拆分成数组**
      suggestions = (result.commit_message as string).split("\n").map((msg: string) => msg.trim()).filter((msg: string) => msg.length > 0);
    } else {
      vscode.window.showErrorMessage("提交信息生成失败3: API 返回格式错误");
      return ["提交信息生成失败"];
    }

    console.log("生成的 Commit Messages:", suggestions);
    return suggestions;

  } catch (error) {
    vscode.window.showErrorMessage(`提交信息生成失败2: ${error instanceof Error ? error.message : String(error)}`);
    return ["提交信息生成失败"];
  }
};

export const fetchCommitMessage = async (predictionCount: number): Promise<any> => {
  try {
    const changes = await getCodeChanges();
    const commitMessage = await generateCommitMessageWithDeepSeek(predictionCount);
    return commitMessage;
  } catch (error) {
    return Promise.reject(error);
  }
};

export const removeSurroundingQuotes = (input: string): string => {
  if (input.startsWith('"') && input.endsWith('"')) {
    return input.slice(1, -1);
  }
  return input;
};

export const capitalize = (input: string): string => {
  input = removeSurroundingQuotes(input);
  return input.length > 0 ? input.charAt(0).toUpperCase() + input.slice(1) : "";
};

export const documentFilters: Array<vscode.DocumentFilter | string> = [
  { language: 'php' },
  { language: 'powershell' },
  { language: 'jade' },
  { language: 'python'},
  { language: 'r' },
  { language: 'razor' },
  { language: 'ruby' },
  { language: 'rust' },
  { language: 'scss' },
  { language: 'search-result' },
  { language: 'shaderlab' },
  { language: 'shellscript' },
  { language: 'sql' },
  { language: 'swift' },
  { language: 'typescript' },
  { language: 'vb' },
  { language: 'xml' },
  { language: 'yaml' },
  { language: 'markdown' },
  { language: 'bat' },
  { language: 'clojure' },
  { language: 'coffeescript' },
  { language: 'jsonc' },
  { language: 'c' },
  { language: 'cpp' },
  { language: 'csharp' },
  { language: 'css' },
  { language: 'dockerfile' },
  { language: 'fsharp' },
  { language: 'git-commit' },
  { language: 'go' },
  { language: 'groovy' },
  { language: 'handlebars' },
  { language: 'hlsl' },
  { language: 'html' },
  { language: 'ini' },
  { language: 'java' },
  { language: 'javascriptreact' },
  { language: 'javascript' },
  { language: 'json' },
  { language: 'less' },
  { language: 'log' },
  { language: 'lua' },
  { language: 'makefile' },
  { language: 'ignore' },
  { language: 'properties' },
  { language: 'objective-c' },
  { language: 'perl' },
  { language: 'perl6' },
  { language: 'typescriptreact' },
  { language: 'yml' },
  '*',
];