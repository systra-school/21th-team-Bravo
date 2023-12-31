/**
 * ファイル名：ShukkinKibouKakuninPageAction.java
 *
 * 変更履歴
 * 1.0  2010/09/04 Kazuya.Naraki
 */
package action.day;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import business.dto.day.HibetsuShiftDto;
import business.logic.day.HibetsuShiftLogic;
import business.logic.utils.CommonUtils;
import constant.CommonConstant;
import form.day.HibetsuShiftForm;

/**
 * 説明：日別シフト確認画面ページ変更アクションクラス
 * @author naraki
 *
 */
public class HibetsuShiftPageAction extends HibetsuShiftAbstractAction {

    // ログ出力クラス
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * 説明：日別シフト確認ページ表示アクションクラス
     *
     * @param mapping アクションマッピング
     * @param form アクションフォーム
     * @param req リクエスト
     * @param res レスポンス
     * @return アクションフォワード
     * @author naraki
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest req, HttpServletResponse res) throws Exception {

        log.info(new Throwable().getStackTrace()[0].getMethodName());

        // フォワードキー
        String forward = CommonConstant.SUCCESS;

        // フォーム
        HibetsuShiftForm hibetsuShiftForm = (HibetsuShiftForm) form;

        // 表示年月
        String yearMonthDay = hibetsuShiftForm.getYearMonthDay();

        // ページング
        String paging = hibetsuShiftForm.getPaging();

        List<HibetsuShiftDto> hibetsuShiftDtoList;

        // ロジック生成
        HibetsuShiftLogic hibetsuShiftLogic = new HibetsuShiftLogic();

        if (CommonConstant.NEXT.equals(paging)) {
            // 次ページ
            yearMonthDay = CommonUtils.add(yearMonthDay, 0, 0, 1);

            // システム日付のシフトデータを取得する
             hibetsuShiftDtoList = hibetsuShiftLogic.getHibetsuShiftDtoList(yearMonthDay);

        } else {
            // 前ページ
            yearMonthDay = CommonUtils.add(yearMonthDay, 0, 0, -1);

            // システム日付のシフトデータを取得する
            hibetsuShiftDtoList = hibetsuShiftLogic.getHibetsuShiftDtoList(yearMonthDay);
        }

//        if (CheckUtils.isEmpty(hibetsuShiftDtoList)) {
//            forward = CommonConstant.NODATA;
//        }

        // フォームへ一覧をセットする
        hibetsuShiftForm.setHibetsuShiftBeanList(dtoToForm(hibetsuShiftDtoList));
        hibetsuShiftForm.setYearMonthDay(yearMonthDay);
        hibetsuShiftForm.setYearMonthDayDisp(CommonUtils.changeFormat(yearMonthDay, CommonConstant.yearMonthDayNoSl, CommonConstant.yearMonthDay));

        return mapping.findForward(forward);
    }

}
