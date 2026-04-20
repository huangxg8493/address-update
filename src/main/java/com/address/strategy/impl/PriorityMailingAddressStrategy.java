package com.address.strategy.impl;

import com.address.model.AddressType;
import com.address.model.CifAddress;
import com.address.strategy.MailingAddressStrategy;

import java.util.List;

public class PriorityMailingAddressStrategy implements MailingAddressStrategy {

    /**
     * 挑选通讯地址
     * 规则（见需求文档第8、9、10条）：
     * 第8条：若接口上送了通讯地址（isMailingAddress='Y'），按优先级顺序选择
     * 第9条：若接口没上送Y标记的，选存量地址中 isMailingAddress='Y' 且修改时间最大的
     * 第10条：若存量也没有Y标记的，按优先级选每种类型最后修改的地址（先从上送选，再到存量）
     */
    @Override
    public CifAddress select(List<CifAddress> mergedIncoming, List<CifAddress> mergedStock) {
        // 第8条：按优先级遍历每种类型，选该类型中 isMailingAddress='Y' 的第一个
        for (AddressType type : AddressType.values()) {
            CifAddress found = findMailing(mergedIncoming, type.getCode());
            if (found != null) {
                return found;
            }
        }

        // 第9条：上送中没有Y标记的，从存量中找 isMailingAddress='Y' 且修改时间最大的
        CifAddress mailingFromStock = findMaxLastChangeWithCond(mergedStock, a -> "Y".equals(a.getIsMailingAddress()));
        if (mailingFromStock != null) {
            return mailingFromStock;
        }

        // 第10条：按优先级从所有地址中选每种类型修改时间最大的（先上送后存量）
        for (AddressType type : AddressType.values()) {
            CifAddress latest = findMaxLastChange(mergedIncoming, type.getCode());
            if (latest == null) {
                latest = findMaxLastChange(mergedStock, type.getCode());
            }
            if (latest != null) {
                return latest;
            }
        }

        return null;
    }

    /**
     * 从地址列表中找 isMailingAddress='Y' 且属于指定类型的第一个地址
     */
    private CifAddress findMailing(List<CifAddress> addresses, String addressType) {
        for (CifAddress a : addresses) {
            if (!"Y".equals(a.getDelFlag()) && "Y".equals(a.getIsMailingAddress()) && addressType.equals(a.getAddressType())) {
                return a;
            }
        }
        return null;
    }

    /**
     * 从地址列表中找指定类型中 lastChangeDate 最大的地址（不考虑其他条件）
     */
    private CifAddress findMaxLastChange(List<CifAddress> addresses, String addressType) {
        CifAddress result = null;
        for (CifAddress a : addresses) {
            if (!"Y".equals(a.getDelFlag()) && addressType.equals(a.getAddressType())) {
                if (result == null || (a.getLastChangeDate() != null && a.getLastChangeDate().after(result.getLastChangeDate()))) {
                    result = a;
                }
            }
        }
        return result;
    }

    /**
     * 从地址列表中找符合条件且 lastChangeDate 最大的地址
     */
    private CifAddress findMaxLastChangeWithCond(List<CifAddress> addresses, CifAddressPredicate cond) {
        CifAddress result = null;
        for (CifAddress a : addresses) {
            if (!"Y".equals(a.getDelFlag()) && cond.test(a)) {
                if (result == null || (a.getLastChangeDate() != null && a.getLastChangeDate().after(result.getLastChangeDate()))) {
                    result = a;
                }
            }
        }
        return result;
    }

    @FunctionalInterface
    interface CifAddressPredicate {
        boolean test(CifAddress a);
    }
}
