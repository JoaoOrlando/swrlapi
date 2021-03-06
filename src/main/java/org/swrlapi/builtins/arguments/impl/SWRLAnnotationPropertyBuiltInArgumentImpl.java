package org.swrlapi.builtins.arguments.impl;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.swrlapi.builtins.arguments.SWRLAnnotationPropertyBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLBuiltInArgumentVisitor;
import org.swrlapi.builtins.arguments.SWRLBuiltInArgumentVisitorEx;

import javax.annotation.Nonnull;
import java.util.Set;

class SWRLAnnotationPropertyBuiltInArgumentImpl extends SWRLNamedBuiltInArgumentImpl implements
		SWRLAnnotationPropertyBuiltInArgument
{
	private static final long serialVersionUID = 1L;

	public SWRLAnnotationPropertyBuiltInArgumentImpl(OWLAnnotationProperty property)
	{
		super(property);
	}

	@Override
	public OWLAnnotationProperty getOWLAnnotationProperty()
	{
		return getOWLEntity().asOWLAnnotationProperty();
	}

	@Override
	public <T> T accept(SWRLBuiltInArgumentVisitorEx<T> visitor)
	{
		return visitor.visit(this);
	}

	@Override
	public void accept(SWRLBuiltInArgumentVisitor visitor)
	{
		visitor.visit(this);
	}

	@Nonnull @Override public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature()
	{
		return null; // TODO OWLAPI V4.0.0 update
	}
}
