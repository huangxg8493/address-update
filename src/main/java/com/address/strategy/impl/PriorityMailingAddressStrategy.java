package com.address.strategy.impl;

import com.address.model.AddressType;
import com.address.model.CifAddress;
import com.address.strategy.MailingAddressStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PriorityMailingAddressStrategy implements MailingAddressStrategy {

    /**
     * 挑选通讯地址
     * 规则（见需求文档第8、9、10条）：
     * 第8条：若接口上送了通讯地址（isMailingAddress='Y'），按优先级顺序选择
     * 第9条：若接口没上送Y标记的，选存量地址中 isMailingAddress='Y' 且修改时间最大的
     * 第10条：若存量也没有Y标记的，按优先级选每种类型最后修改的地址（先从上送选，再到存量）
     */
    @Override
    public CifAddress select(List<CifAddress> mergedIncoming, List<CifAddress> mergedStock) {
        // 第8条：上送中有Y标记的，按优先级选第一个（先按类型优先级排序，再取第一个）
        CifAddress mailingFromIncoming = mergedIncoming.stream()
                .filter(a -> "Y".equals(a.getIsMailingAddress()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .min(Comparator.comparing(a -> {
                    AddressType type = AddressType.fromCode(a.getAddressType());
                    return type.ordinal();
                }))
                .orElse(null);

        if (mailingFromIncoming != null) {
            return mailingFromIncoming;
        }

        // 第9条：上送没有Y标记的，从存量中选Y标记且修改时间最大的
        CifAddress mailingFromStock = mergedStock.stream()
                .filter(a -> "Y".equals(a.getIsMailingAddress()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .max(Comparator.comparing(a ->
                        a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE))
                .orElse(null);

        if (mailingFromStock != null) {
            return mailingFromStock;
        }

        // 第10条：都没有Y标记的，按优先级选每种类型修改时间最大的（先上送后存量）
        for (AddressType type : AddressType.values()) {
            String typeCode = type.getCode();

            CifAddress fromIncoming = mergedIncoming.stream()
                    .filter(a -> typeCode.equals(a.getAddressType()))
                    .filter(a -> !"Y".equals(a.getDelFlag()))
                    .max(Comparator.comparing(a ->
                            a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE))
                    .orElse(null);

            if (fromIncoming != null) {
                return fromIncoming;
            }

            CifAddress fromStock = mergedStock.stream()
                    .filter(a -> typeCode.equals(a.getAddressType()))
                    .filter(a -> !"Y".equals(a.getDelFlag()))
                    .max(Comparator.comparing(a ->
                            a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE))
                    .orElse(null);

            if (fromStock != null) {
                return fromStock;
            }
        }

        return null;
    }
}
