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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
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

	private IFile selectedFile;
	private Shell shell;

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// Mengambil "layar" aktif saat ini untuk tempat memunculkan pop-up
		this.shell = targetPart.getSite().getShell();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// Logika untuk mengekstrak file yang sedang di-klik kanan
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IFile) {
				this.selectedFile = (IFile) firstElement;
			}
		}
	}

	@Override
	public void run(IAction action) {
		if (selectedFile != null) {
			try {
				if (!selectedFile.getName().toLowerCase().endsWith(".bpmn2")) {
					MessageDialog.openError(shell, "Error", "File yang dipilih harus berekstensi .bpmn2");
					return;
				}

				Path inputBpmnPath = Paths.get(selectedFile.getLocation().toOSString());
				Path baseFolder = inputBpmnPath.getParent();

				SelectedInputs inputs = selectInputsWithBack(baseFolder);
				if (inputs == null) {
					return;
				}
				File configFile = inputs.configFile;
				File modelFile = inputs.modelFile;
				File mappingFile = inputs.mappingFile;

				List<String> selectedFeatures = readSelectedFeatures(configFile.toPath());
				if (selectedFeatures.isEmpty()) {
					MessageDialog.openError(shell, "Error", "Tidak ada feature yang terpilih pada file konfigurasi.");
					return;
				}

				Set<String> modelFeatures = readModelFeatures(modelFile.toPath());
				List<String> missingFeatures = new ArrayList<>();
				for (String feature : selectedFeatures) {
					if (!modelFeatures.contains(feature)) {
						missingFeatures.add(feature);
					}
				}
				if (!missingFeatures.isEmpty()) {
					throw new Exception("Feature di config tidak ditemukan di model: " + String.join(", ", missingFeatures));
				}

				Map<String, List<MappingEntry>> featureMappings = parseFeatureMappings(mappingFile.toPath());

				// Prompt for output file
				FileDialog outDialog = new FileDialog(shell, SWT.SAVE);
				outDialog.setText("Pilih Lokasi Output BPMN");
				outDialog.setFilterExtensions(new String[] { "*.bpmn2", "*.*" });
				outDialog.setFileName(selectedFile.getName().replace(".bpmn2", "_derived.bpmn2"));
				if (baseFolder != null && Files.exists(baseFolder)) {
					outDialog.setFilterPath(baseFolder.toAbsolutePath().toString());
				}
				String outputPathStr = outDialog.open();
				if (outputPathStr == null || outputPathStr.trim().isEmpty()) {
					return;
				}
				Path derivedOutputPath = Paths.get(outputPathStr);

				// Use a temp file for varbpmn
				Path tempVarBpmn = Files.createTempFile("varbpmn_", ".bpmn2");
				try {
					annotateVariability(inputBpmnPath, tempVarBpmn, selectedFeatures, featureMappings);
					runAtlDerivation(tempVarBpmn, derivedOutputPath);
				} finally {
					try { Files.deleteIfExists(tempVarBpmn); } catch (Exception ignore) {}
				}

				selectedFile.getParent().refreshLocal(IResource.DEPTH_ONE, null);

				MessageDialog.openInformation(shell, "Transformasi Berhasil",
					"Derivasi sukses dieksekusi.\n\n"
						+ "Config: " + configFile.getName() + "\n"
						+ "Model: " + modelFile.getName() + "\n"
						+ "Mapping: " + mappingFile.getName() + "\n\n"
						+ "BPMN variant: " + derivedOutputPath.getFileName());

			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(shell, "Gagal Melakukan Transformasi",
					"Terjadi kesalahan saat proses derivasi:\n" + e.getMessage() + "\n\n"
						+ "Silakan cek Error Log untuk detail lebih lanjut.");
			}
		} else {
			MessageDialog.openError(shell, "Error", "Tidak ada file yang dipilih.");
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

	private void annotateVariability(Path inputBpmnPath,
		Path outputBpmnPath,
		List<String> selectedFeatures,
		Map<String, List<MappingEntry>> featureMappings) throws Exception {

		Document bpmnDocument = parseXml(inputBpmnPath);
		Element definitions = bpmnDocument.getDocumentElement();
		if (definitions == null) {
			throw new Exception("Invalid BPMN document: missing root element.");
		}

		ensureNamespace(definitions, "sple", SPLE_NS);

		Set<String> allMappedIds = new HashSet<>();
		Map<String, AnnotationUpdate> updatesById = new HashMap<>();

		for (List<MappingEntry> entries : featureMappings.values()) {
			for (MappingEntry entry : entries) {
				if (!isBlank(entry.id)) {
					allMappedIds.add(entry.id);
				}
			}
		}

		for (String feature : selectedFeatures) {
			List<MappingEntry> entries = featureMappings.get(feature);
			if (entries == null) {
				continue;
			}

			for (MappingEntry entry : entries) {
				if (isBlank(entry.id)) {
					continue;
				}

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

	private Map<String, List<MappingEntry>> parseFeatureMappings(Path mappingPath) throws Exception {
		String json = Files.readString(mappingPath, StandardCharsets.UTF_8);
		Object root = new JsonParser(json).parse();
		if (!(root instanceof Map)) {
			throw new Exception("Isi feature_to_var.json tidak valid: root harus object.");
		}

		Map<String, List<MappingEntry>> result = new LinkedHashMap<>();
		Map<?, ?> rootMap = (Map<?, ?>) root;

		for (Map.Entry<?, ?> featureEntry : rootMap.entrySet()) {
			String featureName = safeString(featureEntry.getKey());
			Object value = featureEntry.getValue();
			if (!(value instanceof List)) {
				continue;
			}

			List<MappingEntry> mappings = new ArrayList<>();
			for (Object item : (List<?>) value) {
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

				mappings.add(entry);
			}

			result.put(featureName, mappings);
		}

		return result;
	}

	private File resolveMappingFile(File configFile) {
		File configDirectory = configFile.getParentFile();
		if (configDirectory == null) {
			return null;
		}

		File sibling = new File(configDirectory, "feature_to_var.json");
		if (sibling.exists() && sibling.isFile()) {
			return sibling;
		}

		return null;
	}

	private File askUserFile(String title, String[] filterExtensions, Path preferredDirectory) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText(title);
		dialog.setFilterExtensions(filterExtensions);

		if (preferredDirectory != null && Files.exists(preferredDirectory)) {
			dialog.setFilterPath(preferredDirectory.toAbsolutePath().toString());
		}

		String selectedPath = dialog.open();
		if (selectedPath == null || selectedPath.trim().isEmpty()) {
			return null;
		}

		return new File(selectedPath);
	}

	private SelectedInputs selectInputsWithBack(Path baseFolder) {
		File configFile = null;
		File modelFile = null;
		Path preferredDirectory = baseFolder;
		int step = 0;

		while (true) {
			if (step == 0) {
				configFile = askUserFile("Pilih File Konfigurasi FeatureIDE", new String[] { "*.xml", "*.*" }, preferredDirectory);
				if (configFile == null) {
					return null;
				}
				preferredDirectory = configFile.toPath().getParent();
				step = 1;
				continue;
			}

			if (step == 1) {
				modelFile = askUserFile("Pilih File Model Feature (.uvl)", new String[] { "*.uvl", "*.*" }, preferredDirectory);
				if (modelFile == null) {
					boolean backToConfig = MessageDialog.openQuestion(
						shell,
						"Kembali ke langkah sebelumnya?",
						"Pemilihan model dibatalkan.\nPilih Yes untuk kembali memilih file konfigurasi.\nPilih No untuk membatalkan proses."
					);
					if (backToConfig) {
						step = 0;
						preferredDirectory = configFile != null ? configFile.toPath().getParent() : baseFolder;
						continue;
					}
					return null;
				}
				preferredDirectory = modelFile.toPath().getParent();
				step = 2;
				continue;
			}

			File mappingFile = askUserFile("Pilih File Mapping feature_to_var.json", new String[] { "*.json", "*.*" }, preferredDirectory);
			if (mappingFile == null) {
				boolean backToModel = MessageDialog.openQuestion(
					shell,
					"Kembali ke langkah sebelumnya?",
					"Pemilihan mapping dibatalkan.\nPilih Yes untuk kembali memilih file model.\nPilih No untuk membatalkan proses."
				);
				if (backToModel) {
					step = 1;
					preferredDirectory = modelFile != null ? modelFile.toPath().getParent() : baseFolder;
					continue;
				}
				return null;
			}

			return new SelectedInputs(configFile, modelFile, mappingFile);
		}
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

	private static class SelectedInputs {
		private final File configFile;
		private final File modelFile;
		private final File mappingFile;

		private SelectedInputs(File configFile, File modelFile, File mappingFile) {
			this.configFile = configFile;
			this.modelFile = modelFile;
			this.mappingFile = mappingFile;
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
