package co.mindie.cindy.component.debugger;

import co.mindie.cindy.component.ComponentContext;
import co.mindie.cindy.exception.CindyException;
import co.mindie.cindy.responseserializer.JsonResponseWriter;
import me.corsin.javatools.string.Strings;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Created by simoncorsin on 23/09/14.
 */
public class DebuggerJsonGenerator {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(DebuggerJsonGenerator.class);
	private File outputFile;
	private ComponentContext componentContext;
	private int idSequence;
	private Map<Object, Integer> idByObject;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DebuggerJsonGenerator() {
		this.idByObject = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	private int getId(Object object) {
		if (this.idByObject.containsKey(object)) {
			return this.idByObject.get(object);
		}

		int id = this.idSequence++;
		this.idByObject.put(object, id);

		return id;
	}

	private ComponentContextModel createComponentContextModel(ComponentContext componentContext) {
		ComponentContextModel contextModel = new ComponentContextModel();
		contextModel.setId(this.getId(componentContext));

		if (componentContext.getOwner() != null) {
			contextModel.setOwnerId(this.getId(componentContext.getOwner()));
		}

		List<ComponentModel> components = new ArrayList<>();
		contextModel.setComponents(components);

		for (Object component : componentContext.getComponents()) {
			ComponentModel componentModel = new ComponentModel();
			componentModel.setId(this.getId(component));
			componentModel.setType(component.getClass().getName());

			components.add(componentModel);
		}

		for (ComponentContext childComponentContext : componentContext.getChildComponentContexts()) {
			ComponentContextModel childContextModel = this.createComponentContextModel(childComponentContext);

			ComponentModel ownerModel = null;

			if (childComponentContext.getOwner() != null) {
				int ownerId = this.getId(childComponentContext.getOwner());
				Object[] ownerModels = components.stream().filter(e -> e.getId() == ownerId).toArray();

				if (ownerModels != null && ownerModels.length == 1) {
					ownerModel = (ComponentModel)ownerModels[0];
				}
			}

			if (ownerModel == null) {
				if (contextModel.getIsolatedChildComponentContexts() == null) {
					contextModel.setIsolatedChildComponentContexts(new ArrayList<>());;
				}
				contextModel.getIsolatedChildComponentContexts().add(childContextModel);
			} else {
				if (ownerModel.getSubComponentContexts() == null){
					ownerModel.setSubComponentContexts(new ArrayList<>());
				}
				ownerModel.getSubComponentContexts().add(childContextModel);
			}
		}

		return contextModel;
	}

	public void generate() throws IOException {
		if (this.componentContext == null) {
			throw new CindyException("The component context to generate must be set");
		}
		if (this.outputFile == null) {
			throw new CindyException("The output file must be set");
		}
		this.idSequence = 0;
		this.idByObject.clear();

		try (OutputStream outputStream = new FileOutputStream(this.outputFile)) {
			ComponentContextModel model = this.createComponentContextModel(this.componentContext);
			JsonResponseWriter writer =  new JsonResponseWriter();
			writer.setIndentEnabled(true);
			writer.writeResponse(model, outputStream);
		}
	}

	public static void generateToTempFile(ComponentContext componentContext) throws IOException {
		String name = "cindy_debugger";

		if (componentContext.getOwner() != null) {
			name = componentContext.getOwner().getClass().getSimpleName();
		}

		File outputFile = File.createTempFile(name, ".json");

		DebuggerJsonGenerator dotGenerator = new DebuggerJsonGenerator();
		dotGenerator.setOutputFile(outputFile);
		dotGenerator.setComponentContext(componentContext);
		dotGenerator.generate();

		LOGGER.info("Generated ComponentContext Json debug file on " + outputFile);
	}

	public static void generateToTempFileSafe(ComponentContext componentContext) {
		try {
			generateToTempFile(componentContext);
		} catch (Exception e) {
			LOGGER.error("Failed to generate debug file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ComponentContext getComponentContext() {
		return componentContext;
	}

	public void setComponentContext(ComponentContext componentContext) {
		this.componentContext = componentContext;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
}
