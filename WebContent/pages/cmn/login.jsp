<!-- login.jsp -->
<%@page contentType="text/html; charset=Shift_JIS" pageEncoding="Shift_JIS"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html>
<head>
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv="Cache-Control" content="no-cache">
	<meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
	<html:javascript formName="loginForm" />
	<title>ログイン画面</title>
	<link href="/kikin_test/pages/css/common.css" rel="stylesheet" type="text/css" />
	<link href="/kikin_test/pages/css/test.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="/kikin_test/pages/js/message.js"></script>
	<script type="text/javascript">
		/*
		追記：ota_naoki
		パスワード/社員IDが違う場合のポップを追加
		*/
		function disp(){
			var flgValue = <%= request.getAttribute("flag") %>;
			if(flgValue){
				var msg = getMessageCodeOnly('E-MSG-000002');
				window.alert(msg);
			}
		}
		window.onload = disp;
	</script>
</head>
<body>
		<div class="body"></div>
	    <div class="grad"></div>
	    <br>
	    <div class="login">
	        <html:form action="/login" onsubmit="return validateLoginForm(this)">
		      <html:text property="shainId" size="16" value="" />
		      <br/>
		      <html:password property="password" size="16" redisplay="false" value=""/>
		      <br/>
		      <br/>
		      <html:submit property="submit"  value="Login" />
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
	                　
	              </td>
	          </tr>
	        </table>
	    </div>
		</body>
</html>