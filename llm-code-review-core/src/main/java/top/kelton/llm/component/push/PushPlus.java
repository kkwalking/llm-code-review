package top.kelton.llm.component.push;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import top.kelton.llm.component.push.dto.PushPlusRequestDTO;
import top.kelton.llm.component.push.dto.PushPlusResponseDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class PushPlus implements IPushExecutor {

    private static final Logger logger = Logger.getLogger(PushPlus.class.getName());

    /**
     * http://www.pushplus.plus/send
     */
    private String host = "http://www.pushplus.plus/send";
    private String token;

    public PushPlus(String host, String token) {
        this.host = host;
        this.token = token;
    }


    @Override
    public boolean send(String message) throws Exception {
        return send(message, PushTemplate.TXT);
    }

    @Override
    public boolean send(String message, PushTemplate template) throws Exception {
        URL url = new URL(host);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);

        PushPlusRequestDTO request = new PushPlusRequestDTO();
        request.setToken(token);
        request.setTitle("大模型自动代码评审");
        request.setTemplate(template.getCode());
        request.setContent(message);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();
        PushPlusResponseDTO response = JSON.parseObject(content.toString(), PushPlusResponseDTO.class);
        logger.info(JSON.toJSONString(response));
        return response.getCode() == 200;
    }
}
