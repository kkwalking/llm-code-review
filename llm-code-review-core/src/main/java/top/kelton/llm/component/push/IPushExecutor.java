package top.kelton.llm.component.push;

public interface IPushExecutor {

    boolean send(String message) throws Exception;

    boolean send(String message, PushTemplate template) throws Exception;


    enum PushTemplate {

        HTML("html"),
        JSON("json"),
        TXT("txt"),
        MARKDOWN("markdown"),
        ;

        private String code;

        PushTemplate(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

    }
}
