package org.swrlapi.builtins;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.swrlapi.exceptions.SWRLBuiltInBridgeException;

/**
 * This interface defines methods required by a built-in bridge controller.
 */
public interface SWRLBuiltInBridgeController
{
	void reset() throws SWRLBuiltInBridgeException;

	int getNumberOfInjectedOWLAxioms();

	boolean isInjectedOWLAxiom(OWLAxiom axiom);

	Set<OWLAxiom> getInjectedOWLAxioms();
}
