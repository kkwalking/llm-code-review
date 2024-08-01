package top.kelton.llm.component.git;

import java.io.*;

public class GitCommand {

    /**
     * 获取最新提交和上一次提交的差异
     * @return
     */
    public String diff() throws IOException, InterruptedException {
        // 1. 获取提交人
        ProcessBuilder logProcessBuilder = new ProcessBuilder();
        logProcessBuilder.command("git", "log","-1", "--pretty=format:%H");
        logProcessBuilder.directory(new File("."));
        Process logProcess = logProcessBuilder.start();

        BufferedReader logBufferedReader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));
        // 最近一次调教的commit hash
        String latestCommitHash = logBufferedReader.readLine();
        logBufferedReader.close();
        logProcess.waitFor();

        ProcessBuilder diffProcessBuilder = new ProcessBuilder();
        diffProcessBuilder.command("git", "diff",latestCommitHash+"^", "--pretty=format:%H");
        diffProcessBuilder.directory(new File("."));
        Process diffProcess = diffProcessBuilder.start();

        // 获取差异内容
        BufferedReader diffBufferedReader = new BufferedReader(new InputStreamReader(diffProcess.getInputStream()));
        StringBuilder diffContent = new StringBuilder();
        String line;
        while ((line = diffBufferedReader.readLine()) != null) {
            diffContent.append(line).append("\n");
        }
        diffBufferedReader.close();

        int exitCode = diffProcess.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to get diff, exit code:" + exitCode);
        }

        return diffContent.toString();

    }
}
