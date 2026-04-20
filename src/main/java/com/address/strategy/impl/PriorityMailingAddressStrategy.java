package com.address.strategy.impl;

import com.address.model.CifAddress;
import com.address.strategy.MailingAddressStrategy;
import java.util.List;

public class PriorityMailingAddressStrategy implements MailingAddressStrategy {

    @Override
    public CifAddress select(List<CifAddress> mergedIncoming, List<CifAddress> mergedStock) {
        return null;
    }
}
