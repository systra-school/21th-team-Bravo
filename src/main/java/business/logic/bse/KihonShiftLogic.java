/**
 * ファイル名：KinmuJissekiLogic.java
 *
 * 変更履歴
 * 1.0  2010/11/04 Kazuya.Naraki
 */
package business.logic.bse;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import business.db.dao.bse.KihonShiftDao;
import business.db.dao.mst.ShiftMstMntDao;
import business.db.dao.mth.TsukibetsuShiftDao;
import business.dto.LoginUserDto;
import business.dto.bse.KihonShiftDto;
import business.dto.mst.ShiftMstMntDto;
import business.dto.mth.TsukibetsuShiftDto;
import business.logic.utils.CheckUtils;
import exception.CommonException;

/**
 * 説明：ログイン処理のロジック
 * @author nishioka
 *
 */
public class KihonShiftLogic {

    /**
     * シフト、基本シフトのデータを取得する
     *
     * @param shainId 社員ID
     * @param yearMonth 対象年月
     * @return 勤務実績マップ
     * @author Kazuya.Naraki
     */
    public Map<String, KihonShiftDto> getKihonShiftData() throws SQLException, CommonException {

        KihonShiftDao kihonShiftDao = new KihonShiftDao();

        // 基本シフトデータを取得する
        Map<String, KihonShiftDto> kihonShiftMap = kihonShiftDao.getKihonShiftDataList();

        return kihonShiftMap;
    }

    /**
     * 勤務実績データの登録を行う
     *
     * @param shainId 社員ID
     * @param yearMonth 対象年月
     * @return 勤務実績マップ
     * @author Kazuya.Naraki
     * @throws Exception
     */
    public void registKihonShift(List<KihonShiftDto> kinmuJissekiDtoList, LoginUserDto loginUserDto) throws Exception {

        // 基本シフトDao
        KihonShiftDao kihonShiftDao = new KihonShiftDao();
        // コネクション
        Connection connection = kihonShiftDao.getConnection();

        // トランザクション処理
        connection.setAutoCommit(false);

        try {
            for (int i = 0; i < kinmuJissekiDtoList.size(); i++) {

            	KihonShiftDto kihonShiftDto = kinmuJissekiDtoList.get(i);
                String shainId = kihonShiftDto.getShainId();

                // データが存在するか確認
                boolean updateFlg = kihonShiftDao.isData(shainId);

                if (updateFlg) {
                    // 更新
                    kihonShiftDao.updateKinmuJisseki(kihonShiftDto, loginUserDto.getShainId());
                } else {
                    // 登録
                    kihonShiftDao.insertKihonShift(kihonShiftDto, loginUserDto.getShainId());
                }

            }
        } catch (Exception e) {
            // ロールバック処理
            connection.rollback();

            // 切断
            connection.close();

            throw e;
        }
        // コミット
        connection.commit();
        // 切断
        connection.close();

    }
    
    public Map<String, List<TsukibetsuShiftDto>> KihonShiftDtoMap(String yearMonth, boolean shiftFlg)
			throws SQLException {

		// 戻り値
		Map<String, List<TsukibetsuShiftDto>> tsukibetsuShiftDtoMap = new LinkedHashMap<String, List<TsukibetsuShiftDto>>();
		
		int startWeek = 0;
		int year = Integer.parseInt(yearMonth.substring(0, 4));
		int month = Integer.parseInt(yearMonth.substring(4, 6));
		
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // 8月（0から始まるインデックス）
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                startWeek = 0;
                break;
            case Calendar.MONDAY:
                startWeek = 1;
                break;
            case Calendar.TUESDAY:
                startWeek = 2;
                break;
            case Calendar.WEDNESDAY:
                startWeek = 3;
                break;
            case Calendar.THURSDAY:
                startWeek = 4;
                break;
            case Calendar.FRIDAY:
                startWeek = 5;
                break;
            case Calendar.SATURDAY:
                startWeek = 6;
                break;
        }
	    
		
		// Dao
		TsukibetsuShiftDao dao = new TsukibetsuShiftDao();
		KihonShiftDao kihonDao = new KihonShiftDao();
		ShiftMstMntDao shiftMstDao = new ShiftMstMntDao();

