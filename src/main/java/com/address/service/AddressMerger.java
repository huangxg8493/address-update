package com.address.service;

import com.address.constants.Constants;
import com.address.model.CifAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class AddressMerger {

    public List<CifAddress> mergeIncoming(List<CifAddress> incoming) {
        Map<String, CifAddress> merged = new HashMap<>();

        for (CifAddress addr : incoming) {
            String key = addr.getAddressType() + Constants.KEY_SEPARATOR + addr.getAddressDetail();
            CifAddress existing = merged.get(key);

            if (existing == null) {
                merged.put(key, addr);
            } else {
                CifAddress mergedAddr = mergeTwo(existing, addr);
                merged.put(key, mergedAddr);
            }
        }

        return new ArrayList<>(merged.values());
    }

    public List<CifAddress> mergeStock(List<CifAddress> stock) {
        Map<String, CifAddress> firstSeen = new HashMap<>();
        List<CifAddress> result = new ArrayList<>();

        for (CifAddress addr : stock) {
            String key = addr.getAddressType() + Constants.KEY_SEPARATOR + addr.getAddressDetail();
            CifAddress existing = firstSeen.get(key);
            if (existing == null) {
                firstSeen.put(key, addr);
                addr.setDelFlag(Constants.NO);
                result.add(addr);
            } else {
                String mailing = Constants.YES.equals(existing.getIsMailingAddress()) || Constants.YES.equals(addr.getIsMailingAddress()) ? Constants.YES : Constants.NO;
                String newest = Constants.YES.equals(existing.getIsNewest()) || Constants.YES.equals(addr.getIsNewest()) ? Constants.YES : Constants.NO;
                existing.setIsMailingAddress(mailing);
                existing.setIsNewest(newest);
                Date maxDate = existing.getLastChangeDate();
                if (addr.getLastChangeDate() != null && (maxDate == null || addr.getLastChangeDate().after(maxDate))) {
                    maxDate = addr.getLastChangeDate();
                }
                existing.setLastChangeDate(maxDate);
                addr.setDelFlag(Constants.YES);
                result.add(addr);
            }
        }
        return result;
    }

    private CifAddress mergeTwo(CifAddress a, CifAddress b) {
        CifAddress result = new CifAddress();
        result.setSeqNo(a.getSeqNo());
        result.setClientNo(a.getClientNo());
        result.setAddressType(a.getAddressType());
        result.setAddressDetail(a.getAddressDetail());
        result.setLastChangeDate(new Date());

        String mailing = Constants.YES.equals(a.getIsMailingAddress()) || Constants.YES.equals(b.getIsMailingAddress()) ? Constants.YES : Constants.NO;
        String newest = Constants.YES.equals(a.getIsNewest()) || Constants.YES.equals(b.getIsNewest()) ? Constants.YES : Constants.NO;

        result.setIsMailingAddress(mailing);
        result.setIsNewest(newest);
        result.setDelFlag(Constants.NO);

        return result;
    }
}
