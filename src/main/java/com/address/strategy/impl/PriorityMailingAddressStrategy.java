package com.address.strategy.impl;

import com.address.model.AddressType;
import com.address.model.CifAddress;
import com.address.strategy.MailingAddressStrategy;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PriorityMailingAddressStrategy implements MailingAddressStrategy {

    @Override
    public CifAddress select(List<CifAddress> mergedIncoming, List<CifAddress> mergedStock) {
        java.util.List<CifAddress> allActive = new java.util.ArrayList<>(mergedIncoming);
        allActive.addAll(mergedStock);
        Optional<CifAddress> mailingAddr = allActive.stream()
                .filter(a -> "Y".equals(a.getIsMailingAddress()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .max(Comparator.comparing(a -> a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

        if (mailingAddr.isPresent()) {
            return mailingAddr.get();
        }

        for (AddressType type : AddressType.values()) {
            Optional<CifAddress> latest = allActive.stream()
                    .filter(a -> type.getCode().equals(a.getAddressType()))
                    .filter(a -> !"Y".equals(a.getDelFlag()))
                    .max(Comparator.comparing(a -> a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

            if (latest.isPresent()) {
                return latest.get();
            }
        }
        return null;
    }
}
