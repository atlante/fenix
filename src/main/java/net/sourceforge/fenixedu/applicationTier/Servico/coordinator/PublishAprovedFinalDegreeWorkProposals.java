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
package net.sourceforge.fenixedu.applicationTier.Servico.coordinator;

import java.util.Set;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.domain.ExecutionDegree;
import net.sourceforge.fenixedu.domain.finalDegreeWork.Proposal;
import net.sourceforge.fenixedu.util.FinalDegreeWorkProposalStatus;
import pt.ist.fenixframework.Atomic;

public class PublishAprovedFinalDegreeWorkProposals {
    @Atomic
    public static void run(ExecutionDegree executionDegree) throws FenixServiceException {
        if (executionDegree != null) {
            Set<Proposal> aprovedFinalDegreeWorkProposals = executionDegree.getScheduling().findApprovedProposals();

            if (aprovedFinalDegreeWorkProposals != null && !aprovedFinalDegreeWorkProposals.isEmpty()) {
                for (Proposal proposal : aprovedFinalDegreeWorkProposals) {
                    proposal.setStatus(FinalDegreeWorkProposalStatus.PUBLISHED_STATUS);
                }
            }
        }
    }

}
