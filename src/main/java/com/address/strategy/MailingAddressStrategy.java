package com.address.strategy;

import com.address.model.CifAddress;
import java.util.List;

public interface MailingAddressStrategy {
    CifAddress select(List<CifAddress> mergedIncoming, List<CifAddress> mergedStock);
}
