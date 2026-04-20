package com.address.strategy.impl;

import com.address.model.CifAddress;
import com.address.strategy.NewestAddressStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PriorityNewestAddressStrategy implements NewestAddressStrategy {

    @Override
    public Map<String, CifAddress> selectByType(List<CifAddress> addresses) {
        Map<String, CifAddress> result = new HashMap<>();

        Map<String, List<CifAddress>> byType = addresses.stream()
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .collect(Collectors.groupingBy(CifAddress::getAddressType));

        for (Map.Entry<String, List<CifAddress>> entry : byType.entrySet()) {
            Optional<CifAddress> newest = entry.getValue().stream()
                    .max((a, b) -> {
                        long timeA = a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE;
                        long timeB = b.getLastChangeDate() != null ? b.getLastChangeDate().getTime() : Long.MIN_VALUE;
                        return Long.compare(timeA, timeB);
                    });
            newest.ifPresent(addr -> result.put(entry.getKey(), addr));
        }

        return result;
    }
}
