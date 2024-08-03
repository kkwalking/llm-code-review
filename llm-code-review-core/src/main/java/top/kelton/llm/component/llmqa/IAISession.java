package top.kelton.llm.component.llmqa;

import top.kelton.llm.component.llmqa.dto.ChatCompletionRequestDTO;
import top.kelton.llm.component.llmqa.dto.ChatCompletionSyncResponseDTO;

public interface IAISession {



    ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO requestDTO) throws Exception;
}
