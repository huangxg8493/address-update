package com.address.strategy.impl;

import com.address.model.AddressType;
import com.address.model.CifAddress;
import com.address.strategy.MailingAddressStrategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PriorityMailingAddressStrategy implements MailingAddressStrategy {

    /**
     * 挑选通讯地址
     * 规则优先级（见需求文档）：
     * 1. 若接口上送了通讯地址（isMailingAddress='Y'），按优先级顺序选择
     * 2. 若接口没上送，选存量地址中 isMailingAddress='Y' 且修改时间最大的
     * 3. 若存量中也没有，选所有地址中优先级最高的类型且修改时间最大的
     */
    @Override
    public CifAddress select(List<CifAddress> mergedIncoming, List<CifAddress> mergedStock) {
        // Step 1: 合并上送地址和存量地址，del_flag='Y' 的排除
        List<CifAddress> allActive = new ArrayList<>(mergedIncoming);
        allActive.addAll(mergedStock);

        // Step 2: 从所有地址中找 isMailingAddress='Y' 且修改时间最大的
        Optional<CifAddress> mailingFromAll = allActive.stream()
                .filter(a -> "Y".equals(a.getIsMailingAddress()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .max(Comparator.comparing(a ->
                        a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

        if (mailingFromAll.isPresent()) {
            return mailingFromAll.get();
        }

        // Step 3: 上送中没有Y标记的，从存量中找 isMailingAddress='Y' 且修改时间最大的
        Optional<CifAddress> mailingFromStock = mergedStock.stream()
                .filter(a -> "Y".equals(a.getIsMailingAddress()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .max(Comparator.comparing(a ->
                        a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

        if (mailingFromStock.isPresent()) {
            return mailingFromStock.get();
        }

        // Step 4: 存量中也没有Y标记的，按优先级从所有地址中选每种类型修改时间最大的
        for (AddressType type : AddressType.values()) {
            Optional<CifAddress> latestOfType = allActive.stream()
                    .filter(a -> type.getCode().equals(a.getAddressType()))
                    .filter(a -> !"Y".equals(a.getDelFlag()))
                    .max(Comparator.comparing(a ->
                            a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

            if (latestOfType.isPresent()) {
                return latestOfType.get();
            }
        }

        return null;
    }
}
