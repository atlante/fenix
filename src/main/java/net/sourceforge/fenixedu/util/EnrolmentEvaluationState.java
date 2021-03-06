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
package net.sourceforge.fenixedu.util;

/**
 * @author dcs-rjao
 * 
 *         2/Abr/2003
 */
public class EnrolmentEvaluationState extends FenixUtil {

    public static final int FINAL = 1;

    public static final int TEMPORARY = 2;

    public static final int RECTIFIED = 5;

    public static final int RECTIFICATION = 3;

    public static final int ANNULED = 4;

    public static final EnrolmentEvaluationState FINAL_OBJ = new EnrolmentEvaluationState(EnrolmentEvaluationState.FINAL, 2);

    public static final EnrolmentEvaluationState TEMPORARY_OBJ = new EnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY,
            1);

    public static final EnrolmentEvaluationState RECTIFIED_OBJ = new EnrolmentEvaluationState(EnrolmentEvaluationState.RECTIFIED,
            3);

    public static final EnrolmentEvaluationState RECTIFICATION_OBJ = new EnrolmentEvaluationState(
            EnrolmentEvaluationState.RECTIFICATION, 4);

    public static final EnrolmentEvaluationState ANNULED_OBJ = new EnrolmentEvaluationState(EnrolmentEvaluationState.ANNULED, 0);

    private final Integer state;

    private final int weight;

    private EnrolmentEvaluationState(int state, int weight) {
        this.state = new Integer(state);
        this.weight = weight;
    }

    public static EnrolmentEvaluationState valueOf(Integer state) {
        switch (state) {
        case FINAL:
            return FINAL_OBJ;
        case TEMPORARY:
            return TEMPORARY_OBJ;
        case RECTIFIED:
            return RECTIFIED_OBJ;
        case RECTIFICATION:
            return RECTIFICATION_OBJ;
        case ANNULED:
            return ANNULED_OBJ;
        default:
            return null;
        }
    }

    /**
     * Getter for property state.
     * 
     * @return Value of property state.
     * 
     */
    public java.lang.Integer getState() {
        return state;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EnrolmentEvaluationState) {
            EnrolmentEvaluationState aux = (EnrolmentEvaluationState) o;
            return this.state.equals(aux.getState());
        }
        return false;

    }

    @Override
    public String toString() {

        int value = this.state.intValue();
        String valueS = null;

        switch (value) {
        case TEMPORARY:
            valueS = "TEMPORARY";
            break;
        case FINAL:
            valueS = "FINAL";
            break;
        case RECTIFIED:
            valueS = "RECTIFIED";
            break;
        case RECTIFICATION:
            valueS = "RECTIFICATION";
            break;
        case ANNULED:
            valueS = "ANNULED";
            break;
        default:
            break;
        }

        return valueS;
    }
}