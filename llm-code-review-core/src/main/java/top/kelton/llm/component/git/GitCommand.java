package top.kelton.llm.component.git;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kelton.llm.utils.RandomStringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class GitCommand {
    private final Logger logger = LoggerFactory.getLogger(GitCommand.class);

    private final String githubReviewLogUri;

    private final String githubToken;

    private final String project;

    private final String branch;

    private final String author;

    private final String commitMessage;


    public GitCommand(String githubReviewLogUri, String githubToken, String project, String branch, String author, String commitMessage) {
        this.githubReviewLogUri = githubReviewLogUri;
        this.githubToken = githubToken;
        this.project = project;
        this.branch = branch;
        this.author = author;
        this.commitMessage = commitMessage;
    }

    public String getGithubReviewLogUri() {
        return githubReviewLogUri;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getAuthor() {
        return author;
    }

    public String getCommitMessage() {
        return commitMessage;
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
        String rootFolder = "repo";
        Git git = Git.cloneRepository()
                .setURI(githubReviewLogUri + ".git")
                .setDirectory(new File(rootFolder))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""))
                .call();

        ZoneId beijingZoneId = ZoneId.of("Asia/Shanghai");
        ZonedDateTime beijingTime = ZonedDateTime.now(beijingZoneId);
        DateTimeFormatter createTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String fileCreateTime = beijingTime.format(createTimeFormatter);
        String folder = project + "/" + branch;
        File currentFolder = new File(rootFolder + folder);
        if (!currentFolder.exists()) {
            currentFolder.mkdirs();
        }
        String fileName = author + "-" + fileCreateTime + ".md";
        File newFile = new File(currentFolder, fileName);
        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write(recommend);
        }

        // 提交内容
        git.add().addFilepattern(currentFolder + "/" + fileName).call();
        git.commit().setMessage("add code review new file" + fileName).call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, "")).call();

        logger.info("llm-code-review git commit and push done! {}", fileName);

        return githubReviewLogUri + "/blob/master/" + folder + "/" + fileName;
    }
}
