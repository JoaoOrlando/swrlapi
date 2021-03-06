package org.swrlapi.core;

import java.net.URI;

import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.swrlapi.core.xsd.XSDDate;
import org.swrlapi.core.xsd.XSDDateTime;
import org.swrlapi.core.xsd.XSDDuration;
import org.swrlapi.core.xsd.XSDTime;

/**
 * Factory for constructing SWRLAPI literals, which wrap {@link org.semanticweb.owlapi.model.OWLLiteral}s
 * to provide additional convenience methods used be the SWRLAPI.
 * 
 * @see org.swrlapi.core.SWRLAPILiteral
 * @see org.semanticweb.owlapi.model.OWLLiteral
 */
public interface SWRLAPILiteralFactory
{
	SWRLAPILiteral getSWRLAPILiteral(OWLLiteral literal);

	SWRLAPILiteral getSWRLAPILiteral(String literal, OWLDatatype datatype);

	SWRLAPILiteral getSWRLAPILiteral(String value);

	SWRLAPILiteral getSWRLAPILiteral(boolean value);

	SWRLAPILiteral getSWRLAPILiteral(double value);

	SWRLAPILiteral getSWRLAPILiteral(float value);

	SWRLAPILiteral getSWRLAPILiteral(int value);

	SWRLAPILiteral getSWRLAPILiteral(byte b);

	SWRLAPILiteral getSWRLAPILiteral(short s);

	SWRLAPILiteral getSWRLAPILiteral(URI uri);

	SWRLAPILiteral getSWRLAPILiteral(XSDDate date);

	SWRLAPILiteral getSWRLAPILiteral(XSDTime time);

	SWRLAPILiteral getSWRLAPILiteral(XSDDateTime datetime);

	SWRLAPILiteral getSWRLAPILiteral(XSDDuration duration);
}
