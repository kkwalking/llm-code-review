package top.kelton.llm.component.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kelton.llm.utils.RandomStringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GitCommand {
    private final Logger logger = LoggerFactory.getLogger(GitCommand.class);

    private final String githubReviewLogUri;

    private final String githubToken;

    private final String project;

    private final String branch;

    private final String author;


    public GitCommand(String githubReviewLogUri, String githubToken, String project, String branch, String author) {
        this.githubReviewLogUri = githubReviewLogUri;
        this.githubToken = githubToken;
        this.project = project;
        this.branch = branch;
        this.author = author;
    }

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

    public String commitAndPush(String recommend) throws Exception {
        Git git = Git.cloneRepository()
                .setURI(githubReviewLogUri + ".git")
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""))
                .call();

        // 创建分支
        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File dateFolder = new File("repo/" + dateFolderName);
        if (!dateFolder.exists()) {
            dateFolder.mkdirs();
        }

        String fileName = project + "-" + branch + "-" + author + System.currentTimeMillis() + "-" + RandomStringUtils.randomNumeric(4) + ".md";
        File newFile = new File(dateFolder, fileName);
        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write(recommend);
        }

        // 提交内容
        git.add().addFilepattern(dateFolderName + "/" + fileName).call();
        git.commit().setMessage("add code review new file" + fileName).call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, "")).call();

        logger.info("llm-code-review git commit and push done! {}", fileName);

        return githubReviewLogUri + "/blob/master/" + dateFolderName + "/" + fileName;
    }
}
