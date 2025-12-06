package com.example.cailanzi.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * OCR解析工具类
 * 用于解析行驶证OCR识别结果
 */
@Slf4j
public class OcrParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 行驶证OCR识别结果数据结构
     */
    public static class DrivingLicenseData {
        private String plateNumber;      // 车牌号
        private String vehicleType;      // 车辆类型
        private String owner;            // 所有人
        private String address;          // 住址
        private String useCharacter;     // 使用性质
        private String model;            // 品牌型号
        private String vin;              // 车辆识别代号
        private String engineNo;         // 发动机号码
        private String registerDate;     // 注册日期
        private String issueDate;        // 发证日期
        private String vehicleColor;     // 车辆颜色
        private String approvedLoad;     // 核定载质量

        // Getters and Setters
        public String getPlateNumber() {
            return plateNumber;
        }

        public void setPlateNumber(String plateNumber) {
            this.plateNumber = plateNumber;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getUseCharacter() {
            return useCharacter;
        }

        public void setUseCharacter(String useCharacter) {
            this.useCharacter = useCharacter;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public String getEngineNo() {
            return engineNo;
        }

        public void setEngineNo(String engineNo) {
            this.engineNo = engineNo;
        }

        public String getRegisterDate() {
            return registerDate;
        }

        public void setRegisterDate(String registerDate) {
            this.registerDate = registerDate;
        }

        public String getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(String issueDate) {
            this.issueDate = issueDate;
        }

        public String getVehicleColor() {
            return vehicleColor;
        }

        public void setVehicleColor(String vehicleColor) {
            this.vehicleColor = vehicleColor;
        }

        public String getApprovedLoad() {
            return approvedLoad;
        }

        public void setApprovedLoad(String approvedLoad) {
            this.approvedLoad = approvedLoad;
        }
    }

    /**
     * 解析JSON格式的OCR识别结果
     * @param jsonStr JSON字符串
     * @return DrivingLicenseData对象
     */
    public static DrivingLicenseData parseDrivingLicense(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(jsonStr, DrivingLicenseData.class);
        } catch (Exception e) {
            log.error("解析行驶证OCR数据失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将行驶证数据转换为JSON字符串
     * @param data 行驶证数据
     * @return JSON字符串
     */
    public static String toJson(DrivingLicenseData data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("转换行驶证数据为JSON失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析为Map格式（兼容各种OCR服务商的不同格式）
     * @param jsonStr JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> parseToMap(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("解析OCR数据为Map失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    /**
     * 从Map中提取字段值
     * @param dataMap 数据Map
     * @param key 字段key
     * @return 字段值
     */
    public static String getFieldValue(Map<String, Object> dataMap, String key) {
        if (dataMap == null || key == null) {
            return null;
        }
        Object value = dataMap.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 从OCR结果中提取车牌号
     * @param jsonStr JSON字符串
     * @return 车牌号
     */
    public static String extractPlateNumber(String jsonStr) {
        DrivingLicenseData data = parseDrivingLicense(jsonStr);
        return data != null ? data.getPlateNumber() : null;
    }

    /**
     * 从OCR结果中提取VIN码
     * @param jsonStr JSON字符串
     * @return VIN码
     */
    public static String extractVin(String jsonStr) {
        DrivingLicenseData data = parseDrivingLicense(jsonStr);
        return data != null ? data.getVin() : null;
    }

    /**
     * 从OCR结果中提取发动机号
     * @param jsonStr JSON字符串
     * @return 发动机号
     */
    public static String extractEngineNo(String jsonStr) {
        DrivingLicenseData data = parseDrivingLicense(jsonStr);
        return data != null ? data.getEngineNo() : null;
    }
}
