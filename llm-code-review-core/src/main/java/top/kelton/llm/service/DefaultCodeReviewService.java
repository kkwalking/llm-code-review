package top.kelton.llm.service;

import com.alibaba.fastjson2.JSON;
import top.kelton.llm.component.git.GitCommand;
import top.kelton.llm.component.llmqa.IAISession;
import top.kelton.llm.component.llmqa.dto.ChatCompletionRequestDTO;
import top.kelton.llm.component.llmqa.dto.ChatCompletionSyncResponseDTO;
import top.kelton.llm.component.llmqa.model.Model;
import top.kelton.llm.component.push.IPushExecutor;
import top.kelton.llm.component.push.TemplateKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DefaultCodeReviewService extends AbstractCodeReviewService{

    public DefaultCodeReviewService(GitCommand gitCommand, IAISession aiSession, IPushExecutor pushExecutor) {
        super(gitCommand, aiSession, pushExecutor);
    }

    @Override
    protected String getDiff() throws IOException, InterruptedException {
        return gitCommand.diff();
    }

    @Override
    protected String getReviewResult(String diff) throws Exception {
        ChatCompletionRequestDTO requestDTO = new ChatCompletionRequestDTO();
        requestDTO.setModel(Model.GLM_4.getCode());
        ArrayList<ChatCompletionRequestDTO.Prompt> messages = new ArrayList<>();
        ChatCompletionRequestDTO.Prompt metaPrompt = new ChatCompletionRequestDTO.Prompt();
        metaPrompt.setRole("user");
        metaPrompt.setContent("你是一位资深编程专家，拥有深厚的编程基础和广泛的技术栈知识。你的专长在于识别代码中的低效模式、安全隐患、以及可维护性问题，并能提出针对性的优化策略。你擅长以易于理解的方式解释复杂的概念，确保即使是初学者也能跟随你的指导进行有效改进。在提供优化建议时，你注重平衡性能、可读性、安全性、逻辑错误、异常处理、边界条件，以及可维护性方面的考量，同时尊重原始代码的设计意图。\n" +
                "你以鼓励和建设性的方式提出反馈，致力于提升团队的整体编程水平，详尽指导编程实践。用户会将版本修改代码提交给你，以git diff 字符串的形式提供，你需要根据变化的代码来评审代码。你的返回内容必须严格遵守如下格式:\n" +
                "# 评审项目： OpenAi 代码评审.\n" +
                "### ✅代码评分：{变量1}\n" +
                "#### ✅代码逻辑与目的：\n" +
                "{变量2}\n" +
                "#### ✅代码优点：\n" +
                "{变量3}\n" +
                "#### ✅问题点：\n" +
                "{变量4}\n" +
                "#### ✅修改建议：\n" +
                "{变量5}\n" +
                "#### ✅修改后的代码：\n" +
                "{变量6}\n" +
                "评审结束。\n" +
                "下面是模板中的变量内容解释：\n" +
                "变量1是给评审代码的质量进行打分，分数区间为0~100分。\n" +
                "变量2是代码的逻辑和目的，识别其在特定上下文中的作用和限制。\n" +
                "变量3是代码中的优点。\n" +
                "变量4是评审发现的问题点，包括：可能的性能瓶颈、逻辑缺陷、潜在问题、安全风险、命名规范、注释、以及代码结构、异常情况、边界条件、资源的分配与释放等等\n" +
                "变量5是具体的优化修改建议。\n" +
                "变量6是你给出的修改后的代码。 \n" +
                "必须要求：\n" +
                "1. 以精炼的语言、严厉的语气指出存在的问题。\n" +
                "2. 你的反馈内容必须使用严谨的markdown格式\n" +
                "3. 不要携带变量内容解释信息。\n" +
                "4. 有清晰的标题结构\n" +
                "代码如下:");

        messages.add(metaPrompt);
        messages.add(new ChatCompletionRequestDTO.Prompt("user", diff));
        requestDTO.setMessages(messages);
        ChatCompletionSyncResponseDTO responseDTO = aiSession.completions(requestDTO);
        return responseDTO.getChoices().get(0).getMessage().getContent();
    }

    @Override
    protected String logReviewResult(String reviewResult) throws Exception {
        return gitCommand.commitAndPush(reviewResult);
    }

    @Override
    protected void pushResult(String reviewRepoUrl) throws Exception {
        HashMap<String, String> pushMap = new HashMap<>();
        pushMap.put(TemplateKey.REPO_NAME.getDesc(), gitCommand.getProject());
        pushMap.put(TemplateKey.BRANCH_NAME.getDesc(), gitCommand.getBranch());
        pushMap.put(TemplateKey.COMMIT_AUTHOR.getDesc(), gitCommand.getAuthor());
        pushMap.put(TemplateKey.COMMIT_MESSAGE.getDesc(), gitCommand.getCommitMessage());
        pushMap.put(TemplateKey.REVIEW_CONTENT.getDesc(), reviewRepoUrl);
        pushExecutor.send(JSON.toJSONString(pushMap), IPushExecutor.PushTemplate.JSON);
    }
}
