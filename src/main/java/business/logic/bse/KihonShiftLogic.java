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
import business.db.dao.mth.TsukibetsuShiftDao;
import business.dto.LoginUserDto;
import business.dto.bse.KihonShiftDto;
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
    
    /*@param tsukibetsuShiftDtoMap 
     *  JSPページで表示するデータ / return
     * 
     * @param startWeek 
     * 月初めの曜日を取得/ 0...日曜日  6...土曜日 
     * 
     * @param ShiftUtilWeekArrays
     * ShiftUtilWeekArrays[A][B]
     * [A]...従業員の配列
     * [B]...日曜日～土曜日 までの配列
     * 
     * @param tsukibetsuShiftDtoList
     * tsukibetsuShiftDtoMapのList<TsukibetsuShiftDto>に格納するList
     * 
     * @param oldShainId
     * tsukibetsuShiftDtoMap のString に格納するObject
     * dto.getShainId() から取得する
     * 
     * @return tsukibetsuShiftDtoMap
     * @author ota_naoki
     * */
    public Map<String, List<TsukibetsuShiftDto>> KihonShiftDtoMap(String yearMonth, boolean shiftFlg)
			throws SQLException {

		Map<String, List<TsukibetsuShiftDto>> tsukibetsuShiftDtoMap = new LinkedHashMap<String, List<TsukibetsuShiftDto>>();
		String[][] ShiftUtilWeekArrays = getShiftUtilWeekArrays();
		String oldShainId = "";
		int startWeek = getStartWeek(yearMonth);
	    
		int cnt = 0;
		int week = startWeek;
		
		TsukibetsuShiftDao dao = new TsukibetsuShiftDao();
		List<TsukibetsuShiftDto> tsukibetsuShiftDtoList = dao.getShiftTblData(yearMonth, shiftFlg);

		// 一時領域
		List<TsukibetsuShiftDto> tmpList = new ArrayList<TsukibetsuShiftDto>();

		for (TsukibetsuShiftDto dto : tsukibetsuShiftDtoList) {
			if (CheckUtils.isEmpty(oldShainId)) {
				// 社員IDが空のとき（初回）
				week = startWeek;
				oldShainId = dto.getShainId();
			} else {
				if (!(oldShainId.equals(dto.getShainId()))) {
					// 別社員のデータのとき
					// 別社員時の処理
					cnt++;
					week = startWeek;
					oldShainId = dto.getShainId();
					tmpList = new ArrayList<TsukibetsuShiftDto>();
					tsukibetsuShiftDtoMap.put(oldShainId, tmpList);
				}
			}
			dto.setShiftId(ShiftUtilWeekArrays[cnt][week]);
			tmpList.add(dto);
			
			//週のループ
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
    
    /* 月初めの曜日を取得
     * 
     * @return startWeek;
     * @author ota_naoki
     * */
    public int getStartWeek(String yearMonth) {
    	int startWeek = -1;
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
        return startWeek;
    }
   
    /* ShiftUtilWeekArrays の配列を返す。
     * 
     * ShiftUtilWeekArrays[A][B]
     * [A]...従業員の配列
     * [B]...日曜日～土曜日 までの配列
     * 
     * @return ShiftUtilWeekArrays
     * @author ota_naoki
     * */
    
    public String[][] getShiftUtilWeekArrays() throws SQLException{
    	//Daoオブジェクト生成
    	KihonShiftDao kihonDao = new KihonShiftDao();
		// 基本シフトのDto(一週間分)
		Map<String, KihonShiftDto> kihonShiftDtoList = kihonDao.getKihonShiftDataList();

		// 基本シフト(m_base_shift)の配列作成。週は定数(七日)
		// 配列の長さを取得
		int  arraylength = kihonShiftDtoList.values().size();
		// index[n][0]~[n][6] ... 月曜日～日曜日までの基本シフト
		String[][] ShiftUtilWeekArrays = new String[arraylength][7];
		// カウントの初期化
		int  cnt = 0;
		// 社員ごとの基本シフトの配列化
		for (KihonShiftDto tmpKihonShift : kihonShiftDtoList.values()) {
			ShiftUtilWeekArrays[cnt][0] = tmpKihonShift.getShiftIdOnSunday();
			ShiftUtilWeekArrays[cnt][1] = tmpKihonShift.getShiftIdOnMonday();
			ShiftUtilWeekArrays[cnt][2] = tmpKihonShift.getShiftIdOnTuesday();
			ShiftUtilWeekArrays[cnt][3] = tmpKihonShift.getShiftIdOnWednesday();
			ShiftUtilWeekArrays[cnt][4] = tmpKihonShift.getShiftIdOnThursday();
			ShiftUtilWeekArrays[cnt][5] = tmpKihonShift.getShiftIdOnFriday();
			ShiftUtilWeekArrays[cnt][6] = tmpKihonShift.getShiftIdOnSaturday();

			cnt++;
		}
		return ShiftUtilWeekArrays;
    }
}
