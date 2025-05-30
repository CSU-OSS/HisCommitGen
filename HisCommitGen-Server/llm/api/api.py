import requests
import json
import os
from .Blue import sync_vivogpt
# Please install OpenAI SDK first: `pip3 install openai`

from openai import OpenAI

class BlueUtils:

    def __init__(self, generation_kwargs):
        self._generation_kwargs = generation_kwargs

    def get_chat_completion(self, prompt: str) -> str:
        temperature = self._generation_kwargs['temperature']
        max_new_tokens = self._generation_kwargs['max_tokens']
        result = sync_vivogpt(prompt=prompt, temperature=temperature, max_new_tokens=max_new_tokens)
        return result
    
class OpenAIUtils:
    def __init__(self, generation_kwargs):
        self._generation_kwargs = generation_kwargs

    def get_chat_completion(self, prompt: str) -> str:
        api_key = "sk-91575fda5fbe47a8b2cb03940f728ed6"
        base_url = "https://api.deepseek.com"
        client = OpenAI(api_key=api_key, base_url=base_url)

        response = client.chat.completions.create(
            model="deepseek-chat",
            messages=[
                {"role": "system", "content": "You are a helpful assistant"},
                {"role": "user", "content": prompt},
            ],
            response_format={'type': 'json_object'
            },
            stream=False,
            **self._generation_kwargs
        )
        return response.choices[0].message.content