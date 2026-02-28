/*******************************************************************************
 * Entry point of the 'Generate' generation module.
 * @generated NOT
 *******************************************************************************/
package bpmn.to.winvmj.acceleo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.acceleo.engine.event.IAcceleoTextGenerationListener;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import org.eclipse.acceleo.engine.generation.strategy.IAcceleoGenerationStrategy;
import org.eclipse.acceleo.engine.service.AbstractAcceleoGenerator;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;

public class Generate extends AbstractAcceleoGenerator {

    // ── Register everything at class-load time, BEFORE Acceleo loads the .emtl ──
    static {

        // 1. Register BPMN2 and custom package
        Bpmn2Package bpmn2Pkg = Bpmn2Package.eINSTANCE;
        EPackage.Registry.INSTANCE.put(bpmn2Pkg.getNsURI(), bpmn2Pkg);
        EPackage.Registry.INSTANCE.put(
                "platform:/plugin/org.eclipse.bpmn2/model/BPMN20.ecore",
                bpmn2Pkg
            );


        // 2. Map platform:/plugin/org.eclipse.bpmn2/ → actual jar location
        //    This is required for standalone (non-OSGi) execution so that
        //    the .emtl's href="platform:/plugin/org.eclipse.bpmn2/model/BPMN20.ecore#//"
        //    can be resolved at runtime.
        try {
            String bpmn2JarPath = findPluginJarPath(Bpmn2Package.class);
            URI platformUri  = URI.createURI("platform:/plugin/org.eclipse.bpmn2/");
            URI fileUri      = URI.createURI("jar:file:" + bpmn2JarPath + "!/");
            // If it's a directory (IDE dev mode) rather than a jar, use a plain file URI
            File jarFile = new File(bpmn2JarPath);
            if (jarFile.isDirectory()) {
                fileUri = URI.createFileURI(bpmn2JarPath + "/");
            }
            URIConverter.URI_MAP.put(platformUri, fileUri);
            System.out.println("Mapped " + platformUri + " → " + fileUri);
        } catch (Exception e) {
            System.err.println("WARNING: Could not map platform:/plugin/org.eclipse.bpmn2/ — " + e.getMessage());
        }

        // 3. Register platform:/ protocol handler so EMF can load the remapped URI
        Resource.Factory.Registry.INSTANCE
            .getProtocolToFactoryMap()
            .putIfAbsent("platform", new XMIResourceFactoryImpl());

        // Debug: print all registered packages
        System.out.println("=== ALL REGISTERED PACKAGES ===");
        EPackage.Registry.INSTANCE.forEach((uri, pkg) ->
            System.out.println("  " + uri));
        
        // Map platform:/plugin/org.eclipse.bpmn2/model/BPMN20.ecore
        // → the already-registered Bpmn2Package nsURI
        // This ensures the EClass instances in the .emtl match the runtime ones
        URIConverter.URI_MAP.put(
            URI.createURI("platform:/plugin/org.eclipse.bpmn2/model/BPMN20.ecore"),
            URI.createURI(Bpmn2Package.eINSTANCE.getNsURI())
        );

        // Also map the jar location (for other resources inside the plugin)
        try {
            String bpmn2JarPath = findPluginJarPath(Bpmn2Package.class);
            URI platformUri = URI.createURI("platform:/plugin/org.eclipse.bpmn2/");
            URI fileUri = URI.createURI("jar:file:" + bpmn2JarPath + "!/");
            File jarFile = new File(bpmn2JarPath);
            if (jarFile.isDirectory()) {
                fileUri = URI.createFileURI(bpmn2JarPath + "/");
            }
            URIConverter.URI_MAP.put(platformUri, fileUri);
        } catch (Exception e) {
            System.err.println("WARNING: Could not map platform:/plugin/ — " + e.getMessage());
        }
    }

    // ── Constants ──────────────────────────────────────────────────────────────

    public static final String MODULE_FILE_NAME = "/bpmn/to/winvmj/acceleo/Generate";
    public static final String[] TEMPLATE_NAMES = { "generate" };

    private List<String> propertiesFiles = new ArrayList<>();

    // ── Constructors ───────────────────────────────────────────────────────────

    public Generate() {}

    public Generate(URI modelURI, File targetFolder,
            List<? extends Object> arguments) throws IOException {
        initialize(modelURI, targetFolder, arguments);
    }

