package varbpmn.to.bpmn.transformer.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.osgi.framework.Bundle;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Import untuk EMF dan ATL
import org.eclipse.m2m.atl.core.IExtractor;
import org.eclipse.m2m.atl.core.IInjector;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFExtractor;
import org.eclipse.m2m.atl.core.emf.EMFInjector;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;

public class DeriveAction implements IObjectActionDelegate {
	private static final String PLUGIN_ID = "varbpmn.to.bpmn.transformer";
	private static final String BPMN_NS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	private static final String SPLE_NS = "http://sple/bpmn/extensions";
	private static final Set<String> MODEL_KEYWORDS = Set.of(
		"namespace", "features", "constraints", "optional", "mandatory", "alternative", "or", "and", "xor",
		"cardinality", "group", "abstract", "true", "false"
	);

	private IProject selectedProject;
	private Shell shell;

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// Mengambil "layar" aktif saat ini untuk tempat memunculkan pop-up
		this.shell = targetPart.getSite().getShell();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selectedProject = null;
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IProject) {
				this.selectedProject = (IProject) firstElement;
			} else if (firstElement instanceof IFile) {
				this.selectedProject = ((IFile) firstElement).getProject();
			}
		}
	}

	@Override
	public void run(IAction action) {
		if (selectedProject != null) {
			try {
				Path varProjectRoot = Paths.get(selectedProject.getLocation().toOSString());
				Path varSrc = varProjectRoot.resolve("src");
				Path varBpmnDir = varSrc.resolve("varbpmn");
				Path mappingDir = varSrc.resolve("feature_to_varbpmn");
				Path outDir = varProjectRoot.resolve("out");

				if (!Files.isDirectory(varBpmnDir)) {
					MessageDialog.openError(shell, "Error", "Folder varBPMN tidak ditemukan: " + varBpmnDir);
					return;
				}
				if (!Files.isDirectory(mappingDir)) {
					MessageDialog.openError(shell, "Error", "Folder mapping tidak ditemukan: " + mappingDir);
					return;
				}

				FeatureIdeInputs featureIdeInputs = selectFeatureIdeInputsWithBack(varProjectRoot.getParent());
				if (featureIdeInputs == null) {
					return;
				}

				List<String> selectedFeatures = readSelectedFeatures(featureIdeInputs.configPath);
				if (selectedFeatures.isEmpty()) {
					MessageDialog.openError(shell, "Error", "Tidak ada feature yang terpilih pada file konfigurasi.");
					return;
				}

				Set<String> modelFeatures = readModelFeatures(featureIdeInputs.modelPath);
				Set<String> abstractFeatures = readAbstractFeatures(featureIdeInputs.modelPath);

				List<String> missingFeatures = new ArrayList<>();
				List<String> selectedConcreteFeatures = new ArrayList<>();
				for (String feature : selectedFeatures) {
					if (!modelFeatures.contains(feature)) {
						missingFeatures.add(feature);
					} else if (!abstractFeatures.contains(feature)) {
						selectedConcreteFeatures.add(feature);
					}
				}
				if (!missingFeatures.isEmpty()) {
					throw new Exception("Feature di config tidak ditemukan di model: " + String.join(", ", missingFeatures));
				}
				if (selectedConcreteFeatures.isEmpty()) {
					MessageDialog.openError(shell, "Error", "Tidak ada concrete feature yang terpilih (non-abstract).");
					return;
				}

				if (!Files.exists(outDir)) {
					Files.createDirectories(outDir);
				}

				List<Path> varBpmnFiles = new ArrayList<>();
				try (Stream<Path> files = Files.list(varBpmnDir)) {
					files.filter(Files::isRegularFile)
						.filter(path -> path.getFileName().toString().toLowerCase().endsWith(".bpmn2"))
						.sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
						.forEach(varBpmnFiles::add);
				}
				if (varBpmnFiles.isEmpty()) {
					MessageDialog.openError(shell, "Error", "Tidak ada file .bpmn2 di folder: " + varBpmnDir);
					return;
				}

				int successCount = 0;
				List<String> skippedMappings = new ArrayList<>();

				List<FeatureMappingFile> allMappings = new ArrayList<>();
				if (Files.exists(mappingDir)) {
					try (Stream<Path> mappingFiles = Files.list(mappingDir)) {
						mappingFiles.filter(Files::isRegularFile)
							.filter(path -> path.getFileName().toString().toLowerCase().endsWith(".json"))
							.forEach(path -> {
								try {
									allMappings.add(parseFeatureMappingFile(path));
								} catch (Exception e) {
									e.printStackTrace();
								}
							});
					}
				}

				for (Path inputBpmnPath : varBpmnFiles) {
					String fileName = inputBpmnPath.getFileName().toString();
					String baseName = fileName.substring(0, fileName.length() - ".bpmn2".length());
					
					List<FeatureMappingFile> applicableMappings = new ArrayList<>();
					for (FeatureMappingFile fmf : allMappings) {
						if (fileName.equals(fmf.targetBPMN) && selectedConcreteFeatures.contains(fmf.feature)) {
							applicableMappings.add(fmf);
						}
					}

					if (applicableMappings.isEmpty()) {
						skippedMappings.add(fileName + " (no mappings found/selected)");
						continue;
					}

					applicableMappings.sort(Comparator.comparingInt(m -> m.priority));

					Path derivedOutputPath = outDir.resolve(baseName + ".bpmn2");

					Path tempVarBpmn = Files.createTempFile("varbpmn_", ".bpmn2");
					try {
						annotateVariability(inputBpmnPath, tempVarBpmn, applicableMappings);
						runAtlDerivation(tempVarBpmn, derivedOutputPath);
						injectItemDefinitions(inputBpmnPath, derivedOutputPath);
						successCount++;
					} finally {
						try {
							Files.deleteIfExists(tempVarBpmn);
						} catch (Exception ignore) {
						}
					}
				}

				selectedProject.refreshLocal(IResource.DEPTH_INFINITE, null);

				MessageDialog.openInformation(shell, "Transformasi Berhasil",
					"Batch derivasi selesai.\n\n"
						+ "FeatureIDE project: " + featureIdeInputs.projectRoot.getFileName() + "\n"
						+ "Config: " + featureIdeInputs.configPath.getFileName() + "\n"
						+ "Model: " + featureIdeInputs.modelPath.getFileName() + "\n"
						+ "Berhasil ditransformasi: " + successCount + " file\n"
						+ "Output folder: " + outDir + "\n"
						+ (skippedMappings.isEmpty() ? "" : ("\nMapping tidak ditemukan:\n- " + String.join("\n- ", skippedMappings))));

			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(shell, "Gagal Melakukan Transformasi",
					"Terjadi kesalahan saat proses derivasi:\n" + e.getMessage() + "\n\n"
						+ "Silakan cek Error Log untuk detail lebih lanjut.");
			}
		} else {
			MessageDialog.openError(shell, "Error", "Tidak ada project yang dipilih.");
		}
	}

	private void runAtlDerivation(Path inputVarBpmnPath, Path outputBpmnPath) throws Exception {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		URL asmURL = bundle.getEntry("transformation/DeriveVBPMN.asm");
		if (asmURL == null) {
			throw new Exception("File DeriveVBPMN.asm tidak ditemukan di dalam folder transformation/");
		}

		URL resolvedAsmURL = FileLocator.resolve(asmURL);
		String asmPath = resolvedAsmURL.getPath();

		ModelFactory factory = new EMFModelFactory();
		IInjector injector = new EMFInjector();
		IExtractor extractor = new EMFExtractor();

		IReferenceModel bpmnMetamodel = factory.newReferenceModel();
		injector.inject(bpmnMetamodel, BPMN_NS);

		IModel inModel = factory.newModel(bpmnMetamodel);
		injector.inject(inModel, inputVarBpmnPath.toUri().toString());

		IModel outModel = factory.newModel(bpmnMetamodel);

		EMFVMLauncher launcher = new EMFVMLauncher();
		launcher.initialize(Collections.<String, Object>emptyMap());

		launcher.addInModel(inModel, "IN", "VBPMN");
		launcher.addOutModel(outModel, "OUT", "BPMN");

		try (FileInputStream asmInputStream = new FileInputStream(new File(asmPath))) {
			launcher.launch(ILauncher.RUN_MODE,
				new NullProgressMonitor(),
				Collections.<String, Object>emptyMap(),
				asmInputStream);
		}

		extractor.extract(outModel, outputBpmnPath.toUri().toString());
	}

	private void injectItemDefinitions(Path inputBpmnPath, Path outputBpmnPath) throws Exception {
		String inputContent = Files.readString(inputBpmnPath, StandardCharsets.UTF_8);
		String outputContent = Files.readString(outputBpmnPath, StandardCharsets.UTF_8);
		boolean changed = false;

		// 1. Copy original itemDefinition tags exactly as they were
		List<String> originalItemDefs = new ArrayList<>();
		Matcher itemDefMatcher = Pattern.compile("<bpmn2:itemDefinition\\s+id=\"([^\"]+)\"[^>]*/>").matcher(inputContent);
		while (itemDefMatcher.find()) {
			String id = itemDefMatcher.group(1);
			String fullTag = itemDefMatcher.group(0);
			originalItemDefs.add(fullTag);
			
			// Erase any incomplete versions of this itemDefinition that EMF might have generated
			String badTagRegex = "<bpmn2:itemDefinition\\s+id=\"" + Pattern.quote(id) + "\"[^>]*/>\\s*";
			outputContent = outputContent.replaceAll(badTagRegex, "");
		}

		if (!originalItemDefs.isEmpty()) {
			StringBuilder injectStr = new StringBuilder();
			for (String tag : originalItemDefs) {
				injectStr.append("  ").append(tag).append("\n");
			}
			// Insert them right before the first <bpmn2:process
			outputContent = outputContent.replaceFirst("<bpmn2:process", Matcher.quoteReplacement(injectStr.toString()) + "<bpmn2:process");
			changed = true;
		}

		// 2. Fix property itemSubjectRef mapping
		Matcher propMatcher = Pattern.compile("<(?:bpmn2:)?property\\s+id=\"([^\"]+)\"([^>]*)/>").matcher(inputContent);
		while (propMatcher.find()) {
			String propId = propMatcher.group(1);
			String propRest = propMatcher.group(2);
			
			Matcher refMatcher = Pattern.compile("itemSubjectRef=\"([^\"]+)\"").matcher(propRest);
			if (refMatcher.find()) {
				String subjectRef = refMatcher.group(1);
				
				// Find this property in the output file
				String outPropRegex = "(<(?:bpmn2:)?property\\s+id=\"" + Pattern.quote(propId) + "\")([^>]*)/>";
				Matcher outMatcher = Pattern.compile(outPropRegex).matcher(outputContent);
				if (outMatcher.find()) {
					String outPropPrefix = outMatcher.group(1);
					String outPropRest = outMatcher.group(2);
					
					// Replace or inject itemSubjectRef correctly
					if (outPropRest.contains("itemSubjectRef=")) {
						outPropRest = outPropRest.replaceAll("itemSubjectRef=\"[^\"]+\"", "itemSubjectRef=\"" + subjectRef + "\"");
					} else {
						outPropRest = " itemSubjectRef=\"" + subjectRef + "\"" + outPropRest;
					}
					
					outputContent = outputContent.substring(0, outMatcher.start()) 
							+ outPropPrefix + outPropRest + "/>" 
							+ outputContent.substring(outMatcher.end());
					changed = true;
				}
			}
		}

		// 3. Restore missing xs namespace
		if (!outputContent.contains("xmlns:xs=")) {
			outputContent = outputContent.replaceFirst("<bpmn2:definitions", "<bpmn2:definitions xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");
			changed = true;
		}

		if (changed) {
			Files.writeString(outputBpmnPath, outputContent, StandardCharsets.UTF_8);
		}
	}

	private void annotateVariability(Path inputBpmnPath,
		Path outputBpmnPath,
		List<FeatureMappingFile> applicableMappings) throws Exception {

		Document bpmnDocument = parseXml(inputBpmnPath);
		Element definitions = bpmnDocument.getDocumentElement();
		if (definitions == null) {
			throw new Exception("Invalid BPMN document: missing root element.");
		}

		ensureNamespace(definitions, "sple", SPLE_NS);

		Set<String> allMappedIds = new HashSet<>();
		Map<String, AnnotationUpdate> updatesById = new HashMap<>();

		for (FeatureMappingFile fmf : applicableMappings) {
			for (MappingEntry entry : fmf.mappings) {
				if (isBlank(entry.id)) {
					continue;
				}
				allMappedIds.add(entry.id);

				AnnotationUpdate update = updatesById.computeIfAbsent(entry.id, key -> new AnnotationUpdate());
				if (entry.inclusionVariability != null) {
					update.inclusionVariability = entry.inclusionVariability;
				}
				if (entry.connector != null) {
					update.connector = entry.connector;
				}
				if (entry.receiver != null) {
					update.receiver = new ArrayList<>(entry.receiver);
				}
			}
		}

		for (String id : allMappedIds) {
			Element bpmnNode = findElementById(bpmnDocument, id);
			if (bpmnNode == null) {
				continue;
			}

			Element extensionElements = getOrCreateExtensionElements(bpmnDocument, bpmnNode);
			removeVariabilityAnnotations(extensionElements);

			AnnotationUpdate update = updatesById.get(id);
			if (update == null) {
				continue;
			}

			if (update.inclusionVariability != null) {
				Element inclusionElement = bpmnDocument.createElementNS(SPLE_NS, "sple:inclusionVariability");
				inclusionElement.setTextContent(update.inclusionVariability);
				extensionElements.appendChild(inclusionElement);
			}

			if (update.connector != null) {
				Element connectorElement = bpmnDocument.createElementNS(SPLE_NS, "sple:connector");
				connectorElement.setAttribute("name", safeString(update.connector.name));
				connectorElement.setAttribute("select", safeString(update.connector.select));
				extensionElements.appendChild(connectorElement);
			}

			if (update.receiver != null) {
				for (String receiverValue : update.receiver) {
					Element receiverElement = bpmnDocument.createElementNS(SPLE_NS, "sple:receiver");
					receiverElement.setTextContent(receiverValue);
					extensionElements.appendChild(receiverElement);
				}
			}
		}

		writeXml(bpmnDocument, outputBpmnPath);
		if (!Files.exists(outputBpmnPath)) {
			throw new Exception("Failed to generate BPMN file at: " + outputBpmnPath);
		}
	}

	private List<String> readSelectedFeatures(Path configPath) throws Exception {
		Document configDocument = parseXml(configPath);
		NodeList featureNodes = configDocument.getElementsByTagName("feature");
		List<String> selectedFeatures = new ArrayList<>();

		for (int i = 0; i < featureNodes.getLength(); i++) {
			Node node = featureNodes.item(i);
			if (!(node instanceof Element)) {
				continue;
			}

			Element featureElement = (Element) node;
			String name = trimToNull(featureElement.getAttribute("name"));
			if (name == null) {
				continue;
			}

			String automatic = featureElement.getAttribute("automatic");
			String manual = featureElement.getAttribute("manual");
			String selected = featureElement.getAttribute("selected");

			boolean hasExplicitUnselected = isUnselectedValue(automatic) || isUnselectedValue(manual) || isUnselectedValue(selected);
			boolean hasExplicitSelected = isSelectedValue(automatic) || isSelectedValue(manual) || isSelectedValue(selected);

			boolean isSelected = false;
			if (hasExplicitUnselected) {
				isSelected = false;
			} else if (hasExplicitSelected) {
				isSelected = true;
			}

			if (isSelected) {
				selectedFeatures.add(name);
			}
		}

		return selectedFeatures;
	}

	private Set<String> readModelFeatures(Path modelPath) throws IOException {
		String content = Files.readString(modelPath, StandardCharsets.UTF_8);
		Set<String> features = new LinkedHashSet<>();

		Matcher quotedMatcher = Pattern.compile("\"([^\"]+)\"").matcher(content);
		while (quotedMatcher.find()) {
			String featureName = trimToNull(quotedMatcher.group(1));
			if (featureName != null) {
				features.add(featureName);
			}
		}

		String withoutQuoted = content.replaceAll("\"[^\"]+\"", " ");
		Matcher tokenMatcher = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*").matcher(withoutQuoted);
		while (tokenMatcher.find()) {
			String token = tokenMatcher.group();
			if (!MODEL_KEYWORDS.contains(token.toLowerCase())) {
				features.add(token);
			}
		}

		return features;
	}

	private FeatureMappingFile parseFeatureMappingFile(Path mappingPath) throws Exception {
		String json = Files.readString(mappingPath, StandardCharsets.UTF_8);
		Object root = new JsonParser(json).parse();
		if (!(root instanceof Map)) {
			throw new Exception("Isi feature_to_var.json tidak valid: root harus object. File: " + mappingPath);
		}

		Map<?, ?> rootMap = (Map<?, ?>) root;
		FeatureMappingFile fmf = new FeatureMappingFile();
		fmf.feature = safeString(rootMap.get("feature"));
		fmf.targetBPMN = safeString(rootMap.get("targetBPMN"));
		
		Object prioObj = rootMap.get("priority");
		if (prioObj instanceof Number) {
			fmf.priority = ((Number) prioObj).intValue();
		} else if (prioObj instanceof String) {
			fmf.priority = Integer.parseInt((String) prioObj);
		} else {
			fmf.priority = 0;
		}

		fmf.mappings = new ArrayList<>();
		Object mappingsValue = rootMap.get("mappings");
		if (mappingsValue instanceof List) {
			for (Object item : (List<?>) mappingsValue) {
				if (!(item instanceof Map)) {
					continue;
				}

				Map<?, ?> itemMap = (Map<?, ?>) item;
				MappingEntry entry = new MappingEntry();
				entry.id = stringOrNull(itemMap.get("id"));
				entry.inclusionVariability = stringOrNull(itemMap.get("inclusionVariability"));

				Object connectorValue = itemMap.get("connector");
				if (connectorValue instanceof Map) {
					Map<?, ?> connectorMap = (Map<?, ?>) connectorValue;
					Connector connector = new Connector();
					connector.name = stringOrNull(connectorMap.get("name"));
					connector.select = stringOrNull(connectorMap.get("select"));
					entry.connector = connector;
				}

				Object receiverValue = itemMap.get("receiver");
				if (receiverValue instanceof List) {
					entry.receiver = new ArrayList<>();
					for (Object receiverItem : (List<?>) receiverValue) {
						entry.receiver.add(safeString(receiverItem));
					}
				}

				fmf.mappings.add(entry);
			}
		}

		return fmf;
	}

	private Set<String> readAbstractFeatures(Path modelPath) throws IOException {
		String content = Files.readString(modelPath, StandardCharsets.UTF_8);
		Set<String> abstractFeatures = new HashSet<>();
		Matcher abstractMatcher = Pattern.compile("(?i)\\babstract\\s+(?:\"([^\"]+)\"|([A-Za-z_][A-Za-z0-9_]*))").matcher(content);
		while (abstractMatcher.find()) {
			String quoted = trimToNull(abstractMatcher.group(1));
			String unquoted = trimToNull(abstractMatcher.group(2));
			if (quoted != null) {
				abstractFeatures.add(quoted);
			} else if (unquoted != null) {
				abstractFeatures.add(unquoted);
			}
		}
		return abstractFeatures;
	}

	private FeatureIdeInputs selectFeatureIdeInputsWithBack(Path preferredDirectory) {
		IProject featureProject = null;
		int step = 0;

		while (true) {
			if (step == 0) {
				featureProject = chooseFeatureIdeProjectFromWorkspace();
				if (featureProject == null) {
					return null;
				}
				step = 1;
				continue;
			}

			IFile configFile = chooseConfigFileFromWorkspace(featureProject);
			Path configPath = configFile != null ? Paths.get(configFile.getLocation().toOSString()) : null;
			if (configPath == null) {
				boolean backToProject = MessageDialog.openQuestion(
					shell,
					"Kembali ke langkah sebelumnya?",
					"Pemilihan config dibatalkan.\nPilih Yes untuk memilih ulang project FeatureIDE.\nPilih No untuk membatalkan proses."
				);
				if (backToProject) {
					step = 0;
					continue;
				}
				return null;
			}

			Path featureProjectRoot = Paths.get(featureProject.getLocation().toOSString());
			Path modelPath = Paths.get(featureProject.getFile("model.uvl").getLocation().toOSString());
			return new FeatureIdeInputs(featureProjectRoot, modelPath, configPath);
		}
	}

	private boolean isValidFeatureIdeProject(IProject project) {
		if (project == null || !project.exists()) {
			return false;
		}
		return project.getFile("model.uvl").exists() && project.getFolder("configs").exists();
	}

	private IProject chooseFeatureIdeProjectFromWorkspace() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
			shell,
			new LabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof IResource) {
						return ((IResource) element).getName();
					}
					return super.getText(element);
				}
			},
			new ResourceTreeContentProvider()
		);
		dialog.setTitle("Pilih Project FeatureIDE");
		dialog.setMessage("Pilih project workspace yang berisi model.uvl dan folder configs.");
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setAllowMultiple(false);
		dialog.setValidator(selection -> {
			if (selection.length == 1 && selection[0] instanceof IProject && isValidFeatureIdeProject((IProject) selection[0])) {
				return Status.OK_STATUS;
			}
			return new Status(IStatus.ERROR, PLUGIN_ID, "Pilih project FeatureIDE yang valid.");
		});

		if (dialog.open() != Window.OK) {
			return null;
		}
		Object result = dialog.getFirstResult();
		return result instanceof IProject ? (IProject) result : null;
	}

	private IFile chooseConfigFileFromWorkspace(IProject featureProject) {
		IFolder configsFolder = featureProject.getFolder("configs");
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
			shell,
			new LabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof IResource) {
						return ((IResource) element).getName();
					}
					return super.getText(element);
				}
			},
			new ResourceTreeContentProvider()
		);
		dialog.setTitle("Pilih Config");
		dialog.setMessage("Pilih file config (.xml) dari folder configs.");
		dialog.setInput(configsFolder);
		dialog.setAllowMultiple(false);
		dialog.setValidator(selection -> {
			if (selection.length == 1 && selection[0] instanceof IFile) {
				IFile file = (IFile) selection[0];
				String ext = file.getFileExtension();
				if (ext != null && "xml".equalsIgnoreCase(ext)) {
					return Status.OK_STATUS;
				}
			}
			return new Status(IStatus.ERROR, PLUGIN_ID, "Pilih satu file .xml.");
		});

		if (dialog.open() != Window.OK) {
			return null;
		}
		Object result = dialog.getFirstResult();
		return result instanceof IFile ? (IFile) result : null;
	}

	private Document parseXml(Path xmlPath) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder().parse(xmlPath.toFile());
	}

	private void ensureNamespace(Element element, String prefix, String namespaceUri) {
		String attributeName = "xmlns:" + prefix;
		if (!namespaceUri.equals(element.getAttribute(attributeName))) {
			element.setAttribute(attributeName, namespaceUri);
		}
	}

	private Element findElementById(Document document, String id) throws Exception {
		XPath xPath = XPathFactory.newInstance().newXPath();
		String query = "//*[@id='" + id + "']";
		Node node = (Node) xPath.evaluate(query, document, XPathConstants.NODE);
		if (node instanceof Element) {
			return (Element) node;
		}
		return null;
	}

	private Element getOrCreateExtensionElements(Document document, Element bpmnElement) {
		NodeList children = bpmnElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Element childElement = (Element) child;
				if (BPMN_NS.equals(childElement.getNamespaceURI()) && "extensionElements".equals(childElement.getLocalName())) {
					return childElement;
				}
			}
		}

		Element extensionElements = document.createElementNS(BPMN_NS, "bpmn2:extensionElements");
		Node firstChild = bpmnElement.getFirstChild();
		if (firstChild != null) {
			bpmnElement.insertBefore(extensionElements, firstChild);
		} else {
			bpmnElement.appendChild(extensionElements);
		}

		return extensionElements;
	}

	private void removeVariabilityAnnotations(Element extensionElements) {
		List<Node> toRemove = new ArrayList<>();
		NodeList children = extensionElements.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) {
				continue;
			}
			Element childElement = (Element) child;
			if (SPLE_NS.equals(childElement.getNamespaceURI())) {
				String localName = childElement.getLocalName();
				if ("inclusionVariability".equals(localName) || "connector".equals(localName) || "receiver".equals(localName)) {
					toRemove.add(child);
				}
			}
		}

		for (Node node : toRemove) {
			extensionElements.removeChild(node);
		}
	}

	private void writeXml(Document document, Path outputPath) throws Exception {
		Path parent = outputPath.getParent();
		if (parent != null && !Files.exists(parent)) {
			Files.createDirectories(parent);
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		try (OutputStream outputStream = Files.newOutputStream(outputPath)) {
			transformer.transform(new DOMSource(document), new StreamResult(outputStream));
		}
	}

	private boolean isSelectedValue(String value) {
		if (isBlank(value)) {
			return false;
		}
		String normalized = value.trim().toLowerCase();
		return "selected".equals(normalized) || "true".equals(normalized) || "1".equals(normalized) || "manual".equals(normalized);
	}

	private boolean isUnselectedValue(String value) {
		if (isBlank(value)) {
			return false;
		}
		String normalized = value.trim().toLowerCase();
		return "unselected".equals(normalized) || "false".equals(normalized) || "0".equals(normalized);
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private String safeString(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	private String stringOrNull(Object value) {
		if (value == null) {
			return null;
		}
		String str = String.valueOf(value);
		return isBlank(str) ? null : str;
	}

	private static class FeatureMappingFile {
		private String feature;
		private String targetBPMN;
		private int priority;
		private List<MappingEntry> mappings;
	}

	private static class MappingEntry {
		private String id;
		private String inclusionVariability;
		private Connector connector;
		private List<String> receiver;
	}

	private static class Connector {
		private String name;
		private String select;
	}

	private static class AnnotationUpdate {
		private String inclusionVariability;
		private Connector connector;
		private List<String> receiver;
	}

	private static class FeatureIdeInputs {
		private final Path projectRoot;
		private final Path modelPath;
		private final Path configPath;

		private FeatureIdeInputs(Path projectRoot, Path modelPath, Path configPath) {
			this.projectRoot = projectRoot;
			this.modelPath = modelPath;
			this.configPath = configPath;
		}
	}

	private static class ResourceTreeContentProvider implements ITreeContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			try {
				if (parentElement instanceof IWorkspaceRoot) {
					return ((IWorkspaceRoot) parentElement).getProjects();
				}
				if (parentElement instanceof IContainer) {
					return ((IContainer) parentElement).members();
				}
			} catch (Exception ignored) {
			}
			return new Object[0];
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof IResource) {
				return ((IResource) element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private static class JsonParser {
		private final String text;
		private int index;

		JsonParser(String text) {
			this.text = text;
			this.index = 0;
		}

		Object parse() throws Exception {
			skipWhitespace();
			Object value = parseValue();
			skipWhitespace();
			if (index != text.length()) {
				throw new Exception("Invalid JSON: trailing content at position " + index);
			}
			return value;
		}

		private Object parseValue() throws Exception {
			skipWhitespace();
			if (index >= text.length()) {
				throw new Exception("Invalid JSON: unexpected end of input");
			}

			char ch = text.charAt(index);
			if (ch == '{') {
				return parseObject();
			}
			if (ch == '[') {
				return parseArray();
			}
			if (ch == '"') {
				return parseString();
			}
			if (ch == 't') {
				expectLiteral("true");
				return Boolean.TRUE;
			}
			if (ch == 'f') {
				expectLiteral("false");
				return Boolean.FALSE;
			}
			if (ch == 'n') {
				expectLiteral("null");
				return null;
			}
			if (ch == '-' || Character.isDigit(ch)) {
				return parseNumber();
			}

			throw new Exception("Invalid JSON value at position " + index);
		}

		private Map<String, Object> parseObject() throws Exception {
			expect('{');
			skipWhitespace();

			Map<String, Object> object = new LinkedHashMap<>();
			if (peek('}')) {
				expect('}');
				return object;
			}

			while (true) {
				skipWhitespace();
				String key = parseString();
				skipWhitespace();
				expect(':');
				skipWhitespace();
				Object value = parseValue();
				object.put(key, value);

				skipWhitespace();
				if (peek('}')) {
					expect('}');
					break;
				}
				expect(',');
			}

			return object;
		}

		private List<Object> parseArray() throws Exception {
			expect('[');
			skipWhitespace();

			List<Object> array = new ArrayList<>();
			if (peek(']')) {
				expect(']');
				return array;
			}

			while (true) {
				skipWhitespace();
				array.add(parseValue());
				skipWhitespace();

				if (peek(']')) {
					expect(']');
					break;
				}
				expect(',');
			}

			return array;
		}

		private String parseString() throws Exception {
			expect('"');
			StringBuilder sb = new StringBuilder();

			while (index < text.length()) {
				char ch = text.charAt(index++);
				if (ch == '"') {
					return sb.toString();
				}
				if (ch == '\\') {
					if (index >= text.length()) {
						throw new Exception("Invalid JSON string escape at end of input");
					}
					char escaped = text.charAt(index++);
					switch (escaped) {
					case '"':
						sb.append('"');
						break;
					case '\\':
						sb.append('\\');
						break;
					case '/':
						sb.append('/');
						break;
					case 'b':
						sb.append('\b');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'n':
						sb.append('\n');
						break;
					case 'r':
						sb.append('\r');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'u':
						if (index + 4 > text.length()) {
							throw new Exception("Invalid unicode escape in JSON string");
						}
						String hex = text.substring(index, index + 4);
						index += 4;
						sb.append((char) Integer.parseInt(hex, 16));
						break;
					default:
						throw new Exception("Invalid JSON escape '\\" + escaped + "' at position " + (index - 1));
					}
				} else {
					sb.append(ch);
				}
			}

			throw new Exception("Unterminated JSON string");
		}

		private Number parseNumber() throws Exception {
			int start = index;
			if (peek('-')) {
				index++;
			}

			if (peek('0')) {
				index++;
			} else {
				consumeDigits();
			}

			if (peek('.')) {
				index++;
				consumeDigits();
			}

			if (peek('e') || peek('E')) {
				index++;
				if (peek('+') || peek('-')) {
					index++;
				}
				consumeDigits();
			}

			String numberText = text.substring(start, index);
			try {
				if (numberText.contains(".") || numberText.contains("e") || numberText.contains("E")) {
					return Double.parseDouble(numberText);
				}
				return Long.parseLong(numberText);
			} catch (NumberFormatException ex) {
				throw new Exception("Invalid JSON number: " + numberText, ex);
			}
		}

		private void consumeDigits() throws Exception {
			if (index >= text.length() || !Character.isDigit(text.charAt(index))) {
				throw new Exception("Invalid JSON number at position " + index);
			}
			while (index < text.length() && Character.isDigit(text.charAt(index))) {
				index++;
			}
		}

		private void expectLiteral(String literal) throws Exception {
			if (!text.startsWith(literal, index)) {
				throw new Exception("Expected '" + literal + "' at position " + index);
			}
			index += literal.length();
		}

		private boolean peek(char expected) {
			return index < text.length() && text.charAt(index) == expected;
		}

		private void expect(char expected) throws Exception {
			if (!peek(expected)) {
				throw new Exception("Expected '" + expected + "' at position " + index);
			}
			index++;
		}

		private void skipWhitespace() {
			while (index < text.length()) {
				char ch = text.charAt(index);
				if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
					index++;
				} else {
					break;
				}
			}
		}
	}
}
