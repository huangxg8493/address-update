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
        Map<String, CifAddress> merged = new HashMap<>();
        for (CifAddress addr : stock) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            CifAddress existing = merged.get(key);
            if (existing == null) {
                merged.put(key, addr);
            } else {
                CifAddress mergedAddr = mergeTwoForStock(existing, addr);
                merged.put(key, mergedAddr);
            }
        }
        return new ArrayList<>(merged.values());
    }

    public void markDeleted(List<CifAddress> stock, List<CifAddress> incoming) {
        for (CifAddress addr : stock) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            for (CifAddress inc : incoming) {
                String incKey = inc.getAddressType() + "_" + inc.getAddressDetail();
                if (key.equals(incKey)) {
                    addr.setDelFlag("Y");
                    break;
                }
            }
        }
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
