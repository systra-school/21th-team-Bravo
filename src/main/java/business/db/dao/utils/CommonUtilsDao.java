/**
 * ファイル名：CommonUtilsDao.java
 *
 * 変更履歴
 * 1.0  2010/09/06 Kazuya.Naraki
 */
package business.db.dao.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import constant.DbConstant.Mshukujitsu;


import business.db.dao.AbstractDao;

/**
 * 説明：共通部品用Ｄａｏ
 * @author naraki
 *
 */
public class CommonUtilsDao extends AbstractDao {

    // ログ出力クラス
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * 各テーブルのＩＤの最大を取得する。
     *
     * @param tableName 対象テーブル名
     * @param idName 対象テーブルのIDのカラム名
     * @return 社員マスタ検索Ｄｔｏ
     * @author Kazuya.Naraki
     */
    public String getMaxId(String tableName, String idName) throws SQLException {

        String rtnId = "";

        try {
            // コネクション接続
            this.connect();

            StringBuffer strSql = new StringBuffer();
            strSql.append("SELECT MAX(" + idName + ") AS NEXT_ID FROM " + tableName );

            PreparedStatement ps = connection.prepareStatement(strSql.toString());

            // ログ出力
            log.info(ps);

            // 実行
            ResultSet rs = ps.executeQuery();

            // 取得結果セット
            if (rs.next()) {
                rtnId = rs.getString("NEXT_ID");
            } else {
                return null;
            }
        } catch (SQLException e) {
            // 例外発生
            throw e;
        } finally {
            // コネクション切断
            disConnect();
        }
        return rtnId;
    }

    /**
     * 対象年月の祝日のリストを取得する
     * @param 対象年月
     * @return 祝日のリスト
     * @author naraki
     */
    public List<String> getShukujitsuList(String yearMonth) throws SQLException {

        List<String> rtnList =new ArrayList<String>();

        try {
            // コネクション接続
            this.connect();

            StringBuffer strSql = new StringBuffer();
            strSql.append("SELECT ");
            strSql.append(Mshukujitsu.YEAR_MONTH_DAY.getName());
            strSql.append(" FROM ");
            strSql.append(Mshukujitsu.TABLE_NAME.getName());
            strSql.append(" WHERE SUBSTRING(");
            strSql.append(Mshukujitsu.YEAR_MONTH_DAY.getName());
            strSql.append(", 1, 6)");

            PreparedStatement ps = connection.prepareStatement(strSql.toString());

            // ログ出力
            log.info(ps);

            // 実行
            ResultSet rs = ps.executeQuery();

            // 取得結果セット
            if (rs.next()) {
                rtnList.add(rs.getString(Mshukujitsu.YEAR_MONTH_DAY.getName()));
            } else {
                return null;
            }
        } catch (SQLException e) {
            // 例外発生
            throw e;
        } finally {
            // コネクション切断
            disConnect();
        }
        return rtnList;
    }
}
