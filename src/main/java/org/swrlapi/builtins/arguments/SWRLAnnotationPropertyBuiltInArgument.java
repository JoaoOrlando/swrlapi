package org.swrlapi.builtins.arguments;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;

/**
 * Represents an OWL annotation property argument to a SWRL built-in atom.
 *
 * @see org.semanticweb.owlapi.model.OWLAnnotationProperty
 */
public interface SWRLAnnotationPropertyBuiltInArgument extends SWRLNamedBuiltInArgument
{
	OWLAnnotationProperty getOWLAnnotationProperty();
}
