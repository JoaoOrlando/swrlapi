package org.swrlapi.core;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.swrlapi.builtins.arguments.SWRLBuiltInArgumentFactory;
import org.swrlapi.builtins.arguments.impl.DefaultSWRLBuiltInArgumentFactory;
import org.swrlapi.core.impl.DefaultOWLLiteralFactory;
import org.swrlapi.core.impl.DefaultSWRLAPILiteralFactory;
import org.swrlapi.core.impl.DefaultSWRLAPIOWLDataFactory;
import org.swrlapi.core.impl.DefaultSWRLAPIOWLDatatypeFactory;
import org.swrlapi.core.impl.DefaultSWRLAPIOWLOntology;
import org.swrlapi.core.impl.DefaultSWRLAPIOntologyProcessor;
import org.swrlapi.core.impl.DefaultSWRLAPIRenderer;
import org.swrlapi.core.impl.DefaultSWRLRuleEngineFactory;
import org.swrlapi.core.resolvers.IRIResolver;
import org.swrlapi.exceptions.SWRLAPIException;
import org.swrlapi.parser.SWRLParser;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.values.SQWRLResultValueFactory;
import org.swrlapi.sqwrl.values.impl.DefaultSQWRLResultValueFactory;
import org.swrlapi.ui.controller.SWRLAPIApplicationController;
import org.swrlapi.ui.dialog.SWRLAPIApplicationDialogManager;
import org.swrlapi.ui.model.SWRLAPIApplicationModel;
import org.swrlapi.ui.model.SWRLRulesTableModel;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for generating some of the core entities defined by the SWRLAPI.
 *
 * @see org.swrlapi.core.SWRLAPIOWLOntology
 * @see org.swrlapi.core.SWRLAPIOntologyProcessor
 * @see org.swrlapi.core.SWRLRuleEngineFactory
 * @see org.swrlapi.sqwrl.values.SQWRLResultValueFactory
 * @see org.swrlapi.core.SWRLAPIOWLDataFactory
 * @see org.swrlapi.core.SWRLAPIOWLDatatypeFactory
 */
public class SWRLAPIFactory
{
	private static final String SQWRL_ICON_NAME = "SQWRL.gif";
	private static final String OWL2RL_ICON_NAME = "OWL2RL.gif";

	public static SWRLAPIOWLOntology createOntology(OWLOntology ontology)
	{
		DefaultPrefixManager prefixManager = createPrefixManager(ontology);

		addSWRLAPIBuiltInOntologies(ontology.getOWLOntologyManager());

		return new DefaultSWRLAPIOWLOntology(ontology, prefixManager);
	}

	/**
	 * Create a {@link org.swrlapi.core.SWRLAPIOWLOntology} from an OWLAPI-based
	 * {@link org.semanticweb.owlapi.model.OWLOntology}.
	 */
	public static SWRLAPIOWLOntology createOntology(OWLOntology ontology, DefaultPrefixManager prefixManager)
	{
		if (ontology == null)
			throw new SWRLAPIException("supplied OWL ontology is null");

		if (prefixManager == null)
			throw new SWRLAPIException("suppliedprefix manager is null");

		return new DefaultSWRLAPIOWLOntology(ontology, prefixManager);
	}

	/**
	 * Create a {@link org.swrlapi.core.SWRLAPIOWLOntology} from a file.
	 */
	public static SWRLAPIOWLOntology createOntology(File owlFile)
	{
		OWLOntologyManager ontologyManager = createOWLOntologyManager();

		if (owlFile == null)
			throw new SWRLAPIException("supplied OWL file is null");

		OWLOntology ontology = createOWLOntology(ontologyManager, owlFile);

		if (ontology == null)
			throw new SWRLAPIException("failed to create ontology from file " + owlFile.getAbsolutePath());

		DefaultPrefixManager prefixManager = createPrefixManager(ontology);

		return SWRLAPIFactory.createOntology(ontology, prefixManager);
	}

	/**
	 * Create an empty {@link org.swrlapi.core.SWRLAPIOWLOntology}.
	 */
	public static SWRLAPIOWLOntology createOntology()
	{
		try {
			OWLOntologyManager ontologyManager = createOWLOntologyManager();
			OWLOntology ontology = ontologyManager.createOntology();
			DefaultPrefixManager prefixManager = createPrefixManager(ontology);

			return SWRLAPIFactory.createOntology(ontology, prefixManager);
		} catch (OWLOntologyCreationException e) {
			throw new SWRLAPIException("Error creating OWL ontology", e);
		}
	}

