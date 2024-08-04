package top.kelton.llm.component.push;

public enum TemplateKey {
    REPO_NAME("repo_name", "项目名称"),
    BRANCH_NAME("branch_name", "分支名称"),
    COMMIT_AUTHOR("commit_author", "提交者"),
    COMMIT_MESSAGE("commit_message", "提交信息"),
    REVIEW_CONTENT("review_log_url", "评审日志地址"),
    ;

    private String code;
    private String desc;

    TemplateKey(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}