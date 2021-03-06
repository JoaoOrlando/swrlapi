package org.swrlapi.bridge.converters;

import java.util.Set;

import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.swrlapi.core.SWRLAPIBuiltInAtom;

/**
 * This interface describes methods that can be implemented by a target rule engine to convert SWRL body atoms to native
 * rule clauses.
 * <p>
 * This interface can be used by implementation that need to track variable definitions as each atom is defined.
 * <p>
 * Implementors may also chose an alternate conversion approach.
 *
 * @see org.semanticweb.owlapi.model.SWRLAtom
 */
public interface TargetRuleEngineSWRLBodyAtomWithVariableNamesConverter<T> extends TargetRuleEngineConverter
{
	T convert(SWRLClassAtom atom, Set<String> previouslyEncounteredVariablePrefixedNames);

	T convert(SWRLDataPropertyAtom atom, Set<String> previouslyEncounteredVariablePrefixedNames);

	T convert(SWRLObjectPropertyAtom atom, Set<String> previouslyEncounteredVariablePrefixedNames);

	T convert(SWRLSameIndividualAtom atom, Set<String> previouslyEncounteredVariablePrefixedNames);

	T convert(SWRLDifferentIndividualsAtom atom, Set<String> previouslyEncounteredVariablePrefixedNames);

	T convert(SWRLAPIBuiltInAtom atom, Set<String> previouslyEncounteredVariablePrefixedNames);

	T convert(SWRLDataRangeAtom atom, Set<String> previouslyEncounteredVariablePrefixedNames);
}
