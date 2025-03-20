# HisCommitGen

HisCommitGen is an automatic commit message generation tool that helps programmers write high-quality commit messages quickly.

## 🚀 Features

- **AI-powered commit message generation** based on code changes.
- **High-quality and concise commit messages**, reducing the burden of writing.
- **Customization options**, allowing users to adjust the output style.

---

## 🛠 Installation

### **VSCode Extension Installation**
#### **Method 1: Install from VSCode Marketplace**
1. Open VSCode and go to `Extensions` (`Ctrl + Shift + X`).
2. Search for **"HisCommitGen"**.
3. Click **Install** and wait for the installation to complete.

#### **Method 2: Manual Installation**
1. Download the `.vsix` file from [VSCode Marketplace](https://marketplace.visualstudio.com/items?itemName=csuoss.HisCommitGen).
2. Open VSCode, press `Ctrl + Shift + P`, and search for `Extensions: Install from VSIX`.
3. Select the downloaded file to install.

---

## Usage (How to Use)

HisCommitGen provides multiple ways to generate commit messages:

### **🔹 Automatic Completion**
- Simply start typing in the commit message input box, and the plugin will suggest completions automatically.

### **🔹 Keyboard Shortcuts**
- **Mac:** Press `Option + Esc` to trigger commit message generation.
- **Windows:** Use `Ctrl + Space` or `Ctrl + Shift + Space` to generate commit messages.

These shortcuts allow you to quickly generate high-quality commit messages while staying focused on your workflow.

---

## 🔧 Extension Settings

This extension contributes the following settings:

| Setting                     | Description |
|-----------------------------|-------------|
| `cmg.apiEndpoint`           | The API endpoint for generating commit messages. |
| `cmg.AccordingHistoryOrNot` | Whether to generate messages based on commit history. |
| `cmg.temperature`           | Adjusts the creativity of generated messages. |
| `cmg.max_tokens`            | Maximum length of generated messages. |

---

