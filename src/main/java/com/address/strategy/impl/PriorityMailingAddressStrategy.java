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
        // 第8条：按优先级遍历每种类型，选该类型中 isMailingAddress='Y' 的第一个
        for (AddressType type : AddressType.values()) {
            Optional<CifAddress> mailingOfType = mergedIncoming.stream()
                    .filter(a -> "Y".equals(a.getIsMailingAddress()))
                    .filter(a -> type.getCode().equals(a.getAddressType()))
                    .filter(a -> !"Y".equals(a.getDelFlag()))
                    .findFirst();

            if (mailingOfType.isPresent()) {
                return mailingOfType.get();
            }
        }

        // 第9条：上送中没有Y标记的，从存量中找 isMailingAddress='Y' 且修改时间最大的
        Optional<CifAddress> mailingFromStock = mergedStock.stream()
                .filter(a -> "Y".equals(a.getIsMailingAddress()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .max(Comparator.comparing(a ->
                        a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

        if (mailingFromStock.isPresent()) {
            return mailingFromStock.get();
        }

        // 第10条：存量中也没有Y标记的，按优先级从所有地址中选每种类型修改时间最大的（先上送后存量）
        for (AddressType type : AddressType.values()) {
            // 先从上送地址中找该类型修改时间最大的
            Optional<CifAddress> latestFromIncoming = mergedIncoming.stream()
                    .filter(a -> type.getCode().equals(a.getAddressType()))
                    .filter(a -> !"Y".equals(a.getDelFlag()))
                    .max(Comparator.comparing(a ->
                            a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

            if (latestFromIncoming.isPresent()) {
                return latestFromIncoming.get();
            }

            // 上送中没有，再从存量中找
            Optional<CifAddress> latestFromStock = mergedStock.stream()
                    .filter(a -> type.getCode().equals(a.getAddressType()))
                    .filter(a -> !"Y".equals(a.getDelFlag()))
                    .max(Comparator.comparing(a ->
                            a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

            if (latestFromStock.isPresent()) {
                return latestFromStock.get();
            }
        }

        return null;
    }
}
