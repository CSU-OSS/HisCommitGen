from llm.api.api import BlueUtils, OpenAIUtils
from typing import List
import logging # 导入模块
import json

class CmgModel():

    def __init__(self, temperature: float = 0.9, max_new_tokens: int = 15) -> None:
        if temperature <= 0 or temperature >= 2.0:
            temperature = 0.9
        if max_new_tokens <= 0 or max_new_tokens >= 512:
            max_new_tokens = 15
        self._llm = OpenAIUtils({'temperature': temperature, 'max_tokens': max_new_tokens})

    def get_commit_message(self, diff: str) -> str:
        prompt = Cmg_prompt.zero_shot(diff)
        result = self._llm.get_chat_completion(prompt)
        return result
    
    def get_commit_message_with_history_message(self, diff: str, similar_messages: List[str]) -> str:
        prompt = Cmg_prompt.zero_shot_with_history_message(diff, similar_messages)
        result = self._llm.get_chat_completion(prompt)
        return result
    
    def get_commit_messages(self, diff: str, similar_messages: List[str], needRec: bool) -> str:
        if needRec == False:
            prompt = Cmg_prompt.zero_shot_with_history_message(diff, similar_messages)
        else:
            prompt = Cmg_prompt.zero_shot_recommend(diff, similar_messages, 5)
        result = self._llm.get_chat_completion(prompt)
        if needRec:
            result = result.replace('\"', '"')
            try:
                result = json.loads(result)
            except json.JSONDecodeError as e:
                print(result)
        return result
    
class Cmg_prompt():

    # Generation

    @staticmethod
    def zero_shot(diff: str, **kwargs) -> str:
        return f"Write a commit message for a given diff. Only output commit message.\nDiff:\n{diff}\nCommit message:\n"
    
    @staticmethod
    def zero_shot_with_history_message(diff: str, similar_messages: List[str], **kwargs) -> str:
        if len(similar_messages) > 0:
            similar_messages_str = "\n".join(similar_messages)
            return f"Write a commit message for a given code change. Pay attention to the sequential pattern of history message of similar code change. Only output commit message.\nCode Change:\n{diff}\nPossible similar history messages:{similar_messages_str}\nCommit message:\n"
        else:
            return Cmg_prompt.zero_shot(diff)
    
    @staticmethod
    def zero_shot_recommend(diff: str, similar_messages: List[str], count: int, **kwargs) -> str:
        if len(similar_messages) > 0:
            similar_messages_str = "\n".join(similar_messages)
            return f"Please Write {count} different commit messages for a given code change. Pay attention to the sequential pattern of history message of similar code change. \nEXAMPLE JSON OUTPUT:[\"Refactor: Optimize code structure for better readability and maintainability\"].\nCode Change:\n{diff}\nPossible similar history messages:{similar_messages_str}\nCommit message:\n"
        return f"Please Write {count} different commit messages for a given code change. You should only output the messages with json array.\nEXAMPLE JSON OUTPUT:[\"Refactor: Optimize code structure for better readability and maintainability\"]\nCode Change:\n{diff}\nCommit message:\n"