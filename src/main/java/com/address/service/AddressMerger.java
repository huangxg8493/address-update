package com.address.service;

import com.address.model.CifAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressMerger {

    public List<CifAddress> mergeIncoming(List<CifAddress> incoming) {
        Map<String, CifAddress> merged = new HashMap<>();

        for (CifAddress addr : incoming) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
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
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            CifAddress existing = firstSeen.get(key);
            if (existing == null) {
                firstSeen.put(key, addr);
                addr.setDelFlag("N");
                result.add(addr);
            } else {
                String mailing = "Y".equals(existing.getIsMailingAddress()) || "Y".equals(addr.getIsMailingAddress()) ? "Y" : "N";
                String newest = "Y".equals(existing.getIsNewest()) || "Y".equals(addr.getIsNewest()) ? "Y" : "N";
                existing.setIsMailingAddress(mailing);
                existing.setIsNewest(newest);
                existing.setLastChangeDate(new Date());
                addr.setDelFlag("Y");
                result.add(addr);
            }
        }
        return result;
    }

    private CifAddress mergeTwoForStock(CifAddress a, CifAddress b) {
        CifAddress result = new CifAddress();
        result.setSeqNo(a.getSeqNo());
        result.setClientNo(a.getClientNo());
        result.setAddressType(a.getAddressType());
        result.setAddressDetail(a.getAddressDetail());
        result.setLastChangeDate(new Date());
        result.setDelFlag("N");

        String mailing = "Y".equals(a.getIsMailingAddress()) || "Y".equals(b.getIsMailingAddress()) ? "Y" : "N";
        String newest = "Y".equals(a.getIsNewest()) || "Y".equals(b.getIsNewest()) ? "Y" : "N";
        result.setIsMailingAddress(mailing);
        result.setIsNewest(newest);
        return result;
    }

    private CifAddress mergeTwo(CifAddress a, CifAddress b) {
        CifAddress result = new CifAddress();
        result.setSeqNo(a.getSeqNo());
        result.setClientNo(a.getClientNo());
        result.setAddressType(a.getAddressType());
        result.setAddressDetail(a.getAddressDetail());
        result.setLastChangeDate(new Date());

        String mailing = "Y".equals(a.getIsMailingAddress()) || "Y".equals(b.getIsMailingAddress()) ? "Y" : "N";
        String newest = "Y".equals(a.getIsNewest()) || "Y".equals(b.getIsNewest()) ? "Y" : "N";

        result.setIsMailingAddress(mailing);
        result.setIsNewest(newest);
        result.setDelFlag("N");

        return result;
    }
}
