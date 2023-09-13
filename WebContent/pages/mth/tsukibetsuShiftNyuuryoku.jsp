<!-- tsukibetsuShiftNyuuryoku.jsp -->
<%@page import="constant.CommonConstant.DayOfWeek"%>
<%@page import="business.logic.utils.CheckUtils"%>
<%@page import="form.common.DateBean"%>
<%@page import="java.util.List"%>
<%@page import="form.mth.TsukibetsuShiftNyuuryokuForm"%>
<%
/**
 * ファイル名：tsukibetsuShiftNyuuryoku.jsp
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

<bean:size id="dateBeanListSize" name="tsukibetsuShiftNyuuryokuForm" property="dateBeanList"/>
<bean:size id="listSize" name="tsukibetsuShiftNyuuryokuForm" property="tsukibetsuShiftNyuuryokuBeanList"/>
<bean:define id="showLength" value="16" type="java.lang.String"/>
<bean:define id="offset" name="tsukibetsuShiftNyuuryokuForm" property="offset" />
<bean:define id="color" value="" type="java.lang.String"/>
<bean:define id="cntPage" name="tsukibetsuShiftNyuuryokuForm" property="cntPage" type="java.lang.Integer"/>
<bean:define id="maxPage" name="tsukibetsuShiftNyuuryokuForm" property="maxPage" type="java.lang.Integer"/>

<%
// 高さ指定変更
final int heightSize = 26;

int intShowLength = Integer.parseInt(showLength);

//表示しているリストサイズの調整
if (cntPage.intValue() == maxPage.intValue()) {
 listSize = listSize % intShowLength;
}

if (listSize > intShowLength) {
 listSize = intShowLength;
}

%>
<html>
  <head>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
    <script type="text/javascript" src="/kikin_test/pages/js/common.js"></script>
    <script type="text/javascript" src="/kikin_test/pages/js/checkCommon.js"></script>
    <script type="text/javascript" src="/kikin_test/pages/js/message.js"></script>
    <script type="text/javascript" language="Javascript1.1">
    
    /* ☆メソッドがコメントアウトされてたのでコメントアウトを削除 */
    /**
     * 出勤希望反映
     */
    function submitShukkinKibou() {
        // サブミット
        doSubmit('/kikin_test/tsukibetsuShiftNyuuryokuShukkinKibou.do');
    }
    
    function submitKihonShift() {
		doSubmit('/kikin_test/tsukibetuShiftNyuuryokuKihonShift.do');
	}

    /**
     * 登録
     */
    function submitRegist() {
        // サブミット
        doSubmit('/kikin_test/tsukibetsuShiftNyuuryokuRegist.do');
    }

    /**
     * 検索
     */
    function submitSearch() {
        doSubmit('/kikin_test/tsukibetsuShiftNyuuryokuSearch.do');
    }

    /**
     * サブウィンドウを開く
     */
    function openWindow(){
        window.open("/kikin_test/shiftHanrei.do?param=", null, "menubar=no, toolbar=no, scrollbars=auto, resizable=yes, width=520px, height=650px");
    }
    
    </script>
    <title>月別シフト入力画面</title>

    <link href="/kikin_test/pages/css/common.css" rel="stylesheet" type="text/css" />
      <style type="text/css">
		.lngButton {
			display: initial;
		}

	  </style>
  </head>
  <body>
    <div id="wrapper">
      <div id="header">
        <table>
          <tr>
            <td id="headLeft">
              <!-- ★しのとく★　戻るボタンで画面遷移完了 -->
            	<!--<html:form action="/tsukibetsuShiftNyuuryokuBack">
              		<input type="submit" value="戻る" class="smlButton" />
            	</html:form>-->
              <input value="戻る" type="button" class="smlButton"
						onclick="doSubmit('/kikin_test/tsukibetsuShiftNyuuryokuBack.do')" />
            </td>
            <td id="headCenter">
              月別シフト入力
            </td>
            <td id="headRight">
              <input value="ログアウト" type="button" class="smlButton"  onclick="logout()" />
            </td>
          </tr>
        </table>
      </div>
      <%-- 表のズレ調整 --%>
      <div id="gymBody" style="overflow: auto;">
        <html:form action="/tsukibetsuShiftNyuuryokuPage" >
       	 <%-- 表のズレ調整 --%>
          <div style="margin-left:80px;">
            <div style="height: 25px;">
              表示年月：
              <bean:define id="sessionYearMonth" name="tsukibetsuShiftNyuuryokuForm" property="yearMonth" type="String"/>
              <html:select property="yearMonth" name="tsukibetsuShiftNyuuryokuForm"  onchange="submitSearch()">
              <html:optionsCollection name="tsukibetsuShiftNyuuryokuForm"
                                      property="yearMonthCmbMap"
                                      value="key"
                                      label="value"/>
              </html:select>
              <html:link href="/kikin_test/tsukibetsuShiftNyuuryokuPage.do?paging=back">前へ</html:link>
              <html:link href="/kikin_test/tsukibetsuShiftNyuuryokuPage.do?paging=next">次へ</html:link>
              <bean:write name="tsukibetsuShiftNyuuryokuForm" property="cntPage"/>/
              <bean:write name="tsukibetsuShiftNyuuryokuForm" property="maxPage"/>
            </div>
            <table width="1100px" cellpadding="0" cellspacing="0">
              <tr>
                <td width="150px" valign="top">
                  <table class="tblHeader" border="1" cellpadding="0" cellspacing="0">
                    <tr height="<%=heightSize %>px">
                      <td width="150px" align="center">
                        &nbsp;
                      </td>
                    </tr>
                    <tr height="<%=heightSize %>px">
                      <td width="150px" align="center">
                      社員名
                      </td>
                    </tr>
                    <logic:iterate offset="offset" length="<%=showLength %>"  id="tsukibetsuShiftNyuuryokuBeanList" name="tsukibetsuShiftNyuuryokuForm" property="tsukibetsuShiftNyuuryokuBeanList">
                      <tr height="<%=heightSize %>px">
                        <td width="150px" align="center">
                          <bean:write property="shainName" name="tsukibetsuShiftNyuuryokuBeanList"/><br>
                        </td>
                      </tr>
                    </logic:iterate>
                  </table>
                </td>
                <td>
                  <div style="overflow-x: auto;overflow-y: hidden; width:985px;height: <%=heightSize * (listSize + 2) + 18 %>px; text-align:center;">
                    <table class="tblHeader" border="1" cellpadding="0" cellspacing="0">
                      <tr height="<%=heightSize %>px">
                      <!-- 修正：ota_naoki
                      冗長な書き方を修正
                      1~nまで手書きだったtdタグをfor文で記述
                       -->
                      <%
                      for(int i = 1; i <= dateBeanListSize; i++){
                    	  %> 
                    	  <td width="40px" align="center">
								<%= i %>
                        </td>
                    	  <%
                      }
                      %>
                      </tr>
                      <tr height="<%=heightSize %>px">
                        <logic:iterate id="dateBeanList" name="tsukibetsuShiftNyuuryokuForm" property="dateBeanList">
                          <bean:define id="youbi" name="dateBeanList" property="youbi"/>
                          <bean:define id="shukujitsuFlg" name="dateBeanList" property="shukujitsuFlg"/>
                            <%
                            if (DayOfWeek.SUNDAY.getRyaku().equals(youbi) || (boolean) shukujitsuFlg) {
                                color = "fontRed";
                            } else if (DayOfWeek.SATURDAY.getRyaku().equals(youbi)) {
                                color = "fontBlue";
                            } else {
                                color = "fontBlack";
                            }
                            %>

                            <td width="40px" align="center" class="<%=color %>">
                              <bean:write property="youbi" name="dateBeanList"/><br>
                            </td>
                        </logic:iterate>
                      </tr>
                      
                      <style>
						  /* CSS スタイルを適用するためのセレクタを指定 */
						  td select option[value="maru"],
						  td select option[value="sankaku"],
						  td select option[value="shikaku"],
						  td select option[value="hoshi"] {
						    font-family: "MS Gothic"; /* MS Gothic フォントを指定 */
						  }
						</style>
                      
                      <logic:iterate offset="offset" length="<%=showLength %>" id="tsukibetsuShiftNyuuryokuBeanList" name="tsukibetsuShiftNyuuryokuForm" property="tsukibetsuShiftNyuuryokuBeanList">
						  <html:hidden name="tsukibetsuShiftNyuuryokuBeanList" property="registFlg" value="true" indexed="true"/>
						  <tr height="<%=heightSize %>px">
						  <!-- 修正：ota_naoki
	                      冗長な書き方を修正
	                      1~nまで手書きだったtdタグをfor文で記述
	                       -->
						  <%
						  String baseShiftId = "shiftId";
						  String inputShiftId = null;
						  for(int i = 1; i <= dateBeanListSize; i++){
							  inputShiftId = baseShiftId;
							  if(i < 10){
								  inputShiftId += "0";
							  }
							  inputShiftId += i;
							  %>
							  <td width="40px" align="center" valign="middle">
						      <html:select property="<%=inputShiftId %>" name="tsukibetsuShiftNyuuryokuBeanList" indexed="true" style="font-family: 'MS ゴシック', sans-serif;">
						        <html:optionsCollection name="tsukibetsuShiftNyuuryokuForm"
						          property="shiftCmbMap"
						          value="key"
						          label="value"/>
						        
						      </html:select>
						    </td>
							  <% 
						  }
						  %>
						    
                        </tr>
                      </logic:iterate>
                    </table>
                  </div>
                </td>
              </tr>
            </table>
          </div>
        </html:form>
      </div>
      <div id="footer">
      <div style="margin-left:50px;">
          <input value="凡例表示" type="button" class="lngButton"  onclick="openWindow()" />
          <input value="基本シフト反映" type="button" class="lngButton"  onclick="submitKihonShift()"/>
          <input value="出勤希望日反映" type="button" class="lngButton"  onclick="submitShukkinKibou()" />
        </div>
        <table>
          <tr>
            <td id="footLeft">
            </td>
            <td id="footCenter" style="text-align: right;">

            </td>
            <td id="footRight">
              <input value="登録" type="button" class="smlButton"  onclick="submitRegist()" />
            </td>
          </tr>
        </table>
      </div>
    </div>
  </body>
</html>