	public static DefaultPrefixManager createPrefixManager(OWLOntology ontology)
	{
		if (ontology == null)
			throw new SWRLAPIException("supplied OWL ontology is null");

		DefaultPrefixManager prefixManager = new DefaultPrefixManager();

		updatePrefixManager(ontology, prefixManager);

		return prefixManager;
	}

	public static void updatePrefixManager(OWLOntology ontology, DefaultPrefixManager prefixManager)
	{
		OWLOntologyManager owlOntologyManager = ontology.getOWLOntologyManager();
		OWLDocumentFormat ontologyFormat = owlOntologyManager.getOntologyFormat(ontology);

		if (ontologyFormat.isPrefixOWLOntologyFormat()) {
			PrefixDocumentFormat prefixOntologyFormat = ontologyFormat.asPrefixOWLOntologyFormat();
			String defaultPrefix = prefixOntologyFormat.getDefaultPrefix();

			Map<String, String> map = prefixOntologyFormat.getPrefixName2PrefixMap();
			for (String prefix : map.keySet())
				prefixManager.setPrefix(prefix, map.get(prefix));

			// TODO Look at this. Seems dodgy.
			if (defaultPrefix != null)
				prefixManager.setDefaultPrefix(defaultPrefix);
			else
				prefixManager.setDefaultPrefix(":");

		}
		addSWRLAPIPrefixes(prefixManager);
	}

	public static SWRLAPIApplicationModel createApplicationModel(SWRLAPIOWLOntology swrlapiOWLOntology,
			SWRLRuleEngine ruleEngine)
	{
		return new SWRLAPIApplicationModel(swrlapiOWLOntology, ruleEngine);
	}

	public static SWRLAPIApplicationController createApplicationController(SWRLAPIApplicationModel applicationModel)
	{
		return new SWRLAPIApplicationController(applicationModel);
	}

	public static SWRLAPIApplicationDialogManager createApplicationDialogManager(SWRLAPIApplicationModel applicationModel)
	{
		return new SWRLAPIApplicationDialogManager(applicationModel);
	}

	public static SWRLAPIOntologyProcessor createOntologyProcessor(SWRLAPIOWLOntology swrlapiOWLOntology)
	{
		return new DefaultSWRLAPIOntologyProcessor(swrlapiOWLOntology);
	}

	public static SWRLRuleEngineFactory createSWRLRuleEngineFactory()
	{
		return new DefaultSWRLRuleEngineFactory();
	}

	public static SWRLParser createSWRLParser(SWRLAPIOWLOntology swrlapiOWLOntology)
	{
		return new SWRLParser(swrlapiOWLOntology);
	}

	public static DefaultSWRLAPIRenderer createSWRLAPIRenderer(SWRLAPIOWLOntology swrlapiOWLOntology)
	{
		return new DefaultSWRLAPIRenderer(swrlapiOWLOntology);
	}

	public static SWRLRulesTableModel createSWRLRulesTableModel(SWRLRuleEngine swrlRuleEngine,
			DefaultSWRLAPIRenderer swrlRuleRenderer)
	{
		return new SWRLRulesTableModel(swrlRuleEngine, swrlRuleRenderer);
	}

	public static SWRLAPIOWLDataFactory createSWRLAPIOWLDataFactory(IRIResolver iriResolver)
	{
		return new DefaultSWRLAPIOWLDataFactory(iriResolver);
	}

	public static SWRLAPIOWLDatatypeFactory createSWRLAPIOWLDatatypeFactory()
	{
		return new DefaultSWRLAPIOWLDatatypeFactory();
	}

	public static OWLLiteralFactory createOWLLiteralFactory()
	{
		return new DefaultOWLLiteralFactory();
	}

	public static SWRLAPILiteralFactory createSWRLAPILiteralFactory()
	{
		return new DefaultSWRLAPILiteralFactory();
	}

	public static SWRLBuiltInArgumentFactory createSWRLBuiltInArgumentFactory(IRIResolver iriResolver)
	{
		return new DefaultSWRLBuiltInArgumentFactory(iriResolver);
	}

	public static SQWRLResultValueFactory createSQWRLResultValueFactory(IRIResolver iriResolver)
	{
		return new DefaultSQWRLResultValueFactory(iriResolver);
	}

