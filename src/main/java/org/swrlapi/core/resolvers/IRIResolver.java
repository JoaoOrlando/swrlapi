package org.swrlapi.core.resolvers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.swrlapi.builtins.arguments.SWRLClassBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLDataPropertyBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLNamedIndividualBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLObjectPropertyBuiltInArgument;
import org.swrlapi.exceptions.SWRLAPIInternalException;

/**
 * For simplicity, SWRL rule engine implementations will typically use the prefixed names of OWL named objects to
 * name their representation of those objects. A {@link org.swrlapi.core.SWRLAPIOntologyProcessor} will record all
 * the OWL named objects in an ontology together with their types. Rule engines can then use this class to determine
 * the type of OWL properties using their prefixed name and to map those prefixed names to and from IRIs if necessary.
 *
 * @see org.swrlapi.core.SWRLAPIOntologyProcessor
 */
public class IRIResolver
{
	private final DefaultPrefixManager prefixManager;

	private final Map<String, IRI> prefixedName2IRI;
	private final Map<IRI, String> iri2PrefixedNameCache;
	private final Set<String> variablePrefixedNames;
	private final Set<String> classPrefixedNames;
	private final Set<String> namedIndividualPrefixedNames;
	private final Set<String> objectPropertyPrefixedNames;
	private final Set<String> dataPropertyPrefixedNames;
	private final Set<String> annotationPropertyPrefixedNames;
	private final Set<String> datatypePrefixedNames;

	public IRIResolver(DefaultPrefixManager prefixManager)
	{
		this.prefixManager = prefixManager;

		this.prefixedName2IRI = new HashMap<>();
		this.iri2PrefixedNameCache = new HashMap<>();
		this.variablePrefixedNames = new HashSet<>();
		this.classPrefixedNames = new HashSet<>();
		this.namedIndividualPrefixedNames = new HashSet<>();
		this.objectPropertyPrefixedNames = new HashSet<>();
		this.dataPropertyPrefixedNames = new HashSet<>();
		this.annotationPropertyPrefixedNames = new HashSet<>();
		this.datatypePrefixedNames = new HashSet<>();
	}

	public void reset()
	{
		this.prefixedName2IRI.clear();
		this.iri2PrefixedNameCache.clear();
		this.variablePrefixedNames.clear();
		this.classPrefixedNames.clear();
		this.namedIndividualPrefixedNames.clear();
		this.objectPropertyPrefixedNames.clear();
		this.dataPropertyPrefixedNames.clear();
		this.annotationPropertyPrefixedNames.clear();
		this.datatypePrefixedNames.clear();
	}

	public String iri2PrefixedName(IRI iri)
	{
		if (this.iri2PrefixedNameCache.containsKey(iri))
			return this.iri2PrefixedNameCache.get(iri);
		else {
			String prefixedName = prefixManager.getPrefixIRI(iri);
			if (prefixedName != null)
				return prefixedName;
			else
				throw new SWRLAPIInternalException("could not find prefixed name for IRI " + iri);
		}
	}

	public IRI prefixedName2IRI(String prefixedName)
	{
		if (this.prefixedName2IRI.containsKey(prefixedName))
			return this.prefixedName2IRI.get(prefixedName);
		else {
			try {
				return prefixManager.getIRI(prefixedName);
			} catch (RuntimeException e) {
				throw new SWRLAPIInternalException("could not find IRI for prefixed name " + prefixedName);
			}
		}
	}

	public void recordSWRLVariable(SWRLVariable variable)
	{
		IRI iri = variable.getIRI();
		String variablePrefixedName = iri2PrefixedName(iri);
		this.prefixedName2IRI.put(variablePrefixedName, iri);
		this.variablePrefixedNames.add(variablePrefixedName);
	}

	public void recordOWLClass(OWLEntity cls)
	{
		IRI iri = cls.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);

		this.classPrefixedNames.add(prefixedName);
	}

	public void recordOWLNamedIndividual(OWLEntity individual)
	{
		IRI iri = individual.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);

		this.namedIndividualPrefixedNames.add(prefixedName);
	}

	public void recordOWLObjectProperty(OWLEntity property)
	{
		IRI iri = property.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);

		this.objectPropertyPrefixedNames.add(prefixedName);
	}

	public void recordOWLDataProperty(OWLEntity property)
	{
		IRI iri = property.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);

		this.dataPropertyPrefixedNames.add(prefixedName);
	}

	public void recordOWLAnnotationProperty(OWLEntity property)
	{
		IRI iri = property.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);

		this.annotationPropertyPrefixedNames.add(prefixedName);
	}

	public void recordOWLDatatype(OWLEntity datatype)
	{
		IRI iri = datatype.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);

		this.datatypePrefixedNames.add(prefixedName);
	}

	public void record(SWRLClassBuiltInArgument classArgument)
	{
		IRI iri = classArgument.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);
		this.classPrefixedNames.add(prefixedName);
	}

	public void record(SWRLNamedIndividualBuiltInArgument individualArgument)
	{
		IRI iri = individualArgument.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);
		this.namedIndividualPrefixedNames.add(prefixedName);
	}

	public void record(SWRLObjectPropertyBuiltInArgument propertyArgument)
	{
		IRI iri = propertyArgument.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);
		this.objectPropertyPrefixedNames.add(prefixedName);
	}

	public void record(SWRLDataPropertyBuiltInArgument propertyArgument)
	{
		IRI iri = propertyArgument.getIRI();
		String prefixedName = iri2PrefixedName(iri);

		recordPrefixedName2IRIMapping(prefixedName, iri);
		this.dataPropertyPrefixedNames.add(prefixedName);
	}

	public void recordPrefixedName2IRIMapping(String prefixedName, IRI iri)
	{
		if (!this.prefixedName2IRI.containsKey(prefixedName)) {
			this.prefixedName2IRI.put(prefixedName, iri);
			this.iri2PrefixedNameCache.put(iri, prefixedName);
		}
	}

	public boolean isOWLClass(String prefixedName)
	{
		return this.classPrefixedNames.contains(prefixedName);
	}

	public boolean isOWLNamedIndividual(String prefixedName)
	{
		return this.namedIndividualPrefixedNames.contains(prefixedName);
	}

	public boolean isOWLObjectProperty(String prefixedName)
	{
		return this.objectPropertyPrefixedNames.contains(prefixedName);
	}

	public boolean isOWLDataProperty(String prefixedName)
	{
		return this.dataPropertyPrefixedNames.contains(prefixedName);
	}

	public boolean isOWLAnnotationProperty(String prefixedName)
	{
		return this.annotationPropertyPrefixedNames.contains(prefixedName);
	}

	public boolean isOWLDatatype(String prefixedName)
	{
		return this.datatypePrefixedNames.contains(prefixedName);
	}
}
