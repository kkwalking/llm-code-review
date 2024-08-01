package top.kelton.llm.service;

public abstract class AbstractCodeReviewService implements ICodeReviewService{
    @Override
    public void exec() {
        // 1. 获取代码变更
        String diff = getDiff();


        // 2. 发送大模型问答
        String reviewResult = getReviewResult(diff);

        // 3. 写入log的 git仓库
        String url = logReviewResult(reviewResult);

        // 4. 通过pushplus推送
        pushResult(reviewResult);

    }

    protected abstract String getDiff();
    protected abstract String getReviewResult(String diff);
    protected abstract String logReviewResult(String reviewResult);
    protected abstract String pushResult(String reviewResult);
}
