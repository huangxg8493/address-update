package com.address.strategy;

import com.address.model.CifAddress;
import java.util.List;
import java.util.Map;

public interface NewestAddressStrategy {
    Map<String, CifAddress> selectByType(List<CifAddress> addresses);
}
