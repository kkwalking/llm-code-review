package top.kelton.llm.component.push.dto;

public class PushPlusRequestDTO {
    private String template;
    private String content;
    private String title;
    private String token;


    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
