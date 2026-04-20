package com.address.strategy.impl;

import com.address.model.CifAddress;
import com.address.strategy.NewestAddressStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class PriorityNewestAddressStrategy implements NewestAddressStrategy {

    @Override
    public Map<String, CifAddress> selectByType(List<CifAddress> mergedIncoming, List<CifAddress> mergedStock) {
        Map<String, CifAddress> result = new HashMap<>();

        // 收集所有地址类型
        Set<String> allTypes = new HashSet<>();
        mergedIncoming.stream()
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .forEach(a -> allTypes.add(a.getAddressType()));
        mergedStock.stream()
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .forEach(a -> allTypes.add(a.getAddressType()));

        for (String type : allTypes) {
            CifAddress newest = selectNewestForType(type, mergedIncoming, mergedStock);
            if (newest != null) {
                result.put(type, newest);
            }
        }

        return result;
    }

    private CifAddress selectNewestForType(String type, List<CifAddress> mergedIncoming, List<CifAddress> mergedStock) {
        List<CifAddress> incomingOfType = mergedIncoming.stream()
                .filter(a -> type.equals(a.getAddressType()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .collect(Collectors.toList());

        // 第10条：若接口中有上送，选 isNewest='Y' 的；若没有Y标记的，取最后修改的（lastChangeDate最大的）
        if (!incomingOfType.isEmpty()) {
            CifAddress newest = incomingOfType.stream()
                    .filter(a -> "Y".equals(a.getIsNewest()))
                    .findFirst()
                    .orElse(incomingOfType.stream()
                            .max(Comparator.comparing(a ->
                                    a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE))
                            .orElse(null));
            return newest;
        }

        // 第11、12条：若接口没上送，从存量地址中选
        List<CifAddress> stockOfType = mergedStock.stream()
                .filter(a -> type.equals(a.getAddressType()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .collect(Collectors.toList());

        if (stockOfType.isEmpty()) {
            return null;
        }

        // 选 isNewest='Y' 且修改时间最大的
        Optional<CifAddress> stockNewest = stockOfType.stream()
                .filter(a -> "Y".equals(a.getIsNewest()))
                .max(Comparator.comparing(a ->
                        a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

        if (stockNewest.isPresent()) {
            return stockNewest.get();
        }

        // 若没有 isNewest='Y' 的，选修改时间最大的（null 视为最小）
        return stockOfType.stream()
                .max(Comparator.comparing(a ->
                        a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE))
                .orElse(null);
    }
}
