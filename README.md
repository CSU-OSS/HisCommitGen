# HisCommitGen

Directory structure
- /HisCommitGen-VSCode : Plugin for VSCode
- /HisCommitGen-JetBrain : Plugin for Jetbrain
- /HisCommitGen-Server : Server

Demo video address: https://www.youtube.com/watch?v=Lmi5WfYwvNE

## HisCommitGen


### installation:  
1. Open your VSCode IDE  
2. Navigate to Extensions → Marketplace.  
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
  A list of 5 recommended messages will appear. Use the arrow keys to navigate and press Enter to apply.  
2. Commit Message Completion  
  Scenario: Auto-complete a message based on a partial prefix.


### Scoring criterion for hunman evaluation

| Score | Definition                                                              |
|-------|-------------------------------------------------------------------------|
| 0     | Neither relevant in semantic nor having shared tokens.                 |
| 1     | Irrelevant in semantic but with some shared tokens.                    |
| 2     | Partially similar in semantic, but each contains exclusive information.|
| 3     | Relevant in semantic.                                                  |
| 4     | Highly similar but not identical in semantic.                          |
| 5     | Identical in semantic.                                                 |


### Efficacy of different history retrieval methods for *HisCommitGen*

Table 2 below presents the results of using different retrieval methods for *HisCommitGen*.

* **No retrieval** refers to not providing any commit history.
* **Random retrieval** involves randomly selecting a message from the developer's commit history.

The results show:

* Using no retrieval produces the lowest scores.
* Random retrieval slightly improves performance.
* **TF-IDF** and **BM25** are sparse retrieval methods.
* **CodeT5 embedding** is a dense retriever using cosine similarity on embedded vectors of code changes.
* All retrieval methods (TF-IDF, BM25, and CodeT5 embedding) significantly enhance performance, yielding similar scores.
* **BM25** achieves the highest scores across multiple metrics, showing its superiority.

#### Table 2: Efficacy of different history retrieval methods for *HisCommitGen*

| Retrieval Method | BLEU (%) | METEOR (%) | Log-MNEXT (%) |
| ---------------- | -------- | ---------- | ------------- |
| No retrieval     | 1.63     | 13.42      | 12.51         |
| Random           | 2.13     | 15.44      | 12.69         |
| TF-IDF           | 3.36     | 19.24      | 15.58         |
| CodeT5 embedding | 3.40     | 19.40      | 15.65         |
| **BM25**         | **3.42** | **19.60**  | **15.81**     |

