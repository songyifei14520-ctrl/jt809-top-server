package cn.com.onlinetool.jt809.netty.codec.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * JT809报文检测工具类
 * 用于验证JT809协议报文的正确性
 *
 * @author system
 * @since 2024-01-01
 */
public class JT809MessageValidator {

    private static final Logger log = LoggerFactory.getLogger(JT809MessageValidator.class);

    /**
     * 验证API地址
     */
    private static final String VALIDATE_URL = "https://jttools.smallchi.cn/jtt/JT809/Analyze";

    /**
     * HTTP客户端
     */
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * JSON处理器
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 验证JT809报文
     *
     * @param hexData 十六进制报文数据
     * @param protocolType 协议类型（"2011" 或 "2019"）
     * @param isEncrypt 是否加密
     * @return 验证结果
     */
    public static ValidationResult validateMessage(String hexData, String protocolType, boolean isEncrypt) {
        return validateMessage(hexData, protocolType, isEncrypt, 0, 0, 0);
    }

    /**
     * 验证JT809报文（完整参数）
     *
     * @param hexData 十六进制报文数据
     * @param protocolType 协议类型（"2011" 或 "2019"）
     * @param isEncrypt 是否加密
     * @param m1 M1参数
     * @param ia1 IA1参数
     * @param ic1 IC1参数
     * @return 验证结果
     */
    public static ValidationResult validateMessage(String hexData, String protocolType, boolean isEncrypt,
                                                 int m1, int ia1, int ic1) {
        try {
            // 构建请求参数
            ValidationRequest request = new ValidationRequest();
            request.setHex(hexData);
            request.setProtocolType(protocolType);
            request.setIsEncrypt(isEncrypt);
            request.setM1(m1);
            request.setIa1(ia1);
            request.setIc1(ic1);

            // 转换为JSON
            String jsonBody = OBJECT_MAPPER.writeValueAsString(request);

            // 构建HTTP请求
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonBody);

            Request httpRequest = new Request.Builder()
                    .url(VALIDATE_URL)
                    .method("POST", body)
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Origin", "https://jttools.smallchi.cn")
                    .addHeader("Referer", "https://jttools.smallchi.cn/")
                    .addHeader("Sec-Fetch-Dest", "empty")
                    .addHeader("Sec-Fetch-Mode", "cors")
                    .addHeader("Sec-Fetch-Site", "same-origin")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36")
                    .addHeader("sec-ch-ua", "\"Not A(Brand\";v=\"8\", \"Chromium\";v=\"132\", \"Google Chrome\";v=\"132\"")
                    .addHeader("sec-ch-ua-mobile", "?0")
                    .addHeader("sec-ch-ua-platform", "\"Windows\"")
                    .build();

            // 执行请求
            try (Response response = CLIENT.newCall(httpRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    log.debug("验证响应: {}", responseBody);

                    // 解析响应结果
                    ValidationResponse validationResponse = OBJECT_MAPPER.readValue(responseBody, ValidationResponse.class);
                    
                    boolean isValid = validationResponse.isSuccess() && validationResponse.getData() != null;
                    String resultMessage = validationResponse.getMessage();
                    if (isValid && (resultMessage == null || resultMessage.trim().isEmpty())) {
                        resultMessage = "报文解析成功";
                    }

                    return ValidationResult.builder()
                            .success(true)
                            .valid(isValid)
                            .message(resultMessage)
                            .data(validationResponse.getData())
                            .rawResponse(responseBody)
                            .build();
                } else {
                    String errorMsg = String.format("HTTP请求失败，状态码: %d", response.code());
                    log.error(errorMsg);
                    return ValidationResult.builder()
                            .success(false)
                            .valid(false)
                            .message(errorMsg)
                            .build();
                }
            }

        } catch (IOException e) {
            String errorMsg = "网络请求异常: " + e.getMessage();
            log.error(errorMsg, e);
            return ValidationResult.builder()
                    .success(false)
                    .valid(false)
                    .message(errorMsg)
                    .build();
        } catch (Exception e) {
            String errorMsg = "验证过程异常: " + e.getMessage();
            log.error(errorMsg, e);
            return ValidationResult.builder()
                    .success(false)
                    .valid(false)
                    .message(errorMsg)
                    .build();
        }
    }

    /**
     * 验证JT809-2019报文（默认参数）
     *
     * @param hexData 十六进制报文数据
     * @return 验证结果
     */
    public static ValidationResult validateJT809_2019(String hexData) {
        return validateMessage(hexData, "2019", true);
    }

    /**
     * 验证JT809-2011报文（默认参数）
     *
     * @param hexData 十六进制报文数据
     * @return 验证结果
     */
    public static ValidationResult validateJT809_2011(String hexData) {
        return validateMessage(hexData, "2011", false);
    }

    /**
     * 验证请求参数
     */
    public static class ValidationRequest {
        @JsonProperty("Hex")
        private String hex;

        @JsonProperty("ProtocolType")
        private String protocolType;

        @JsonProperty("IsEncrypt")
        private boolean isEncrypt;

        @JsonProperty("M1")
        private int m1;

        @JsonProperty("IA1")
        private int ia1;

        @JsonProperty("IC1")
        private int ic1;

        // Getters and Setters
        public String getHex() { return hex; }
        public void setHex(String hex) { this.hex = hex; }

        public String getProtocolType() { return protocolType; }
        public void setProtocolType(String protocolType) { this.protocolType = protocolType; }

        public boolean getIsEncrypt() { return isEncrypt; }
        public void setIsEncrypt(boolean isEncrypt) { this.isEncrypt = isEncrypt; }

        public int getM1() { return m1; }
        public void setM1(int m1) { this.m1 = m1; }

        public int getIa1() { return ia1; }
        public void setIa1(int ia1) { this.ia1 = ia1; }

        public int getIc1() { return ic1; }
        public void setIc1(int ic1) { this.ic1 = ic1; }
    }

    /**
     * 验证响应结果
     */
    public static class ValidationResponse {
        @JsonProperty("Message")
        private String message;
        
        @JsonProperty("Code")
        private int code;
        
        @JsonProperty("Result")
        private ApiResult result;

        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }

        public ApiResult getResult() { return result; }
        public void setResult(ApiResult result) { this.result = result; }
        
        public boolean isSuccess() { return code == 200; }
        public Object getData() { return result != null ? result.getJsonValue() : null; }
        
        /**
         * API结果类
         */
        public static class ApiResult {
            @JsonProperty("JsonValue")
            private String jsonValue;
            
            public String getJsonValue() { return jsonValue; }
            public void setJsonValue(String jsonValue) { this.jsonValue = jsonValue; }
        }
    }

    /**
     * 验证结果
     */
    public static class ValidationResult {
        private boolean success;    // 请求是否成功
        private boolean valid;      // 报文是否有效
        private String message;     // 结果消息
        private Object data;        // 响应数据
        private String rawResponse; // 原始响应数据

        private ValidationResult() {}

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private ValidationResult result = new ValidationResult();

            public Builder success(boolean success) {
                result.success = success;
                return this;
            }

            public Builder valid(boolean valid) {
                result.valid = valid;
                return this;
            }

            public Builder message(String message) {
                result.message = message;
                return this;
            }

            public Builder data(Object data) {
                result.data = data;
                return this;
            }
            
            public Builder rawResponse(String rawResponse) {
                result.rawResponse = rawResponse;
                return this;
            }

            public ValidationResult build() {
                return result;
            }
        }

        // Getters
        public boolean isSuccess() { return success; }
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
        public String getRawResponse() { return rawResponse; }
        
        /**
         * 获取格式化的解析结果
         * @return 格式化的JSON字符串，如果解析失败则返回原始消息
         */
        public String getFormattedResult() {
            if (valid && data != null) {
                try {
                    // 尝试格式化JSON数据
                    Object jsonObj = OBJECT_MAPPER.readValue(data.toString(), Object.class);
                    return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
                } catch (Exception e) {
                    log.debug("格式化JSON失败: {}", e.getMessage());
                    return data.toString();
                }
            }
            return message != null ? message : "解析失败";
        }

        @Override
        public String toString() {
            if (valid && data != null) {
                return String.format("验证成功 - %s\n解析结果:\n%s", 
                    message != null ? message : "报文解析成功", 
                    getFormattedResult());
            } else {
                return String.format("验证失败 - %s", 
                    message != null ? message : "未知错误");
            }
        }
    }
}
