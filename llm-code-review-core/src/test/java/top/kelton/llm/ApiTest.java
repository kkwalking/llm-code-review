package top.kelton.llm;

import top.kelton.llm.component.git.GitCommand;

import java.io.IOException;

public class ApiTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        GitCommand gitCommand = new GitCommand();
        String diff = gitCommand.diff();
        System.out.println(diff);
    }
}
