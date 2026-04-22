package com.address.repository;

import com.address.model.CifAddress;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CifAddressMapper {

    @Update("UPDATE CIF_ADDRESS SET DEL_FLAG = 'Y' WHERE SEQ_NO = #{seqNo}")
    void delete(@Param("seqNo") String seqNo);

    List<CifAddress> findByClientNo(@Param("clientNo") String clientNo, @Param("delFlag") String delFlag);

    void save(CifAddress address);

    void update(CifAddress address);

    void saveAll(List<CifAddress> addresses);

    void batchUpdate(List<CifAddress> addresses);
}
