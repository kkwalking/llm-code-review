package top.kelton.llm;

import com.alibaba.fastjson2.JSON;
import org.junit.Test;
import top.kelton.llm.component.git.GitCommand;
import top.kelton.llm.component.llmqa.ChatGLM;
import top.kelton.llm.component.llmqa.IAISession;
import top.kelton.llm.component.llmqa.dto.ChatCompletionRequestDTO;
import top.kelton.llm.component.llmqa.dto.ChatCompletionSyncResponseDTO;
import top.kelton.llm.component.push.PushPlus;
import top.kelton.llm.component.push.TemplateKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ApiTest {


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
        PushPlus pushPlus = new PushPlus("http://www.pushplus.plus/send", "*******");
        HashMap<String, String> content = new HashMap<>();
        content.put(TemplateKey.REPO_NAME.getDesc(), "AI代码自动评审");
        content.put(TemplateKey.COMMIT_AUTHOR.getDesc(), "zhouzekun");
        content.put(TemplateKey.BRANCH_NAME.getDesc(), "master");
        content.put(TemplateKey.COMMIT_MESSAGE.getDesc(), "新增用户查询接口");
        content.put(TemplateKey.REVIEW_CONTENT.getDesc(), "# 1.代码质量\n\t代码较为健壮\n# 2.安全性\n\t## 较为安全");
        boolean send = pushPlus.send(JSON.toJSONString(content));
        System.out.println("推送结果:" + send);
    }

    @Test
    public void test_createAndPush() throws Exception {
        String repoUrl = "https://github.com/kkwalking/llm-code-review-log";
        String token = "ghp_WKqALrHVUtkZeUR******p";
        String project = "llm-code-review";
        String branch = "master";
        String author = "zzk";
        String message = "# 项目： LLM自动代码评审.\n" +
                "### \uD83D\uDE03代码评分：85\n" +
                "#### \uD83D\uDE03代码逻辑与目的：\n" +
                "该代码是一个Java类，名为OpenAiCodeReview。这个类的主要功能是通过HTTP请求与OpenAI API进行交互，发送一个包含git diff记录的请求，并接收OpenAI的代码审查结果。修改后的代码使用了更加面向对象的方式构建了请求的JSON字符串，而不是直接拼接字符串。\n" +
                "\n" +
                "#### ✅代码优点：\n" +
                "1. 代码结构清晰，逻辑明确。\n" +
                "2. 优化了JSON字符串的构造过程，使用了对象的方式，提高了代码的可读性和可维护性。\n" +
                "3. 使用了try-with-resources来自动管理资源，避免了可能的资源泄露。\n" +
                "\n" +
                "#### \uD83E\uDD14问题点：\n" +
                "1. 在`OpenAiCodeReview`类中，`review`方法中直接使用了`System.out.println`打印日志，这不是一个好的日志记录方式。在生产环境中，我们应该使用专门的日志框架来记录日志。\n" +
                "2. `ChatCompletionRequest.Prompt`的构造过程中，`diffCode`没有被添加进去，这可能是一个逻辑错误。\n" +
                "\n" +
                "#### \uD83C\uDFAF修改建议：\n" +
                "1. 使用专门的日志框架，如SLF4J或Log4j，来替代`System.out.println`。\n" +
                "2. 在构造`ChatCompletionRequest.Prompt`时，应将`diffCode`添加到内容中。\n" +
                "\n" +
                "#### \uD83D\uDCBB修改后的代码：\n" +
                "```java\n" +
                "import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;\n" +
                "\n" +
                "public class OpenAiCodeReview {\n" +
                "\n" +
                "    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAiCodeReview.class);\n" +
                "\n" +
                "    public String review(String diffCode) {\n" +
                "        //...\n" +
                "        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>() {\n" +
                "            private static final long serialVersionUID = -7988151926241837899L;\n" +
                "\n" +
                "            {\n" +
                "                add(new ChatCompletionRequest.Prompt(\"user\", \"你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:\"));\n" +
                "                add(new ChatCompletionRequest.Prompt(\"user\", diffCode));\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        //...\n" +
                "        LOGGER.info(\"评审结果：\" + content.toString());\n" +
                "        //...\n" +
                "    }\n" +
                "}\n" +
                "```\n";
        GitCommand gitCommand = new GitCommand(repoUrl, token, project, branch, author, "新增用户接口");
        String distUrl = gitCommand.commitAndPush(message);
        System.out.println("评审地址: "+distUrl);

    }
}
