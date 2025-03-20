# HisCommitGen

Directory structure
- /HisCommitGen-VSCode : Plugin for VSCode
- /HisCommitGen-JetBrain : Plugin for Jetbrain
- /HisCommitGen-Server : Server

Demo video address:https://youtu.be/eB5OVh66OOQ

## HisCommitGen-JetBrain : Plugin for Jetbrain  
### installation:  
1. Open your JetBrains IDE  
2. Navigate to Settings/Preferences → Plugins → Marketplace.  
3. Search for "HisCommitGen" and click Install.

### Configure Settings:  
Go to Settings→ Tools → CMG plunging settings.  
Adjust the following parameters:  
+ Use History Commit Messages: Toggle to enable/disable historical commit context.  
+ Generate more recommendation messages：5 by default, you can increase the number after this function is enabled.  
+ temperature: Controls randomness (0.0–1.0; lower values = more deterministic).  
+ maxTokens: Maximum length of generated messages.
### Usage  
HisCommitGen supports two workflows: Recommendation (full message generation) and Completion (prefix-based suggestions).
1. Commit Message Recommendation  
  Scenario: Generate commit messages from scratch after code changes.  
  A list of 5 recommended messages will appear. Use arrow keys to navigate and press Enter to apply.  
2. Commit Message Completion  
  Scenario: Auto-complete a message based on a partial prefix.  
