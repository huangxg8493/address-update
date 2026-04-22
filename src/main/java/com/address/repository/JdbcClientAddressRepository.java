package com.address.repository;

import com.address.constants.Constants;
import com.address.model.CifAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcClientAddressRepository implements ClientAddressRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcClientAddressRepository.class);

    private final DataSource dataSource;

    public JdbcClientAddressRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        List<CifAddress> result = new ArrayList<>();
        String sql = "SELECT SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG FROM CIF_ADDRESS WHERE CLIENT_NO = ? AND DEL_FLAG = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clientNo);
            ps.setString(2, Constants.NO);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询地址失败", e);
        }
        return result;
    }

    @Override
    public CifAddress findBySeqNo(String seqNo, String clientNo) {
        String sql = "SELECT SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG FROM CIF_ADDRESS WHERE SEQ_NO = ? AND CLIENT_NO = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seqNo);
            ps.setString(2, clientNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询地址失败", e);
        }
        return null;
    }

    @Override
    public void save(CifAddress address) {
        logger.info("保存地址 clientNo={}", address.getClientNo());
        String sql = "INSERT INTO CIF_ADDRESS (SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, " +
                     "LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, address);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("保存地址失败", e);
        }
    }

    @Override
    public void update(CifAddress address) {
        logger.info("更新地址 seqNo={}", address.getSeqNo());
        String sql = "UPDATE CIF_ADDRESS SET ADDRESS_TYPE = ?, ADDRESS_DETAIL = ?, " +
                     "LAST_CHANGE_DATE = ?, IS_MAILING_ADDRESS = ?, IS_NEWEST = ?, DEL_FLAG = ? " +
                     "WHERE SEQ_NO = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, address.getAddressType());
            ps.setString(2, address.getAddressDetail());
            ps.setTimestamp(3, address.getLastChangeDate() != null ?
                    new Timestamp(address.getLastChangeDate().getTime()) : null);
            ps.setString(4, address.getIsMailingAddress());
            ps.setString(5, address.getIsNewest());
            ps.setString(6, address.getDelFlag());
            ps.setString(7, address.getSeqNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新地址失败", e);
        }
    }

    @Override
    public void saveAll(List<CifAddress> addresses) {
        logger.info("批量保存地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        String sql = "INSERT INTO CIF_ADDRESS (SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, " +
                     "LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CifAddress address : addresses) {
                setParams(ps, address);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("批量保存地址失败", e);
        }
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
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Constants.YES);
            ps.setString(2, seqNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除地址失败", e);
        }
    }

    private void setParams(PreparedStatement ps, CifAddress address) throws SQLException {
        ps.setString(1, address.getSeqNo());
        ps.setString(2, address.getClientNo());
        ps.setString(3, address.getAddressType());
        ps.setString(4, address.getAddressDetail());
        ps.setTimestamp(5, address.getLastChangeDate() != null ?
                new Timestamp(address.getLastChangeDate().getTime()) : null);
        ps.setString(6, address.getIsMailingAddress());
        ps.setString(7, address.getIsNewest());
        ps.setString(8, address.getDelFlag());
    }

    private CifAddress mapRow(ResultSet rs) throws SQLException {
        CifAddress address = new CifAddress();
        address.setSeqNo(rs.getString("SEQ_NO"));
        address.setClientNo(rs.getString("CLIENT_NO"));
        address.setAddressType(rs.getString("ADDRESS_TYPE"));
        address.setAddressDetail(rs.getString("ADDRESS_DETAIL"));
        Timestamp ts = rs.getTimestamp("LAST_CHANGE_DATE");
        if (ts != null) {
            address.setLastChangeDate(new Date(ts.getTime()));
        }
        address.setIsMailingAddress(rs.getString("IS_MAILING_ADDRESS"));
        address.setIsNewest(rs.getString("IS_NEWEST"));
        address.setDelFlag(rs.getString("DEL_FLAG"));
        return address;
    }

    private void createTableIfNotExists() {
        String checkSql = "SELECT COUNT(*) FROM information_schema.tables " +
                          "WHERE table_schema = DATABASE() AND table_name = 'CIF_ADDRESS'";
        String createSql = "CREATE TABLE IF NOT EXISTS CIF_ADDRESS (" +
                "SEQ_NO VARCHAR(64) PRIMARY KEY," +
                "CLIENT_NO VARCHAR(32) NOT NULL," +
                "ADDRESS_TYPE VARCHAR(2) NOT NULL," +
                "ADDRESS_DETAIL VARCHAR(256) NOT NULL," +
                "LAST_CHANGE_DATE DATETIME," +
                "IS_MAILING_ADDRESS CHAR(1) DEFAULT 'N'," +
                "IS_NEWEST CHAR(1) DEFAULT 'N'," +
                "DEL_FLAG CHAR(1) DEFAULT 'N'," +
                "INDEX idx_client_no (CLIENT_NO)," +
                "INDEX idx_client_type (CLIENT_NO, ADDRESS_TYPE)" +
                ")";

        try (Connection conn = dataSource.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(createSql);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("初始化数据库表失败", e);
        }
    }
}
