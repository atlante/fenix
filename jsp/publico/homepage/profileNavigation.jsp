<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<ul class="treemenu">
	<li>
		<html:link page="/viewHomepage.do?method=list">
			Homepage listings
		</html:link>
    </li>
	<li>
		<html:link page="/l1">
			Blogs
		</html:link>
    </li>
	<li>
		<html:link page="/l1">
			Forums
		</html:link>
    </li>
	<li>
		<html:link page="/l1">
			Statistics
		</html:link>
    </li>
</ul>