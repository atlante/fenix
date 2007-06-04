package net.sourceforge.fenixedu.domain.degreeStructure;

import java.text.Collator;
import java.util.Comparator;
import java.util.Set;

import net.sourceforge.fenixedu.domain.CurricularCourse;
import net.sourceforge.fenixedu.domain.DegreeModuleScope;
import net.sourceforge.fenixedu.domain.ExecutionPeriod;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.RootDomainObject;
import net.sourceforge.fenixedu.domain.curricularPeriod.CurricularPeriod;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.injectionCode.Checked;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;

import dml.runtime.RelationAdapter;

public class Context extends Context_Base implements Comparable<Context> {

    public static final Comparator<Context> COMPARATOR_BY_DEGREE_MODULE_NAME = new ComparatorChain();
    static {
	((ComparatorChain) COMPARATOR_BY_DEGREE_MODULE_NAME).addComparator(new BeanComparator("childDegreeModule.name", Collator.getInstance()));
	((ComparatorChain) COMPARATOR_BY_DEGREE_MODULE_NAME).addComparator(new BeanComparator("childDegreeModule.idInternal"));
    }

    static {
	CourseGroupContext.addListener(new RelationAdapter<Context, CourseGroup>() {
	    
	    @Override
	    public void beforeAdd(Context context, CourseGroup courseGroup) {
	        if(context != null && courseGroup != null) {
	            if(context.getChildDegreeModule() != null && 
	        	    context.getChildDegreeModule().isCycleCourseGroup()) {
	        	CycleCourseGroup cycleCourseGroup = (CycleCourseGroup) context.getChildDegreeModule();
	        	if(cycleCourseGroup.getParentContexts().size() > 1) {
	        	    throw new DomainException("error.degreeStructure.CycleCourseGroup.can.only.have.one.parent");
	        	}
	        	if(!courseGroup.isRoot()) {
	        	    throw new DomainException("error.degreeStructure.CycleCourseGroup.parent.must.be.RootCourseGroup");
	        	}
	            }
	        }
	    }
	    
	});
	
	DegreeModuleContext.addListener(new RelationAdapter<Context, DegreeModule>() {
	    @Override
	    public void beforeAdd(Context context, DegreeModule degreeModule) {
		if(context != null && degreeModule != null) {
	            if(degreeModule.isRoot()) {
	        	throw new DomainException("error.degreeStructure.RootCourseGroup.cannot.have.parent.contexts");
	            }
	            if(degreeModule.isCycleCourseGroup()) {
	        	CycleCourseGroup cycleCourseGroup = (CycleCourseGroup) degreeModule;
	        	if(cycleCourseGroup.getParentContexts().size() > 0) {
	        	    throw new DomainException("error.degreeStructure.CycleCourseGroup.can.only.have.one.parent");
	        	}
	        	if(context.getParentCourseGroup() != null && !context.getParentCourseGroup().isRoot()) {
	        	    throw new DomainException("error.degreeStructure.CycleCourseGroup.parent.must.be.RootCourseGroup");
	        	}
	            }
	        }
	    }
	});
	
	
    }

    protected Context() {
        super();
        setRootDomainObject(RootDomainObject.getInstance());
        this.setChildOrder(0);
    }

    public Context(CourseGroup courseGroup, DegreeModule degreeModule,
            CurricularPeriod curricularPeriod, ExecutionPeriod beginExecutionPeriod,
            ExecutionPeriod endExecutionPeriod) {
        
        this();
        if (courseGroup == null || degreeModule == null || beginExecutionPeriod == null) {
            throw new DomainException("error.incorrectContextValues");
        }
        
        checkExecutionPeriods(beginExecutionPeriod, endExecutionPeriod);

        super.setParentCourseGroup(courseGroup);
        super.setChildDegreeModule(degreeModule);
        super.setCurricularPeriod(curricularPeriod);
        super.setBeginExecutionPeriod(beginExecutionPeriod);
        super.setEndExecutionPeriod(endExecutionPeriod);
    }
    
