package org.swrlapi.sqwrl.values.impl;

import java.util.Comparator;

import org.semanticweb.owlapi.model.OWLLiteral;
import org.swrlapi.core.OWLLiteralComparator;
import org.swrlapi.core.impl.DefaultSWRLAPILiteral;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.SQWRLAnnotationPropertyResultValue;
import org.swrlapi.sqwrl.values.SQWRLClassResultValue;
import org.swrlapi.sqwrl.values.SQWRLDataPropertyResultValue;
import org.swrlapi.sqwrl.values.SQWRLIndividualResultValue;
import org.swrlapi.sqwrl.values.SQWRLLiteralResultValue;
import org.swrlapi.sqwrl.values.SQWRLEntityResultValue;
import org.swrlapi.sqwrl.values.SQWRLObjectPropertyResultValue;

/**
 * Implementation of a data value object that represents Java and XML Schema primitive data literals.
 */
class SQWRLLiteralResultValueImpl extends DefaultSWRLAPILiteral implements SQWRLLiteralResultValue
{
	private static Comparator<OWLLiteral> owlLiteralComparator = OWLLiteralComparator.COMPARATOR;

	public SQWRLLiteralResultValueImpl(OWLLiteral literal)
	{
		super(literal);
	}

	@Override
	public boolean isEntity() { return false; }

	@Override
	public boolean isClass() { return false; }

	@Override
	public boolean isIndividual() { return false; }

	@Override
	public boolean isObjectProperty() { return false; }

	@Override
	public boolean isDataProperty() { return false; }

	@Override
	public boolean isAnnotationProperty() { return false; }

	@Override
	public boolean isLiteral()
	{
		return true;
	}

	@Override
	public SQWRLClassResultValue asEntityResult() throws SQWRLException
	{
		throw new SQWRLException(getClass().getName() + " is not a " + SQWRLEntityResultValue.class.getName());
	}

	@Override
	public SQWRLClassResultValue asClassResult() throws SQWRLException
	{
		throw new SQWRLException(getClass().getName() + " is not a " + SQWRLClassResultValue.class.getName());
	}

	@Override
	public SQWRLIndividualResultValue asIndividualResult() throws SQWRLException
	{
		throw new SQWRLException(getClass().getName() + " is not a " + SQWRLIndividualResultValue.class.getName());
	}

	@Override
	public SQWRLObjectPropertyResultValue asObjectPropertyResult() throws SQWRLException
	{
		throw new SQWRLException(getClass().getName() + " is not an " + SQWRLObjectPropertyResultValue.class.getName());
	}

	@Override
	public SQWRLDataPropertyResultValue asDataPropertyResult() throws SQWRLException
	{
		throw new SQWRLException(getClass().getName() + " is not an " + SQWRLDataPropertyResultValue.class.getName());
	}

	@Override
	public SQWRLAnnotationPropertyResultValue asAnnotationPropertyResult() throws SQWRLException
	{
		throw new SQWRLException(getClass().getName() + " is not an " + SQWRLAnnotationPropertyResultValue.class.getName());
	}

	@Override
	public SQWRLLiteralResultValue asLiteralResult()
	{
		return this;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		SQWRLLiteralResultValueImpl l = (SQWRLLiteralResultValueImpl)obj;

		return owlLiteralComparator.compare(this.getOWLLiteral(), l.getOWLLiteral()) == 0;
	}

	@Override
	public int hashCode()
	{
		int hash = 98;
		hash = hash + (null == this.getOWLLiteral() ? 0 : this.getOWLLiteral().hashCode());
		return hash;
	}

	@Override
	public int compareTo(SQWRLLiteralResultValue o)
	{
		return owlLiteralComparator.compare(this.getOWLLiteral(), o.getOWLLiteral());
	}
}
