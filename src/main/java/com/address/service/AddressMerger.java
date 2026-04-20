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

    public List<CifAddress> mergeStock(List<CifAddress> stock, List<CifAddress> toDelete) {
        // Step 1: 存量自身去重合并（与 mergeIncoming 对称）
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

        // Step 2: 标记需要删除的地址（在上送中出现的）
        List<CifAddress> mergedList = new ArrayList<>(merged.values());
        for (CifAddress addr : mergedList) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            for (CifAddress td : toDelete) {
                String tdKey = td.getAddressType() + "_" + td.getAddressDetail();
                if (key.equals(tdKey)) {
                    addr.setDelFlag("Y");
                    break;
                }
            }
        }
        return mergedList;
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