    /**
     * This allows clients to instantiates a generator with all required information.
     * 
     * @param model
     *            We'll iterate over the content of this element to find Objects matching the first parameter
     *            of the template we need to call.
     * @param targetFolder
     *            This will be used as the output folder for this generation : it will be the base path
     *            against which all file block URLs will be resolved.
     * @param arguments
     *            If the template which will be called requires more than one argument taken from the model,
     *            pass them here.
     * @throws IOException
     *             This can be thrown in two scenarios : the module cannot be found, or it cannot be loaded.
     * @generated
     */
    public Generate(EObject model, File targetFolder,
            List<? extends Object> arguments) throws IOException {
        initialize(model, targetFolder, arguments);
    }

    // ── Main ───────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.out.println("Usage: java Generate <bpmn2-file> <output-folder>");
                System.out.println("Example: java Generate model.bpmn2 ./output");
                return;
            }

            URI modelURI = URI.createFileURI(new File(args[0]).getAbsolutePath());
            File folder  = new File(args[1]);
            if (!folder.exists()) folder.mkdirs();

            System.out.println("Loading BPMN2 model: " + args[0]);
            System.out.println("Output folder:       " + folder.getAbsolutePath());

            Generate generator = new Generate(modelURI, folder, new ArrayList<>());

            for (int i = 2; i < args.length; i++) {
                generator.addPropertiesFile(args[i]);
            }

            generator.doGenerate(new BasicMonitor());
            System.out.println("Generation completed successfully!");

        } catch (IOException e) {
            System.err.println("IO Error:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error:");
            e.printStackTrace();
        }
    }

    // ── initialize overrides ───────────────────────────────────────────────────

    /**
     * Override initialize(URI) to load the model ourselves with BPMN2 support,
     * then delegate to initialize(EObject).
     * @generated NOT
     */
    @Override
    public void initialize(URI modelURI, File targetFolder,
            List<? extends Object> arguments) throws IOException {

        System.out.println("=== initialize(URI) ===");

        ResourceSet rs = new ResourceSetImpl();
        configureResourceSet(rs);

        Map<String, Object> loadOptions = new HashMap<>();
        loadOptions.put(XMLResource.OPTION_DEFER_ATTACHMENT, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.TRUE);

        Resource resource = rs.createResource(modelURI);
        if (resource == null) {
            throw new IOException("Could not create resource for: " + modelURI);
        }

        resource.load(loadOptions);

        if (resource.getContents().isEmpty()) {
            throw new IOException("Model is empty: " + modelURI);
        }

        EObject root = resource.getContents().get(0);
        System.out.println("Loaded model root: " + root.eClass().getName());

        // Unwrap DocumentRoot → find the Definitions inside it
        if (root instanceof org.eclipse.bpmn2.DocumentRoot) {
            org.eclipse.bpmn2.DocumentRoot docRoot = (org.eclipse.bpmn2.DocumentRoot) root;
            root = docRoot.getDefinitions();
            System.out.println("Unwrapped DocumentRoot → Definitions: " + root);
        }

        if (root == null) {
            throw new IOException("Could not find bpmn2::Definitions in model: " + modelURI);
        }

        initialize(root, targetFolder, arguments);
    }

    /**
     * Override initialize(EObject) to ensure packages are registered before
     * super.initialize() loads and compiles the .emtl module.
     * @generated NOT
     */
    @Override
    public void initialize(EObject model, File targetFolder,
            List<? extends Object> arguments) throws IOException {

        System.out.println("=== initialize(EObject) ===");
        System.out.println("BPMN2 in registry: " +
            (EPackage.Registry.INSTANCE.getEPackage(Bpmn2Package.eINSTANCE.getNsURI()) != null));

        // Re-register defensively — super.initialize() will call registerPackages()
        // but that happens AFTER module loading; we need them registered NOW.
        EPackage.Registry.INSTANCE.put(Bpmn2Package.eINSTANCE.getNsURI(), Bpmn2Package.eINSTANCE);

        super.initialize(model, targetFolder, arguments);
    }

    // ── doGenerate ─────────────────────────────────────────────────────────────

    @Override
    public void doGenerate(Monitor monitor) throws IOException {
        org.eclipse.emf.ecore.util.EcoreUtil.resolveAll(model);

        if (model != null && model.eResource() != null) {
            List<Resource.Diagnostic> errors = model.eResource().getErrors();
            if (!errors.isEmpty()) {
                System.err.println("Model errors:");
                errors.forEach(d -> System.err.println("  " + d));
                throw new IOException("Model has " + errors.size() + " error(s).");
            }
        }

        super.doGenerate(monitor);
    }

    

	/**
     * If this generator needs to listen to text generation events, listeners can be returned from here.
     * 
     * @return List of listeners that are to be notified when text is generated through this launch.
     * @generated
     */
    @Override
    public List<IAcceleoTextGenerationListener> getGenerationListeners() {
        List<IAcceleoTextGenerationListener> listeners = super.getGenerationListeners();
        /*
         * TODO if you need to listen to generation event, add listeners to the list here. If you want to change
         * the content of this method, do NOT forget to change the "@generated" tag in the Javadoc of this method
         * to "@generated NOT". Without this new tag, any compilation of the Acceleo module with the main template
         * that has caused the creation of this class will revert your modifications.
         */
        return listeners;
    }

    // ── Acceleo hooks ──────────────────────────────────────────────────────────

    @Override
    public String getModuleName() {
        return MODULE_FILE_NAME;
    }

    @Override
    public String[] getTemplateNames() {
        return TEMPLATE_NAMES;
    }

    @Override
    public List<String> getProperties() {
        return propertiesFiles;
    }

    @Override
    public void addPropertiesFile(String propertiesFile) {
        this.propertiesFiles.add(propertiesFile);
    }

    @Override
    public IAcceleoGenerationStrategy getGenerationStrategy() {
        return super.getGenerationStrategy();
    }

    /**
     * @generated NOT
     */
    @Override
    public void registerPackages(ResourceSet resourceSet) {
        super.registerPackages(resourceSet);

        EPackage.Registry global = EPackage.Registry.INSTANCE;
        global.put(Bpmn2Package.eINSTANCE.getNsURI(), Bpmn2Package.eINSTANCE);
        global.put("platform:/plugin/org.eclipse.bpmn2/model/BPMN20.ecore", Bpmn2Package.eINSTANCE);

        resourceSet.getPackageRegistry().put(Bpmn2Package.eINSTANCE.getNsURI(), Bpmn2Package.eINSTANCE);
        resourceSet.getPackageRegistry().put("platform:/plugin/org.eclipse.bpmn2/model/BPMN20.ecore", Bpmn2Package.eINSTANCE);
    }

    /**
     * @generated NOT
     */
    @Override
    public void registerResourceFactories(ResourceSet resourceSet) {
        super.registerResourceFactories(resourceSet);
        configureResourceSet(resourceSet);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Configures a ResourceSet with BPMN2 factories and package registrations.
     */
    private static void configureResourceSet(ResourceSet rs) {
        // Package registry
        rs.getPackageRegistry().put(Bpmn2Package.eINSTANCE.getNsURI(), Bpmn2Package.eINSTANCE);
        
        // Resource factories
        Map<String, Object> extMap = rs.getResourceFactoryRegistry().getExtensionToFactoryMap();
        try {
            Bpmn2ResourceFactoryImpl bpmn2Factory = new Bpmn2ResourceFactoryImpl();
            extMap.put("bpmn2", bpmn2Factory);
            extMap.put("bpmn",  bpmn2Factory);
        } catch (NoClassDefFoundError e) {
            XMLResourceFactoryImpl xmlFactory = new XMLResourceFactoryImpl();
            extMap.put("bpmn2", xmlFactory);
            extMap.put("bpmn",  xmlFactory);
            System.out.println("Fallback: using XMLResourceFactoryImpl for BPMN2 files");
        }
        extMap.put("xml",  new XMLResourceFactoryImpl());
        extMap.put("emtl", new XMIResourceFactoryImpl());

        // Protocol factories
        Map<String, Object> protoMap = rs.getResourceFactoryRegistry().getProtocolToFactoryMap();
        protoMap.putIfAbsent("platform", new XMIResourceFactoryImpl());

        // Try BPMN2 DI package
        try {
            Class<?> diClass = Class.forName("org.eclipse.bpmn2.Bpmn2DiPackage");
            EPackage diPkg = (EPackage) diClass.getField("eINSTANCE").get(null);
            EPackage.Registry.INSTANCE.put("http://www.omg.org/spec/BPMN/20100524/DI", diPkg);
            rs.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/DI", diPkg);
        } catch (Exception ignored) {}
    }

    /**
     * Finds the filesystem path to the JAR (or directory) that contains the given class.
     * Used to build the platform:/plugin/ URI mapping for standalone execution.
     */
    private static String findPluginJarPath(Class<?> clazz) throws URISyntaxException {
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        File file = new File(location.toURI());
        return file.getAbsolutePath();
    }
}