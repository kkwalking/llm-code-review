package top.kelton.llm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kelton.llm.component.git.GitCommand;
import top.kelton.llm.component.llmqa.IAISession;
import top.kelton.llm.component.push.IPushExecutor;

import java.io.IOException;

public abstract class AbstractCodeReviewService implements ICodeReviewService{
    private final Logger logger = LoggerFactory.getLogger(AbstractCodeReviewService.class);

    protected GitCommand gitCommand;

    protected IAISession aiSession;

    protected IPushExecutor pushExecutor;

    public AbstractCodeReviewService(GitCommand gitCommand, IAISession aiSession, IPushExecutor pushExecutor) {
        this.gitCommand = gitCommand;
        this.aiSession = aiSession;
        this.pushExecutor = pushExecutor;
    }

    @Override
    public void exec() throws Exception {
        // 1. 获取代码变更
        String diff = getDiff();
        logger.info("diff: {}", diff);


        // 2. 发送大模型问答
        String reviewResult = getReviewResult(diff);
        logger.info("reviewResult: {}", reviewResult);

        // 3. 写入log的 git仓库
        String url = logReviewResult(reviewResult);
        logger.info("review url: {}", url);

        // 4. 通过pushplus推送
        pushResult(url);
        logger.info("push url success!");

    }

    protected abstract String getDiff() throws IOException, InterruptedException;
    protected abstract String getReviewResult(String diff) throws Exception;
    protected abstract String logReviewResult(String reviewResult) throws Exception;
    protected abstract void pushResult(String reviewRepoUrl) throws Exception;
}