    public void edit(CourseGroup parentCourseGroup, DegreeModule degreeModule,
            CurricularPeriod curricularPeriod, ExecutionPeriod beginExecutionPeriod,
            ExecutionPeriod endExecutionPeriod) {
        
        edit(beginExecutionPeriod, endExecutionPeriod);
        setParentCourseGroup(parentCourseGroup);
        setChildDegreeModule(degreeModule);
        setCurricularPeriod(curricularPeriod);
    }
    
    protected void edit(ExecutionPeriod beginExecutionPeriod, ExecutionPeriod endExecutionPeriod) {
        checkExecutionPeriods(beginExecutionPeriod, endExecutionPeriod);
        setBeginExecutionPeriod(beginExecutionPeriod);
        setEndExecutionPeriod(endExecutionPeriod);
    }

    public void delete() {
        removeCurricularPeriod();
        removeChildDegreeModule();
        removeParentCourseGroup();
        removeBeginExecutionPeriod();
        removeEndExecutionPeriod();
        removeRootDomainObject();
        super.deleteDomainObject();
    }
    
    public int compareTo(Context o) {
        int orderCompare = this.getChildOrder().compareTo(o.getChildOrder());
        if (this.getParentCourseGroup().equals(o.getParentCourseGroup()) && orderCompare != 0) {
            return orderCompare;
        } else {
            if (this.getChildDegreeModule() instanceof CurricularCourse) {
                int periodsCompare = this.getCurricularPeriod().compareTo(o.getCurricularPeriod());
                if (periodsCompare != 0) {
                    return periodsCompare;     
                }
                return this.getChildDegreeModule().getName().compareTo(o.getChildDegreeModule().getName());
            } else {
                return this.getChildDegreeModule().getName().compareTo(o.getChildDegreeModule().getName());
            }
        }
    }

    @Override
    @Checked("ContextPredicates.curricularPlanMemberWritePredicate")
    public void setParentCourseGroup(CourseGroup courseGroup) {
        super.setParentCourseGroup(courseGroup);
    }

    @Override
    @Checked("ContextPredicates.curricularPlanMemberWritePredicate")
    public void setCurricularPeriod(CurricularPeriod curricularPeriod) {
        super.setCurricularPeriod(curricularPeriod);
    }

    @Override
    @Checked("ContextPredicates.curricularPlanMemberWritePredicate")
    public void setChildDegreeModule(DegreeModule degreeModule) {
        super.setChildDegreeModule(degreeModule);
    }
    
    public boolean isValid(ExecutionPeriod executionPeriod) {
	return isOpen(executionPeriod)
            && ((getChildDegreeModule() instanceof CurricularCourse && containsSemester(executionPeriod.getSemester()))
                    || !(getChildDegreeModule() instanceof CurricularCourse));
    }
    
    public boolean isValid(final ExecutionYear executionYear) {
	for (final ExecutionPeriod executionPeriod : executionYear.getExecutionPeriods()) {
	    if (isValid(executionPeriod)) {
		return true;
	    }
	}
	return false;
    }
    
    protected void checkExecutionPeriods(ExecutionPeriod beginExecutionPeriod, ExecutionPeriod endExecutionPeriod) {
	if (beginExecutionPeriod == null) {
	    throw new DomainException("context.begin.execution.period.cannot.be.null");
	}
        if (endExecutionPeriod != null && beginExecutionPeriod.isAfter(endExecutionPeriod)) {
            throw new DomainException("context.begin.is.after.end.execution.period");
        }
    }
    
    public boolean isOpen(final ExecutionPeriod executionPeriod) {
	return getBeginExecutionPeriod().isBeforeOrEquals(executionPeriod)
		&& (!hasEndExecutionPeriod() || getEndExecutionPeriod().isAfterOrEquals(executionPeriod));
    }
    
    public boolean isOpen(final ExecutionYear executionYear) {
	for (final ExecutionPeriod executionPeriod : executionYear.getExecutionPeriods()) {
	    if (isOpen(executionPeriod)) {
		return true;
	    }
	}
	return false;
    }

