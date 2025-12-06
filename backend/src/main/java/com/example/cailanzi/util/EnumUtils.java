package com.example.cailanzi.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 枚举工具类
 */
public class EnumUtils {

    /**
     * 获取枚举的所有值
     * @param enumClass 枚举类
     * @return 枚举值列表
     */
    public static <T extends Enum<T>> List<String> getEnumNames(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    /**
     * 获取枚举的所有显示值（中文）
     * @param enumClass 枚举类
     * @return 枚举显示值列表
     */
    public static <T extends Enum<T>> List<String> getEnumValues(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::toString)
                .collect(Collectors.toList());
    }

    /**
     * 根据名称获取枚举
     * @param enumClass 枚举类
     * @param name 枚举名称
     * @return 枚举实例，如果不存在返回null
     */
    public static <T extends Enum<T>> T getEnumByName(Class<T> enumClass, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 检查枚举值是否有效
     * @param enumClass 枚举类
     * @param value 要检查的值
     * @return 是否有效
     */
    public static <T extends Enum<T>> boolean isValidEnum(Class<T> enumClass, String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        try {
            Enum.valueOf(enumClass, value);
            return true;
        } catch (IllegalArgumentException ex) {
            // 也尝试通过toString匹配（中文枚举值）
            return Arrays.stream(enumClass.getEnumConstants())
                    .anyMatch(enumValue -> enumValue.toString().equals(value));
        }
    }

    /**
     * 根据显示值（中文）获取枚举
     * @param enumClass 枚举类
     * @param value 显示值
     * @return 枚举实例，如果不存在返回null
     */
    public static <T extends Enum<T>> T getEnumByValue(Class<T> enumClass, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(enumValue -> enumValue.toString().equals(value))
                .findFirst()
                .orElse(null);
    }
}
