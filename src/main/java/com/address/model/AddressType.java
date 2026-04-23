package com.address.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AddressType {
    OTHER("01"),           // 其他地址
    CONTACT("02"),         // 联系地址
    RESIDENCE("03"),       // 居住地址
    COMPANY("04"),         // 单位地址
    HOUSEHOLD("05"),       // 户籍地址
    CERTIFICATE("06"),     // 证件地址
    BUSINESS("07"),        // 营业地址
    REGISTERED("08"),      // 注册地址
    OFFICE("09"),          // 办公地址
    PERMANENT("10");       // 永久地址

    private static final Logger logger = LoggerFactory.getLogger(AddressType.class);

    private final String code;

    AddressType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AddressType fromCode(String code) {
        for (AddressType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        logger.error("无效的地址类型编码: {}", code);
        throw new RuntimeException("无效的地址类型编码: " + code);
    }
}
