package top.kelton.llm;

import com.alibaba.fastjson2.JSON;
import org.junit.Test;
import top.kelton.llm.component.git.GitCommand;
import top.kelton.llm.component.llmqa.ChatGLM;
import top.kelton.llm.component.llmqa.IAISession;
import top.kelton.llm.component.llmqa.dto.ChatCompletionRequestDTO;
import top.kelton.llm.component.llmqa.dto.ChatCompletionSyncResponseDTO;
import top.kelton.llm.component.push.PushPlus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ApiTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        GitCommand gitCommand = new GitCommand();
        String diff = gitCommand.diff();
        System.out.println(diff);
    }

    @Test
    public void test_ChatGLM() throws Exception {
        String host = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
        String apiKeySecret = "cff52829623***.******";

        IAISession chatGLM = new ChatGLM(host, apiKeySecret);

        ArrayList<ChatCompletionRequestDTO.Prompt> prompts = new ArrayList<>();
        prompts.add(new ChatCompletionRequestDTO.Prompt("user", "java冒泡排序"));
        ChatCompletionRequestDTO request = new ChatCompletionRequestDTO();
        request.setMessages(prompts);
        ChatCompletionSyncResponseDTO response = chatGLM.completions(request);
        System.out.println(JSON.toJSONString(response));

    }

    @Test
    public void test_PushPlus() throws Exception {
        PushPlus pushPlus = new PushPlus("http://www.pushplus.plus/send", "*****");
        HashMap<String, String> content = new HashMap<>();
        content.put("username", "bruce");
        content.put("age", "19");
        pushPlus.send(JSON.toJSONString(content));
    }
}
