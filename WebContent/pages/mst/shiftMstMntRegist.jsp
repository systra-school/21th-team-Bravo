<!-- shiftMstMntRegist.jsp -->
<%@page import="business.logic.utils.CheckUtils"%>
<%@page import="form.mst.ShiftMstMntBean"%>
<%@page import="java.util.List"%>
<%@page import="form.mst.ShiftMstMntForm"%>
<%
/**
 * ファイル名：shiftMstMntRegist.jsp
 *
 * 変更履歴
 * 1.0  2010/09/13 Kazuya.Naraki
 */
%>
<%@page contentType="text/html; charset=Shift_JIS" pageEncoding="Shift_JIS"%>
<%@ page import="constant.RequestSessionNameConstant"%>
<%@ page import="constant.CommonConstant"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<html>
  <head>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
    <!-- 修正 oota_naoki
    	javascriptのパスを正規なものに変更
    	kikin → kikin_test
      -->
    <script type="text/javascript" src="/kikin_test/pages/js/common.js"></script>
    <script type="text/javascript" src="/kikin_test/pages/js/checkCommon.js"></script>
    <script type="text/javascript" src="/kikin_test/pages/js/message.js"></script>
    <script type="text/javascript" language="Javascript1.1">
    /**
     * 登録処理を行う
     */
    function shiftMstMntRegist() {

        // 開始時間エラーメッセージ
        var startTimeErrMsg = '';
        // 終了時間エラーメッセージ
        var endTimeErrMsg = '';
        // 休憩時間エラーメッセージ
        var breakTimeErrMsg = '';
        // エラーメッセージ
        var errorMsg = '';
        // From - To エラーメッセージ
        var fromToErrMsg = '';

        // 時間チェック
        with (document.forms[0]) {
          // 開始時間を取得する。
          var varStartTime = startTime.value;
          // 終了時間を取得する。
          var varEndTime = endTime.value;
          // 休憩時間を取得する。
          var barBreakTime = breakTime.value;

          // 背景色をクリアする
          startTime.style.backgroundColor = 'white';
          endTime.style.backgroundColor = 'white';
          breakTime.style.backgroundColor = 'white';	

          if (!checkTime(varStartTime)) {
              var strArr = ['開始時間'];
              startTimeErrMsg = getMessage('E-MSG-000004', strArr);
              startTime.style.backgroundColor = 'red';
          }

          if (!checkTime(varEndTime)) {
              var strArr = ['終了時間'];
              endTimeErrMsg = getMessage('E-MSG-000004', strArr);
              endTime.style.backgroundColor = 'red';
          }

          if (!checkTime(barBreakTime)) {
              var strArr = ['休憩時間'];
              breakTimeErrMsg = getMessage('E-MSG-000004', strArr);
              breakTime.style.backgroundColor = 'red';
          }

          // from - to のチェック
          if (!checkTimeCompare(varStartTime, varEndTime)) {
        	  //checkTimeの参照先が違っていたのを修正　清田
            if (checkTime(varStartTime) && checkTime(varEndTime)) {
                fromToErrMsg = getMessageCodeOnly('E-MSG-000005');
                startTime.style.backgroundColor = 'red';
                endTime.style.backgroundColor = 'red';
            }
          }
        }

        // エラーメッセージ
        errorMsg = startTimeErrMsg + endTimeErrMsg + breakTimeErrMsg + fromToErrMsg;

        if (errorMsg) {
            alert(errorMsg);
            // エラー
            return false;
        }

        document.forms[0].submit();
    }

    </script>

    <title>シフトマスタメンテナンス画面</title>

    <link href="/kikin_test/pages/css/common.css" rel="stylesheet" type="text/css" />
  </head>
  <body>
    <div id="wrapper">
      <div id="header">
        <table>
          <tr>
            <td id="headLeft">
              <input value="戻る" type="button" class="smlButton"  onclick="doSubmit('/kikin_test/shiftMstMntRegistBack.do')" />
            </td>
            <td id="headCenter">
              シフトマスタメンテナンス（新規登録）
            </td>
            <td id="headRight">
              <input value="ログアウト" type="button" class="smlButton"  onclick="logout()" />
            </td>
          </tr>
        </table>
      </div>
      <div id="gymBody">
        <html:form action="/shiftMstMntRegist">
          <div style="width: 630px; margin-left:300px;">
            <table class="tblHeader" border="1" cellpadding="0" cellspacing="0">
              <tr>
                <td width="120px" align="center">
                  シフト名
                </td>
                <td width="70px" align="center">
                  シンボル
                </td>
                <td width="230px" align="center">
                  時間
                </td>
                <td width="100px" align="center">
                  休憩
                </td>
              </tr>
            </table>
          </div>
          <div style="overflow: auto; height: 400px; width: 630px; margin-left:300px;">
            <table class="tblBody" border="1" cellpadding="0" cellspacing="0">
              <tr>
                <td width="120px"  align="center">
                  <html:text property="shiftName" size="10" maxlength="10" />
                </td>
                <td width="70px"  align="center">
                  <html:text property="symbol" size="2" maxlength="2" />
                </td>
                <td width="230px"  align="center">
                  <table width="100%" >
                    <tr>
                      <td align="center">
                        <html:text property="startTime" size="5" maxlength="5" />
                      </td>
                      <td align="center">
                          &#xFF5E;
                      </td>
                      <td align="center">
                        <html:text property="endTime" size="5" maxlength="5" />
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="100px"  align="center">
                  <html:text property="breakTime" size="5" maxlength="5" />
                </td>
              </tr>
            </table>
          </div>
        </html:form>
      </div>
      <div id="footer">
        <table>
          <tr>
            <td id="footLeft">
              　
            </td>
            <td id="footCenter">
              　
            </td>
            <td id="footRight">
                <input value="登録" type="button" class="smlButton"  onclick="shiftMstMntRegist()" />
            </td>
          </tr>
        </table>
      </div>
    </div>
  </body>
</html>