/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Department.java
 * 
 * Created on 6 de Novembro de 2002, 15:57
 */

/**
 * @author Nuno Nunes & Joana Mota
 */

package net.sourceforge.fenixedu.domain;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.degreeStructure.CycleType;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.messaging.DepartmentForum;
import net.sourceforge.fenixedu.domain.organizationalStructure.CompetenceCourseGroupUnit;
import net.sourceforge.fenixedu.domain.organizationalStructure.DepartmentUnit;
import net.sourceforge.fenixedu.domain.organizationalStructure.ScientificAreaUnit;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;
import net.sourceforge.fenixedu.domain.teacher.TeacherPersonalExpectation;
import net.sourceforge.fenixedu.domain.time.calendarStructure.AcademicInterval;
import net.sourceforge.fenixedu.domain.util.FactoryExecutor;
import net.sourceforge.fenixedu.domain.vigilancy.VigilantGroup;
import net.sourceforge.fenixedu.injectionCode.AccessControl;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.Interval;
import org.joda.time.YearMonthDay;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class Department extends Department_Base {

    public static final Comparator<Department> COMPARATOR_BY_NAME = new ComparatorChain();
    static {
        ((ComparatorChain) COMPARATOR_BY_NAME).addComparator(new BeanComparator("name", Collator.getInstance()));
        ((ComparatorChain) COMPARATOR_BY_NAME).addComparator(DomainObjectUtil.COMPARATOR_BY_ID);
    }

    public Department() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    public List<Employee> getAllCurrentActiveWorkingEmployees() {
        Unit departmentUnit = getDepartmentUnit();
        return (departmentUnit != null) ? departmentUnit.getAllCurrentActiveWorkingEmployees() : new ArrayList<Employee>(0);
    }

    public List<Employee> getAllWorkingEmployees(YearMonthDay begin, YearMonthDay end) {
        Unit departmentUnit = getDepartmentUnit();
        return (departmentUnit != null) ? departmentUnit.getAllWorkingEmployees(begin, end) : new ArrayList<Employee>(0);
    }

    public List<Employee> getAllWorkingEmployees() {
        Unit departmentUnit = getDepartmentUnit();
        return (departmentUnit != null) ? departmentUnit.getAllWorkingEmployees() : new ArrayList<Employee>(0);
    }

    public List<Teacher> getAllCurrentTeachers() {
        Unit departmentUnit = getDepartmentUnit();
        List<Teacher> list = (departmentUnit != null) ? departmentUnit.getAllCurrentTeachers() : new ArrayList<Teacher>(0);
        for (ExternalTeacherAuthorization teacherAuthorization : this.getTeacherAuthorizationsAuthorizedSet()) {
            if (teacherAuthorization.getActive()
                    && teacherAuthorization.getExecutionSemester().equals(ExecutionSemester.readActualExecutionSemester())
                    && !list.contains(teacherAuthorization.getTeacher())) {
                list.add(teacherAuthorization.getTeacher());
            }
        }
        return list;
    }

    public List<Teacher> getAllTeachers() {
        Unit departmentUnit = getDepartmentUnit();
        List<Teacher> list = (departmentUnit != null) ? departmentUnit.getAllTeachers() : new ArrayList<Teacher>(0);
        for (ExternalTeacherAuthorization teacherAuthorization : this.getTeacherAuthorizationsAuthorizedSet()) {
            if (teacherAuthorization.getActive() && !list.contains(teacherAuthorization.getTeacher())) {
                list.add(teacherAuthorization.getTeacher());
            }
        }
        return list;
    }

    public List<Teacher> getAllTeachers(YearMonthDay begin, YearMonthDay end) {
        Unit departmentUnit = getDepartmentUnit();
        List<Teacher> list = (departmentUnit != null) ? departmentUnit.getAllTeachers(begin, end) : new ArrayList<Teacher>(0);
        for (ExternalTeacherAuthorization teacherAuthorization : this.getTeacherAuthorizationsAuthorizedSet()) {
            if (teacherAuthorization.getActive()
                    && teacherAuthorization.getExecutionSemester().getAcademicInterval()
                            .overlaps(new Interval(begin.toDateMidnight(), end.toDateMidnight()))
                    && !list.contains(teacherAuthorization.getTeacher())) {
                list.add(teacherAuthorization.getTeacher());
            }
        }
        return list;
    }

    public List<Teacher> getAllTeachers(AcademicInterval academicInterval) {
        Unit departmentUnit = getDepartmentUnit();
        List<Teacher> list =
                (departmentUnit != null) ? departmentUnit.getAllTeachers(academicInterval) : new ArrayList<Teacher>(0);
        for (ExternalTeacherAuthorization teacherAuthorization : this.getTeacherAuthorizationsAuthorizedSet()) {
            if (teacherAuthorization.getActive()
                    && teacherAuthorization.getExecutionSemester().getAcademicInterval().overlaps(academicInterval)
                    && !list.contains(teacherAuthorization.getTeacher())) {
                list.add(teacherAuthorization.getTeacher());
            }
        }
        return list;
    }

    public Set<DegreeType> getDegreeTypes() {
        Set<DegreeType> degreeTypes = new TreeSet<DegreeType>();
        for (Degree degree : getDegreesSet()) {
            degreeTypes.add(degree.getDegreeType());
        }
        return degreeTypes;
    }

    public Set<CycleType> getCycleTypes() {
        TreeSet<CycleType> cycles = new TreeSet<CycleType>();
        for (DegreeType degreeType : getDegreeTypes()) {
            cycles.addAll(degreeType.getCycleTypes());
        }
        return cycles;
    }

    @Deprecated
    /**
     * 
     * This direct association between CompetenceCourses and Departments should no longer be used.
     * Instead, use the DepartmentUnit to get the CompetenceCourses.
     * 
     */
    public List<CompetenceCourse> getCompetenceCoursesByExecutionYear(ExecutionYear executionYear) {
        Collection<CompetenceCourse> competenceCourses = this.getCompetenceCoursesSet();
        List<CompetenceCourse> competenceCoursesByExecutionYear = new ArrayList<CompetenceCourse>();
        for (CompetenceCourse competenceCourse : competenceCourses) {
            if (competenceCourse.hasActiveScopesInExecutionYear(executionYear)) {
                competenceCoursesByExecutionYear.add(competenceCourse);
            }

        }
        return competenceCoursesByExecutionYear;
    }

    @Deprecated
    /**
     * 
     * This direct association between CompetenceCourses and Departments should no longer be used.
     * Instead, use addAllBolonhaCompetenceCourses()
     * 
     */
    public void addAllCompetenceCoursesByExecutionPeriod(final Collection<CompetenceCourse> competenceCourses,
            final ExecutionSemester executionSemester) {
        for (CompetenceCourse competenceCourse : getCompetenceCoursesSet()) {
            if (competenceCourse.hasActiveScopesInExecutionPeriod(executionSemester)) {
                competenceCourses.add(competenceCourse);
            }
        }
    }

    @Deprecated
    /**
     * 
     * This direct association between CompetenceCourses and Departments should no longer be used.
     * Instead, use the DepartmentUnit to get the CompetenceCourses.
     * 
     */
    public Set<ExecutionCourse> getAllExecutionCoursesByExecutionPeriod(final ExecutionSemester executionSemester) {

        Set<ExecutionCourse> executionCourses = new HashSet<ExecutionCourse>();

        for (CompetenceCourse competenceCourse : getCompetenceCoursesSet()) {
            competenceCourse.getExecutionCoursesByExecutionPeriod(executionSemester, executionCourses);
        }

        return executionCourses;
    }

    public List<TeacherPersonalExpectation> getTeachersPersonalExpectationsByExecutionYear(ExecutionYear executionYear) {
        List<Teacher> teachersFromDepartment =
                getAllTeachers(executionYear.getBeginDateYearMonthDay(), executionYear.getEndDateYearMonthDay());
        List<TeacherPersonalExpectation> personalExpectations = new ArrayList<TeacherPersonalExpectation>();
        for (Teacher teacher : teachersFromDepartment) {
            TeacherPersonalExpectation teacherPersonalExpectation =
                    teacher.getTeacherPersonalExpectationByExecutionYear(executionYear);
            if (teacherPersonalExpectation != null) {
                personalExpectations.add(teacherPersonalExpectation);
            }
        }
        return personalExpectations;
    }

    public String getAcronym() {
        final int begin = this.getRealName().indexOf("(");
        final int end = this.getRealName().indexOf(")");
        return this.getRealName().substring(begin + 1, end);
    }

    public List<VigilantGroup> getVigilantGroupsForGivenExecutionYear(ExecutionYear executionYear) {
        Unit departmentUnit = this.getDepartmentUnit();
        return departmentUnit.getVigilantGroupsForGivenExecutionYear(executionYear);
    }

    public List<CompetenceCourse> getBolonhaCompetenceCourses() {
        DepartmentUnit departmentUnit = this.getDepartmentUnit();
        List<CompetenceCourse> courses = new ArrayList<CompetenceCourse>();
        for (ScientificAreaUnit areaUnit : departmentUnit.getScientificAreaUnits()) {
            for (CompetenceCourseGroupUnit competenceCourseGroupUnit : areaUnit.getCompetenceCourseGroupUnits()) {
                courses.addAll(competenceCourseGroupUnit.getCompetenceCourses());
            }
        }
        return courses;
    }

    public void addAllBolonhaCompetenceCourses(final Collection<CompetenceCourse> competenceCourses,
            final ExecutionSemester period) {
        for (CompetenceCourse course : getBolonhaCompetenceCourses()) {
            if (!course.getCurricularCoursesWithActiveScopesInExecutionPeriod(period).isEmpty()) {
                competenceCourses.add(course);
            }
        }
    }

    public TeacherPersonalExpectationPeriod getTeacherPersonalExpectationPeriodForExecutionYear(ExecutionYear executionYear,
            Class<? extends TeacherPersonalExpectationPeriod> clazz) {

        if (executionYear != null) {
            for (TeacherPersonalExpectationPeriod period : getTeacherPersonalExpectationPeriodsSet()) {
                if (period.getExecutionYear().equals(executionYear) && period.getClass().equals(clazz)) {
                    return period;
                }
            }
        }
        return null;
    }

    public TeacherAutoEvaluationDefinitionPeriod getTeacherAutoEvaluationDefinitionPeriodForExecutionYear(
            ExecutionYear executionYear) {
        TeacherPersonalExpectationPeriod period =
                getTeacherPersonalExpectationPeriodForExecutionYear(executionYear, TeacherAutoEvaluationDefinitionPeriod.class);
        return period != null ? (TeacherAutoEvaluationDefinitionPeriod) period : null;
    }

    public TeacherExpectationDefinitionPeriod getTeacherExpectationDefinitionPeriodForExecutionYear(ExecutionYear executionYear) {
        TeacherPersonalExpectationPeriod period =
                getTeacherPersonalExpectationPeriodForExecutionYear(executionYear, TeacherExpectationDefinitionPeriod.class);
        return period != null ? (TeacherExpectationDefinitionPeriod) period : null;
    }

    public TeacherPersonalExpectationsVisualizationPeriod getTeacherPersonalExpectationsVisualizationPeriodByExecutionYear(
            ExecutionYear executionYear) {
        TeacherPersonalExpectationPeriod period =
                getTeacherPersonalExpectationPeriodForExecutionYear(executionYear,
                        TeacherPersonalExpectationsVisualizationPeriod.class);
        return period != null ? (TeacherPersonalExpectationsVisualizationPeriod) period : null;
    }

    public TeacherPersonalExpectationsEvaluationPeriod getTeacherPersonalExpectationsEvaluationPeriodByExecutionYear(
            ExecutionYear executionYear) {
        TeacherPersonalExpectationPeriod period =
                getTeacherPersonalExpectationPeriodForExecutionYear(executionYear,
                        TeacherPersonalExpectationsEvaluationPeriod.class);
        return period != null ? (TeacherPersonalExpectationsEvaluationPeriod) period : null;
    }

    public List<Teacher> getPossibleTutors() {
        List<Teacher> teachers = new ArrayList<Teacher>();

        for (Teacher teacher : this.getAllTeachers()) {
            if (teacher.canBeTutorOfDepartment(this)) {
                teachers.add(teacher);
            }
        }

        Collections.sort(teachers, Teacher.TEACHER_COMPARATOR_BY_CATEGORY_AND_NUMBER);
        return teachers;

    }

    // -------------------------------------------------------------
    // read static methods
    // -------------------------------------------------------------
    public static Department readByName(final String departmentName) {
        for (final Department department : Bennu.getInstance().getDepartmentsSet()) {
            if (department.getName().equals(departmentName)) {
                return department;
            }
        }
        return null;
    }

    public static class DepartmentDegreeBean implements FactoryExecutor, Serializable {

        private Department department;
        private Degree degree;

        public Department getDepartment() {
            return department;
        }

        public void setDepartment(final Department department) {
            this.department = department;
        }

        public Degree getDegree() {
            return degree;
        }

        public void setDegree(final Degree degree) {
            this.degree = degree;
        }

        @Override
        public Object execute() {
            final Department department = getDepartment();
            final Degree degree = getDegree();
            if (department != null && degree != null) {
                department.getDegreesSet().add(degree);
            }
            return null;
        }
    }

    public void delete() {
        if (!getTeacherGroupSet().isEmpty()) {
            throw new DomainException("error.department.cannotDeleteDepartmentUsedInAccessControl");
        }
        setDepartmentUnit(null);
        setRootDomainObject(null);
        deleteDomainObject();
    }

    /**
     * Joins the portuguese and english version of the department's name in a
     * MultiLanguageString for an easier handling of the name in a
     * internacionalized context.
     * 
     * @return a MultiLanguageString with the portuguese and english versions of
     *         the department's name
     */
    public MultiLanguageString getNameI18n() {
        return new MultiLanguageString().with(MultiLanguageString.pt, getRealName()).with(MultiLanguageString.en, getRealNameEn());
    }

    public Integer getCompetenceCourseInformationChangeRequestsCount() {
        int count = 0;
        for (CompetenceCourse course : getDepartmentUnit().getCompetenceCourses()) {
            count += course.getCompetenceCourseInformationChangeRequestsSet().size();
        }

        return count;
    }

    public Integer getDraftCompetenceCourseInformationChangeRequestsCount() {
        int count = 0;
        for (CompetenceCourse course : getDepartmentUnit().getCompetenceCourses()) {
            count += course.getDraftCompetenceCourseInformationChangeRequestsCount();
        }

        return count;
    }

    public Group getCompetenceCourseMembersGroup() {
        return getMembersGroup().toGroup();
    }

    public void setCompetenceCourseMembersGroup(Group group) {
        setMembersGroup(group.toPersistentGroup());
    }

    public boolean isUserMemberOfCompetenceCourseMembersGroup(User user) {
        return getCompetenceCourseMembersGroup().isMember(user);
    }

    public boolean isUserMemberOfCompetenceCourseMembersGroup(Person person) {
        return getCompetenceCourseMembersGroup().isMember(person.getUser());
    }

    public boolean isCurrentUserMemberOfCompetenceCourseMembersGroup() {
        return isUserMemberOfCompetenceCourseMembersGroup(AccessControl.getPerson());
    }

    public boolean hasCurrentActiveWorkingEmployee(final Employee employee) {
        final Unit unit = getDepartmentUnit();
        return unit != null && unit.hasCurrentActiveWorkingEmployee(employee);
    }

    public DepartmentForum getDepartmentForum() {
        return getForum();
    }

    public Person getCurrentDepartmentPresident() {
        final DepartmentUnit unit = getDepartmentUnit();
        return unit == null ? null : unit.getCurrentDepartmentPresident();
    }

    public boolean isCurrentUserCurrentDepartmentPresident() {
        final Person person = AccessControl.getPerson();
        return isCurrentDepartmentPresident(person);
    }

    public boolean isCurrentDepartmentPresident(final Person person) {
        return person != null && person == getCurrentDepartmentPresident();
    }

    public static Department find(final String departmentCode) {
        for (final Department department : Bennu.getInstance().getDepartmentsSet()) {
            if (department.getAcronym().equals(departmentCode)) {
                return department;
            }
        }
        if (StringUtils.isNumeric(departmentCode)) {
            final Unit unit = Unit.readByCostCenterCode(new Integer(departmentCode));
            if (unit != null) {
                final net.sourceforge.fenixedu.domain.organizationalStructure.DepartmentUnit departmentUnit =
                        unit.getDepartmentUnit();
                return departmentUnit == null ? null : departmentUnit.getDepartment();
            }
        }
        return null;
    }

    public static List<Department> readActiveDepartments() {
        final List<Department> departments = new ArrayList<Department>();
        for (final Department department : Bennu.getInstance().getDepartmentsSet()) {
            if (department.getActive()) {
                departments.add(department);
            }
        }
        Collections.sort(departments, Department.COMPARATOR_BY_NAME);
        return departments;
    }

}
