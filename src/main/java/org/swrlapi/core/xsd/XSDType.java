package org.swrlapi.core.xsd;

import org.semanticweb.owlapi.model.IRI;

/**
 * Base class the the small set of XSD types provided by the SWRLAPI.
 */
public abstract class XSDType<T> implements Comparable<T>
{
	private final String content;
	private IRI iri = null;

	public XSDType(String content)
	{
		this.content = content;
		validate();
	}

	public String getContent()
	{
		return this.content;
	}

	@Override
	public String toString()
	{
		return this.content;
	}

	public IRI getIRI()
	{
		return this.iri;
	}

	protected abstract void validate();

	protected void setURI(IRI iri)
	{
		this.iri = iri;
	}
}
