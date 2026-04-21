package com.address.repository;

import com.address.constants.Constants;
import com.address.model.CifAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Primary
public class JdbcTemplateClientAddressRepository implements ClientAddressRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateClientAddressRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateClientAddressRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        String sql = "SELECT SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, LAST_CHANGE_DATE, " +
                     "IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG FROM CIF_ADDRESS WHERE CLIENT_NO = ? AND DEL_FLAG = ?";
        return jdbcTemplate.query(sql, new CifAddressRowMapper(), clientNo, Constants.NO);
    }

    @Override
    public void save(CifAddress address) {
        logger.info("保存地址 clientNo={}", address.getClientNo());
        String sql = "INSERT INTO CIF_ADDRESS (SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, " +
                     "LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, address.getSeqNo(), address.getClientNo(), address.getAddressType(),
                address.getAddressDetail(), address.getLastChangeDate(), address.getIsMailingAddress(),
                address.getIsNewest(), address.getDelFlag());
    }

    @Override
    public void update(CifAddress address) {
        logger.info("更新地址 seqNo={}", address.getSeqNo());
        String sql = "UPDATE CIF_ADDRESS SET ADDRESS_TYPE = ?, ADDRESS_DETAIL = ?, " +
                     "LAST_CHANGE_DATE = ?, IS_MAILING_ADDRESS = ?, IS_NEWEST = ?, DEL_FLAG = ? " +
                     "WHERE SEQ_NO = ?";
        jdbcTemplate.update(sql, address.getAddressType(), address.getAddressDetail(),
                address.getLastChangeDate(), address.getIsMailingAddress(),
                address.getIsNewest(), address.getDelFlag(), address.getSeqNo());
    }

    @Override
    public void saveAll(List<CifAddress> addresses) {
        logger.info("批量保存地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        String sql = "INSERT INTO CIF_ADDRESS (SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, " +
                     "LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, addresses, addresses.size(),
                (PreparedStatement ps, CifAddress address) -> {
                    ps.setString(1, address.getSeqNo());
                    ps.setString(2, address.getClientNo());
                    ps.setString(3, address.getAddressType());
                    ps.setString(4, address.getAddressDetail());
                    ps.setTimestamp(5, address.getLastChangeDate() != null ?
                            new java.sql.Timestamp(address.getLastChangeDate().getTime()) : null);
                    ps.setString(6, address.getIsMailingAddress());
                    ps.setString(7, address.getIsNewest());
                    ps.setString(8, address.getDelFlag());
                });
    }

    @Override
    public void updateAll(List<CifAddress> addresses) {
        logger.info("批量更新地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        for (CifAddress address : addresses) {
            update(address);
        }
    }

    @Override
    public void delete(String seqNo) {
        logger.info("删除地址 seqNo={}", seqNo);
        String sql = "UPDATE CIF_ADDRESS SET DEL_FLAG = ? WHERE SEQ_NO = ?";
        jdbcTemplate.update(sql, Constants.YES, seqNo);
    }

    private static class CifAddressRowMapper implements RowMapper<CifAddress> {
        @Override
        public CifAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
            CifAddress address = new CifAddress();
            address.setSeqNo(rs.getString("SEQ_NO"));
            address.setClientNo(rs.getString("CLIENT_NO"));
            address.setAddressType(rs.getString("ADDRESS_TYPE"));
            address.setAddressDetail(rs.getString("ADDRESS_DETAIL"));
            address.setLastChangeDate(rs.getDate("LAST_CHANGE_DATE"));
            address.setIsMailingAddress(rs.getString("IS_MAILING_ADDRESS"));
            address.setIsNewest(rs.getString("IS_NEWEST"));
            address.setDelFlag(rs.getString("DEL_FLAG"));
            return address;
        }
    }
}
