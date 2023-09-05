/**
 * ファイル名：ShukkinKibouNyuuryokuRegistAction.java
 *
 * 変更履歴
 * 1.0  2010/09/04 Kazuya.Naraki
 */
package action.shk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import business.dto.LoginUserDto;
import business.dto.mth.TsukibetsuShiftDto;
import business.logic.comparator.MethodComparator;
import business.logic.mth.TsukibetsuShiftLogic;
import business.logic.shk.ShukkinKibouLogic;
import business.logic.utils.ComboListUtilLogic;
import business.logic.utils.CommonUtils;
import constant.CommonConstant;
import constant.RequestSessionNameConstant;
import form.common.DateBean;
import form.mth.TsukibetsuShiftNyuuryokuBean;
import form.mth.TsukibetsuShiftNyuuryokuForm;

/**
 * 説明：月別シフト入力登録アクションクラス
 * @author naraki
 */
public class ShukkinKibouNyuuryokuRegistAction extends ShukkinKibouAbstractAction{

    /**
     * 説明：月別シフト入力登録アクションクラス
     *
     * @param mapping アクションマッピング
     * @param form アクションフォーム
     * @param req リクエスト
     * @param res レスポンス
     * @return アクションフォワード
     * @author naraki
     */
	//新規作成 ota_naoki
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest req, HttpServletResponse res) throws Exception {

        log.info(new Throwable().getStackTrace()[0].getMethodName());

        // フォワードキー
        String forward = CommonConstant.SUCCESS;

        // セッション
        HttpSession session = req.getSession();

        // ログインユーザ情報をセッションより取得
        LoginUserDto loginUserDto = (LoginUserDto) session.getAttribute(RequestSessionNameConstant.SESSION_CMN_LOGIN_USER_INFO);

        // フォーム
        //全体の取得
        TsukibetsuShiftNyuuryokuForm tsukibetsuShiftForm = (TsukibetsuShiftNyuuryokuForm) form;

        // 画面のリスト情報
        //View経由取得情報
        List<TsukibetsuShiftNyuuryokuBean> tsukibetsuShiftBeanList = tsukibetsuShiftForm.getTsukibetsuShiftNyuuryokuBeanList();

        // 対象年月
        //修正前 当月のyyyymmを取得
        //CommonUtils.getFisicalDay(CommonConstant.yearMonthNoSl);
        String yearMonth = tsukibetsuShiftForm.getYearMonth();

        // ロジック生成
        TsukibetsuShiftLogic tsukibetsuShiftLogic = new TsukibetsuShiftLogic();
        ShukkinKibouLogic shukkinKibouLogic = new ShukkinKibouLogic();

        // 対象年月の月情報を取得する。
        List<DateBean> dateBeanList = CommonUtils.getDateBeanList(yearMonth);
        
        // フォームデータをDtoに変換する
        //List<TsukibetsuShiftDto> 形式で取得
        //1~n日の shiftIDが入ってる
        List<List<TsukibetsuShiftDto>> tsukibetsuShiftDtoList = this.formToDto(tsukibetsuShiftBeanList, dateBeanList);

        // 登録・更新処理
        //月別登録でなく希望シフト登録
        shukkinKibouLogic.registShukkinKibou(tsukibetsuShiftDtoList, loginUserDto);
        //tsukibetsuShiftLogic.registTsukibetsuShift(tsukibetsuShiftDtoList, loginUserDto);

        // シフトIDを取得する

        // セレクトボックスの取得
        ComboListUtilLogic comboListUtils = new ComboListUtilLogic();
        Map<String, String> shiftCmbMap = comboListUtils.getComboShift(true);
        Map<String, String> yearMonthCmbMap = comboListUtils.getComboYearMonth(CommonUtils.getFisicalDay(CommonConstant.yearMonthNoSl), 3, ComboListUtilLogic.KBN_YEARMONTH_NEXT, false);

		/*必要か判断出来ないので一応残しておく ota_naoki
		 * 
		 * if (CheckUtils.isEmpty(tsukibetsuShiftDtoMap)) { // データなし
		 * TsukibetsuShiftNyuuryokuBean tsukibetsuShiftBean = new
		 * TsukibetsuShiftNyuuryokuBean();
		 * tsukibetsuShiftBean.setShainId(loginUserDto.getShainId());
		 * tsukibetsuShiftBean.setShainName(loginUserDto.getShainName());
		 * tsukibetsuShiftBean.setRegistFlg(true);
		 * 
		 * tsukibetsuShiftBeanList.add(tsukibetsuShiftBean); }
		 */

        // フォームにデータをセットする
        tsukibetsuShiftForm.setShiftCmbMap(shiftCmbMap);
        tsukibetsuShiftForm.setYearMonthCmbMap(yearMonthCmbMap);
        tsukibetsuShiftForm.setTsukibetsuShiftNyuuryokuBeanList(tsukibetsuShiftBeanList);
        tsukibetsuShiftForm.setDateBeanList(dateBeanList);
        tsukibetsuShiftForm.setYearMonth(yearMonth);
        // ページング用
        tsukibetsuShiftForm.setOffset(0);
        tsukibetsuShiftForm.setCntPage(1);
        tsukibetsuShiftForm.setMaxPage(CommonUtils.getMaxPage(tsukibetsuShiftLogic.getTsukibetsuShiftDtoMap(yearMonth, true).size(), SHOW_LENGTH));

        return mapping.findForward(forward);
    }



    /**
     * DtoからBeanへ変換する
     * @param tsukibetsuShiftBeanList
     * @return List<TsukibetsuShiftDto>
     * @author naraki
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    //改修済 ota_naoki
    //戻り値が
    private List<List<TsukibetsuShiftDto>> formToDto(List<TsukibetsuShiftNyuuryokuBean> tsukibetsuShiftBeanList
                                                      , List<DateBean> dateBeanList) throws IllegalArgumentException,
                                                                        IllegalAccessException,
                                                                        InvocationTargetException {
        // 戻り値
    	List<List<TsukibetsuShiftDto>> tsukibetsuShiftDtoList = new  ArrayList<List<TsukibetsuShiftDto>>();

        for (TsukibetsuShiftNyuuryokuBean tsukibetsuShiftBean : tsukibetsuShiftBeanList) {

        	List<TsukibetsuShiftDto> tmptsukibetsuShiftDtoList = new ArrayList<TsukibetsuShiftDto>();

            // 登録フラグ
            boolean registFlg = tsukibetsuShiftBean.getRegistFlg();

            if (!registFlg) {
                continue;
            }

            // メソッドの取得
            Method[] methods = tsukibetsuShiftBean.getClass().getMethods();

            // ソートを行う
            Comparator<Method> asc = new MethodComparator();
            Arrays.sort(methods, asc); // 配列をソート

            int listSize = dateBeanList.size();

            int index = 0;

            for (int i = 0; i < methods.length; i++) {
                // "getShiftIdXX" のメソッドを動的に実行する
                if (methods[i].getName().startsWith("getShiftId") && index < listSize) {
                    String yearMonthDay = "";

                    // 対象年月取得
                    yearMonthDay = dateBeanList.get(index).getYearMonthDay();

                    TsukibetsuShiftDto tsukibetsuShiftDto = new TsukibetsuShiftDto();
                    String shiftId = (String) methods[i].invoke(tsukibetsuShiftBean);

                    if (CommonConstant.BLANK_ID.equals(shiftId)) {
                        // 空白が選択されている場合
                        shiftId = null;
                    }

                    tsukibetsuShiftDto.setShiftId(shiftId);
                    tsukibetsuShiftDto.setShainId(tsukibetsuShiftBean.getShainId());
                    tsukibetsuShiftDto.setYearMonthDay(yearMonthDay);
                    tmptsukibetsuShiftDtoList.add(tsukibetsuShiftDto);

                    index++;
                }
            }
            tsukibetsuShiftDtoList.add(tmptsukibetsuShiftDtoList);
        }

        return tsukibetsuShiftDtoList;
    }
}