		// シフト情報を取得する。
		List<TsukibetsuShiftDto> tsukibetsuShiftDtoList = dao.getShiftTblData(yearMonth, shiftFlg);
		// 基本シフトのDto(一週間分)
		Map<String, KihonShiftDto> kihonShiftDtoList = kihonDao.getKihonShiftDataList();
		// シフトマスタのDto
		List<ShiftMstMntDto> ShiftMstDtoList = shiftMstDao.getAllList();

		// シフトマスタ(m_shift)の配列作成
		// 配列の長さを取得
		int arraylength = ShiftMstDtoList.size();
		// index[n][0]...ShiftId index[n][1]...symbol
		String[][] ShiftMstMntArrays = new String[arraylength][2];
		// カウント変数の作成
		int cnt = 0;
		for (ShiftMstMntDto tmpShiftMst : ShiftMstDtoList) {
			ShiftMstMntArrays[cnt][0] = tmpShiftMst.getShiftId();
			ShiftMstMntArrays[cnt][1] = tmpShiftMst.getSymbol();

			cnt++;
		}

		// 基本シフト(m_base_shift)の配列作成。週は定数(七日)
		// 配列の長さを取得
		arraylength = kihonShiftDtoList.values().size();
		// index[n][0]~[n][6] ... 月曜日～日曜日までの基本シフト
		String[][] shiftmstArrays = new String[arraylength][7];
		// カウントの初期化
		cnt = 0;
		// 社員ごとの基本シフトの配列化
		for (KihonShiftDto tmpKihonShift : kihonShiftDtoList.values()) {
			shiftmstArrays[cnt][1] = tmpKihonShift.getShiftIdOnMonday();
			shiftmstArrays[cnt][2] = tmpKihonShift.getShiftIdOnTuesday();
			shiftmstArrays[cnt][3] = tmpKihonShift.getShiftIdOnWednesday();
			shiftmstArrays[cnt][4] = tmpKihonShift.getShiftIdOnThursday();
			shiftmstArrays[cnt][5] = tmpKihonShift.getShiftIdOnFriday();
			shiftmstArrays[cnt][6] = tmpKihonShift.getShiftIdOnSaturday();
			shiftmstArrays[cnt][0] = tmpKihonShift.getShiftIdOnSunday();

			cnt++;
		}

		String oldShainId = "";

		// 一時領域
		List<TsukibetsuShiftDto> tmpList = new ArrayList<TsukibetsuShiftDto>();

		// DB取得より取得する値を各社員づつ区切る
		cnt = 0;
		int week = startWeek;
		
		for (TsukibetsuShiftDto dto : tsukibetsuShiftDtoList) {
			if (CheckUtils.isEmpty(oldShainId)) {
				// 社員IDが空のとき（初回）
				week = startWeek;
				oldShainId = dto.getShainId();

			} else {
				if (!(oldShainId.equals(dto.getShainId()))) {
					// 別社員のデータのとき
					// 前の社員分をマップにつめる
					week = startWeek;
					cnt++;
					tsukibetsuShiftDtoMap.put(oldShainId, tmpList);

					// oldShainId を入れ替える
					oldShainId = dto.getShainId();

					// 新しい社員のデータを追加していく
					tmpList = new ArrayList<TsukibetsuShiftDto>();

				}
				// KihonShiftはここで shiftid symbol(Viewで表示されるアイコン（平、黒）) shiftName(SH000X) を書き換える
				
			}
			dto.setShiftId(shiftmstArrays[cnt][week]);
			//swich
			for(String[] symbol : ShiftMstMntArrays) {
				if(symbol[0].equals(shiftmstArrays[cnt][week])) {
					dto.setSymbol(symbol[1]);
					break;
				}
			}
			dto.setSymbol(shiftmstArrays[cnt][1]);
			tmpList.add(dto);
			
			if (week != 6) {
				week++;
			} else {
				week = 0;
			}
		}

		if (!CheckUtils.isEmpty(oldShainId)) {
			// 最後分を追加する
			tsukibetsuShiftDtoMap.put(oldShainId, tmpList);
		}

		return tsukibetsuShiftDtoMap;
	}
}