	public static SQWRLQueryEngine createSQWRLQueryEngine(SWRLAPIOWLOntology swrlapiOWLOntology,
			SWRLRuleEngineManager.TargetSWRLRuleEngineCreator swrlRuleEngineCreator)
	{
		SWRLRuleEngineFactory swrlRuleEngineFactory = SWRLAPIFactory.createSWRLRuleEngineFactory();
		swrlRuleEngineFactory.registerRuleEngine(swrlRuleEngineCreator);

		return swrlRuleEngineFactory.createSWRLRuleEngine(swrlapiOWLOntology);
	}

	public static SWRLRuleEngine createSWRLRuleEngine(SWRLAPIOWLOntology swrlapiOWLOntology,
			SWRLRuleEngineManager.TargetSWRLRuleEngineCreator swrlRuleEngineCreator)
	{
		SWRLRuleEngineFactory swrlRuleEngineFactory = SWRLAPIFactory.createSWRLRuleEngineFactory();
		swrlRuleEngineFactory.registerRuleEngine(swrlRuleEngineCreator);

		return swrlRuleEngineFactory.createSWRLRuleEngine(swrlapiOWLOntology);
	}

	public static Icon getOWL2RLReasonerIcon() throws SWRLAPIException
	{
		URL url = SWRLAPIFactory.class.getResource(OWL2RL_ICON_NAME);

		if (url != null)
			return new ImageIcon(url);
		else
			throw new SWRLAPIException("No OWL 2 RL icon found!");
	}

	public static Icon getSQWRLIcon() throws SWRLAPIException
	{
		URL url = SWRLAPIFactory.class.getResource(SQWRL_ICON_NAME);

		if (url != null)
			return new ImageIcon(url);
		else
			throw new SWRLAPIException("No SQWRL icon found!");
	}

	public static OWLOntologyManager createOWLOntologyManager()
	{
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

		addSWRLAPIBuiltInOntologies(ontologyManager);

		return ontologyManager;
	}

	private static void addSWRLAPIPrefixes(DefaultPrefixManager prefixManager)
	{
		prefixManager.setPrefix("swrl:", "http://www.w3.org/2003/11/swrl#");
		prefixManager.setPrefix("swrlb:", "http://www.w3.org/2003/11/swrlb#");
		prefixManager.setPrefix("sqwrl:", "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl#");
		prefixManager.setPrefix("swrlm:", "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlm.owl#");
		prefixManager.setPrefix("temporal:", "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl#");
		prefixManager.setPrefix("swrlx:", "http://swrl.stanford.edu/ontologies/built-ins/3.3/swrlx.owl#");
		prefixManager.setPrefix("swrla:", "http://swrl.stanford.edu/ontologies/3.3/swrla.owl#");
	}

	private static void addSWRLAPIBuiltInOntologies(OWLOntologyManager ontologyManager)
	{
		Map<String, String> map = new HashMap<>();

		map.put("http://www.w3.org/2003/11/swrl#", resourceName2File("owl/swrl.owl"));
		map.put("http://www.w3.org/2003/11/swrlb#", resourceName2File("owl/swrlb.owl"));
		map.put("http://swrl.stanford.edu/ontologies/3.3/swrla.owl", resourceName2File("owl/swrla.owl"));
		map.put("http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlm.owl", resourceName2File("owl/swrlm.owl"));
		map.put("http://swrl.stanford.edu/ontologies/built-ins/3.3/swrlx.owl", resourceName2File("owl/swrlx.owl"));
		map.put("http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl", resourceName2File("owl/temporal.owl"));
		map.put("http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl", resourceName2File("owl/sqwrl.owl"));

		for (String key : map.keySet())
			ontologyManager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(key), IRI.create(map.get(key))));
	}

	private static OWLOntology createOWLOntology(OWLOntologyManager ontologyManager, File file) throws SWRLAPIException
	{
		try {
			return ontologyManager.loadOntologyFromOntologyDocument(file);
		} catch (OWLOntologyCreationException e) {
			throw new SWRLAPIException(
					"Error create OWL ontology from file " + file.getAbsolutePath() + ": " + e.getMessage());
		}
	}

	private static String resourceName2File(String resourceName)
	{
		URL url = SWRLAPIFactory.class.getClassLoader().getResource(resourceName);
		if (url == null)
			throw new SWRLAPIException("Could not find resource " + resourceName);
		return "file:///" + url.getFile();
	}
}