    @Deprecated
    public Integer getOrder() {
        return super.getChildOrder();
    }

    @Deprecated
    public void setOrder(Integer order) {
        super.setChildOrder(order);
    }

    public boolean containsCurricularYear(final Integer curricularYear) {        
        final CurricularPeriod firstCurricularPeriod = getCurricularPeriod().getParent();
        final int firstCurricularPeriodOrder = firstCurricularPeriod.getAbsoluteOrderOfChild();                
        return curricularYear.intValue() == firstCurricularPeriodOrder;
    } 
    
    public boolean containsSemester(final Integer semester) {        
        final CurricularPeriod firstCurricularPeriod = getCurricularPeriod();
        final int firstCurricularPeriodOrder = firstCurricularPeriod.getChildOrder();                
        return semester.intValue() == firstCurricularPeriodOrder;
    } 
    
    public boolean containsSemesterAndCurricularYear(final Integer semester, final Integer curricularYear, 
            final RegimeType regimeType) {
        
        final int argumentOrder = (curricularYear - 1) * 2 + semester.intValue();        
        final CurricularPeriod firstCurricularPeriod = getCurricularPeriod();
        final int firstCurricularPeriodOrder = firstCurricularPeriod.getAbsoluteOrderOfChild();
        final int duration;
        if (regimeType == RegimeType.ANUAL) {
            duration = 2;
        } else if (regimeType == RegimeType.SEMESTRIAL) {
            duration = 1;
        } else {
            throw new IllegalArgumentException("Unknown regimeType: " + regimeType);
        }
        final int lastCurricularPeriodOrder = firstCurricularPeriodOrder + duration - 1;
        return firstCurricularPeriodOrder <= argumentOrder && argumentOrder <= lastCurricularPeriodOrder;            
    }
    
    private DegreeModuleScopeContext degreeModuleScopeContext = null;
    
    private synchronized void initDegreeModuleScopeContext() {
        if(degreeModuleScopeContext == null) {
            degreeModuleScopeContext = new DegreeModuleScopeContext(this);
        }
    }
    
    public DegreeModuleScopeContext getDegreeModuleScopeContext() {
        if(degreeModuleScopeContext == null) {
            initDegreeModuleScopeContext();
        }
        return degreeModuleScopeContext;
    }
    
    @Override
    public void setBeginExecutionPeriod(ExecutionPeriod beginExecutionPeriod) {
	if (beginExecutionPeriod == null) {
	    throw new DomainException("curricular.rule.begin.execution.period.cannot.be.null");
	}
        super.setBeginExecutionPeriod(beginExecutionPeriod);
    }
    
    public void removeBeginExecutionPeriod() {
        super.setBeginExecutionPeriod(null);
    }
    
    public class DegreeModuleScopeContext extends DegreeModuleScope {

        private final Context context;
        
        private DegreeModuleScopeContext(Context context) {
            this.context = context;
        }
               
        @Override
        public Integer getIdInternal() {       
            return context.getIdInternal();
        }

        @Override
        public Integer getCurricularSemester() {        
            return context.getCurricularPeriod().getChildOrder();                        
        }

        @Override
        public Integer getCurricularYear() {
            return context.getCurricularYear();
        }

        @Override
        public String getBranch() {            
            return "";
        }           
        
        public Context getContext() {
            return context;
        }

        @Override
        public boolean isActiveForExecutionPeriod(final ExecutionPeriod executionPeriod) {
            return getContext().isValid(executionPeriod);
        }

        @Override
        public CurricularCourse getCurricularCourse() {            
            return (CurricularCourse) context.getChildDegreeModule();
        }

	@Override
	public String getAnotation() {
	    return null;
	}
	
    }

    public Integer getCurricularYear() {
	return getCurricularPeriod().getParent().getAbsoluteOrderOfChild();
    }

    public void getAllCurricularCourses(final Set<CurricularCourse> curricularCourses) {
	final DegreeModule degreeModule = getChildDegreeModule();
	degreeModule.getAllCurricularCourses(curricularCourses);
    }

}