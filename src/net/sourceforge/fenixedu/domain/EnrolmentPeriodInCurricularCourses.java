/*
 * Created on 2004/08/24
 *
 */
package net.sourceforge.fenixedu.domain;

import java.util.Date;

/**
 * @author Luis Cruz
 */
public class EnrolmentPeriodInCurricularCourses extends EnrolmentPeriodInCurricularCourses_Base {

    public EnrolmentPeriodInCurricularCourses(final DegreeCurricularPlan degreeCurricularPlan,
	    final ExecutionSemester executionSemester, final Date startDate, final Date endDate) {
	super();
	init(degreeCurricularPlan, executionSemester, startDate, endDate);
    }

}