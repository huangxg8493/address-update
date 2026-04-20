package com.address.strategy.impl;

import com.address.model.CifAddress;
import com.address.strategy.NewestAddressStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriorityNewestAddressStrategy implements NewestAddressStrategy {

    @Override
    public Map<String, CifAddress> selectByType(List<CifAddress> mergedIncoming, List<CifAddress> mergedStock) {
        return new HashMap<>();
    }
}
