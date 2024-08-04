package top.kelton.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kelton.llm.component.git.GitCommand;
import top.kelton.llm.component.llmqa.ChatGLM;
import top.kelton.llm.component.llmqa.IAISession;
import top.kelton.llm.component.push.IPushExecutor;
import top.kelton.llm.component.push.PushPlus;
import top.kelton.llm.service.DefaultCodeReviewService;

public class CodeReviewApplication {
    private static final Logger logger = LoggerFactory.getLogger(CodeReviewApplication.class);

    // ChatGLM 配置
    private String chatglm_apiHost = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private String chatglm_apiKeySecret = "****.***";

    // pushplus 配置
    private String pushplus_token = "*****";

    // Github 配置
    private String github_review_log_uri = "https://github.com/kkwalking/llm-code-review-log";
    private String github_token = "****";

    // 工程配置 - 自动获取
    private String github_project = "llm-code-review";
    private String github_branch = "master";
    private String github_author = "kkwalking";

    public static void main(String[] args) throws Exception {
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        IAISession aiSession = new ChatGLM(getEnv("CHATGLM_APIHOST"), getEnv("CHATGLM_APIKEYSECRET"));

        String push_host = "http://www.pushplus.plus/send";
        IPushExecutor pushExecutor = new PushPlus(push_host, getEnv("PUSHPLUS_TOKEN"));

        DefaultCodeReviewService defaultCodeReviewService = new DefaultCodeReviewService(gitCommand, aiSession, pushExecutor);
        defaultCodeReviewService.exec();

        logger.info("execute code review success.");

    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }
}
