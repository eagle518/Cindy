package co.mindie.cindy.core.component.debugger;

import co.mindie.cindy.core.component.box.ComponentBox;
import co.mindie.cindy.core.exception.CindyException;
import co.mindie.cindy.webservice.responsewriter.JsonResponseWriter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by simoncorsin on 23/09/14.
 */
public class DebuggerJsonGenerator {

	////////////////////////
	// VARIABLES
	////////////////

	private static final Logger LOGGER = Logger.getLogger(DebuggerJsonGenerator.class);
	private File outputFile;
	private ComponentBox componentBox;
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

	private ComponentContextModel createComponentContextModel(ComponentBox componentBox) {
		ComponentContextModel contextModel = new ComponentContextModel();
		contextModel.setId(this.getId(componentBox));

		if (componentBox.getOwner() != null) {
			contextModel.setOwnerId(this.getId(componentBox.getOwner()));
		}

		List<ComponentModel> components = new ArrayList<>();
		contextModel.setComponents(components);

		for (Object component : componentBox.getComponents()) {
			ComponentModel componentModel = new ComponentModel();
			componentModel.setId(this.getId(component));
			componentModel.setType(component.getClass().getName());
			componentModel.setHashCode(component.hashCode());

			components.add(componentModel);
		}

		for (ComponentBox childComponentBox : componentBox.getChildComponentBoxes()) {
			ComponentContextModel childContextModel = this.createComponentContextModel(childComponentBox);

			ComponentModel ownerModel = null;

			if (childComponentBox.getOwner() != null) {
				int ownerId = this.getId(childComponentBox.getOwner());
				Object[] ownerModels = components.stream().filter(e -> e.getId() == ownerId).toArray();

				if (ownerModels != null && ownerModels.length == 1) {
					ownerModel = (ComponentModel) ownerModels[0];
				}
			}

			if (ownerModel == null) {
				if (contextModel.getIsolatedChildComponentContexts() == null) {
					contextModel.setIsolatedChildComponentContexts(new ArrayList<>());
				}
				contextModel.getIsolatedChildComponentContexts().add(childContextModel);
			} else {
				if (ownerModel.getSubComponentContexts() == null) {
					ownerModel.setSubComponentContexts(new ArrayList<>());
				}
				ownerModel.getSubComponentContexts().add(childContextModel);
			}
		}

		return contextModel;
	}

	public void generate() throws IOException {
		if (this.componentBox == null) {
			throw new CindyException("The component context to generate must be set");
		}
		if (this.outputFile == null) {
			throw new CindyException("The output file must be set");
		}
		this.idSequence = 0;
		this.idByObject.clear();

		try (OutputStream outputStream = new FileOutputStream(this.outputFile)) {
			ComponentContextModel model = this.createComponentContextModel(this.componentBox);
			JsonResponseWriter writer = new JsonResponseWriter();
			writer.setIndentEnabled(true);
			writer.writeResponse(model, outputStream);
		}
	}

	public static void generateToTempFile(ComponentBox componentBox) throws IOException {
		String name = "cindy_debugger";

		if (componentBox.getOwner() != null) {
			name = componentBox.getOwner().getClass().getSimpleName();
		}

		File outputFile = File.createTempFile(name, ".json");

		DebuggerJsonGenerator dotGenerator = new DebuggerJsonGenerator();
		dotGenerator.setOutputFile(outputFile);
		dotGenerator.setComponentBox(componentBox);
		dotGenerator.generate();

		LOGGER.info("Generated ComponentBox Json debug file on " + outputFile);
	}

	public static void generateToTempFileSafe(ComponentBox componentBox) {
		try {
			generateToTempFile(componentBox);
		} catch (Exception e) {
			LOGGER.error("Failed to generate debug file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ComponentBox getComponentBox() {
		return componentBox;
	}

	public void setComponentBox(ComponentBox componentBox) {
		this.componentBox = componentBox;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
}
