package com.address.repository;

import com.address.config.DbConfig;
import com.address.constants.Constants;
import com.address.model.CifAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcClientAddressRepository implements ClientAddressRepository {

    static {
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            DbConfig.getUrl(),
            DbConfig.getUsername(),
            DbConfig.getPassword()
        );
    }

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        List<CifAddress> result = new ArrayList<>();
        String sql = "SELECT * FROM cif_address WHERE client_no = ? AND del_flag = ?";
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
    public void save(CifAddress address) {
        String sql = "INSERT INTO cif_address (seq_no, client_no, address_type, address_detail, " +
                     "last_change_date, is_mailing_address, is_newest, del_flag) " +
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
        String sql = "UPDATE cif_address SET address_type = ?, address_detail = ?, " +
                     "last_change_date = ?, is_mailing_address = ?, is_newest = ?, del_flag = ? " +
                     "WHERE seq_no = ?";
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
        String sql = "INSERT INTO cif_address (seq_no, client_no, address_type, address_detail, " +
                     "last_change_date, is_mailing_address, is_newest, del_flag) " +
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
        for (CifAddress address : addresses) {
            update(address);
        }
    }

    @Override
    public void delete(String seqNo) {
        String sql = "UPDATE cif_address SET del_flag = ? WHERE seq_no = ?";
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
        address.setSeqNo(rs.getString("seq_no"));
        address.setClientNo(rs.getString("client_no"));
        address.setAddressType(rs.getString("address_type"));
        address.setAddressDetail(rs.getString("address_detail"));
        Timestamp ts = rs.getTimestamp("last_change_date");
        if (ts != null) {
            address.setLastChangeDate(new Date(ts.getTime()));
        }
        address.setIsMailingAddress(rs.getString("is_mailing_address"));
        address.setIsNewest(rs.getString("is_newest"));
        address.setDelFlag(rs.getString("del_flag"));
        return address;
    }

    private static void createTableIfNotExists() {
        String checkSql = "SELECT COUNT(*) FROM information_schema.tables " +
                          "WHERE table_schema = DATABASE() AND table_name = 'cif_address'";
        String createSql = "CREATE TABLE IF NOT EXISTS cif_address (" +
                "seq_no VARCHAR(64) PRIMARY KEY," +
                "client_no VARCHAR(32) NOT NULL," +
                "address_type VARCHAR(2) NOT NULL," +
                "address_detail VARCHAR(256) NOT NULL," +
                "last_change_date DATETIME," +
                "is_mailing_address CHAR(1) DEFAULT 'N'," +
                "is_newest CHAR(1) DEFAULT 'N'," +
                "del_flag CHAR(1) DEFAULT 'N'," +
                "INDEX idx_client_no (client_no)," +
                "INDEX idx_client_type (client_no, address_type)" +
                ")";

        try (Connection conn = DriverManager.getConnection(
                DbConfig.getUrl(), DbConfig.getUsername(), DbConfig.getPassword())) {

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
