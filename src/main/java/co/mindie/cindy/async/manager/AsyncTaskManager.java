package co.mindie.cindy.async.manager;

import co.mindie.cindy.async.annotation.Async;
import co.mindie.cindy.async.task.AsyncResult;
import co.mindie.cindy.async.task.ConcurrentAsyncTaskManager;
import co.mindie.cindy.async.task.PeriodicAsyncTaskManager;
import co.mindie.cindy.async.task.SequentialAsyncTaskManager;
import co.mindie.cindy.async.task.queue.TaskQueue;
import co.mindie.cindy.core.component.CreationBox;
import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.core.annotation.MetadataModifier;
import co.mindie.cindy.core.component.SearchScope;
import co.mindie.cindy.core.component.*;
import co.mindie.cindy.core.component.metadata.ComponentDependency;
import co.mindie.cindy.core.component.metadata.ComponentMetadata;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManagerBuilder;
import co.mindie.cindy.core.component.metadata.Wire;
import co.mindie.cindy.core.exception.CindyException;
import co.mindie.cindy.core.tools.Initializable;
import javassist.*;

import java.io.Closeable;
import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Load
public abstract class AsyncTaskManager implements Initializable, Closeable {

	////////////////////////
	// VARIABLES
	////////////////

	private TaskQueue taskQueue;

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	protected abstract TaskQueue createTaskQueue();

	@Override
	public void close() {
		if (this.taskQueue != null) {
			this.taskQueue.close();
			this.taskQueue = null;
		}
	}

	@Override
	public void init() {
		this.taskQueue = this.createTaskQueue();
	}

	public void enqueueTask(Runnable runnable) {
		this.taskQueue.executeAsync(runnable);
	}

	private static Class<?> makeAsyncType(Class<?> componentClass, String newClassName, List<Method> asyncMethods) throws Exception {
		ClassPool pool = ClassPool.getDefault();

		CtClass componentMetadataCls = pool.get(componentClass.getName());

		CtClass cls = pool.makeClass(newClassName, componentMetadataCls);

		int idx = 0;
		for (Method method : asyncMethods) {
			if (Modifier.isPrivate(method.getModifiers())) {
				throw new CindyException("Cannot create ASync method impl on " + method + ": Only protected or public methods" +
						" are supported");
			}
			String asyncTaskManagerPropertyName = "__asyncTaskManager_" + idx;
			String asyncRunnerPropertyName = "__asyncRunner_" + idx;
			cls.addField(CtField.make("private " + AsyncTaskManager.class.getName() + " " + asyncTaskManagerPropertyName + ";", cls));
			cls.addField(CtField.make("public " + AsyncRunner.class.getName() + " " + asyncRunnerPropertyName + ";", cls));

			StringBuilder methodImpl = new StringBuilder();
			String modifier = java.lang.reflect.Modifier.isPublic(method.getModifiers()) ? "public" : "protected";
			methodImpl.append(modifier + " ")
					.append(method.getReturnType().getName())
					.append(" ")
					.append(method.getName())
					.append("(")
			;

			boolean isFirst = true;
			for (Parameter parameter : method.getParameters()) {
				if (!isFirst) {
					methodImpl.append(", ");
				}
				methodImpl
						.append(parameter.getType().getName())
						.append(" ")
						.append(parameter.getName());

				isFirst = false;
			}

			methodImpl.append(") {\n");

			String paramsObjectName = "__parametersToReturn";

			methodImpl.append("  Object[] ");
			methodImpl.append(paramsObjectName);
			methodImpl.append(" = new Object[");
			methodImpl.append(method.getParameterCount());
			methodImpl.append("];\n");

			for (int i = 0; i < method.getParameterCount(); i++) {
				methodImpl.append("  ");
				methodImpl.append(paramsObjectName);
				methodImpl.append("[");
				methodImpl.append(i);
				methodImpl.append("] = ");

				Parameter param = method.getParameters()[i];

				if (param.getType() == int.class) {
					methodImpl.append("new java.lang.Integer(");
					methodImpl.append(param.getName());
					methodImpl.append(")");
				} else if (param.getType() == char.class) {
					methodImpl.append("new java.lang.Character(");
					methodImpl.append(param.getName());
					methodImpl.append(")");
				} else if (param.getType() == double.class || param.getType() == float.class ||
						param.getType() == long.class || param.getType() == boolean.class || param.getType() == short.class) {
					methodImpl.append("new java.lang.");
					methodImpl.append(Character.toUpperCase(param.getType().getName().charAt(0)));
					methodImpl.append(param.getType().getName().substring(1));
					methodImpl.append("(");
					methodImpl.append(param.getName());
					methodImpl.append(")");
				} else {
					methodImpl.append(param.getName());
				}
				methodImpl.append(";\n");
			}

			if (method.getReturnType() == void.class) {
				methodImpl.append("  ");
			} else {
				if (!method.getReturnType().isAssignableFrom(AsyncResult.class)) {
					throw new CindyException("Async methods must either return AsyncResult or void");
				}
				methodImpl.append("  return ");
			}
			methodImpl.append("this.");
			methodImpl.append(asyncRunnerPropertyName);
			methodImpl.append(".run(");
			methodImpl.append("this.");
			methodImpl.append(asyncTaskManagerPropertyName);
			methodImpl.append(", this, ");
			methodImpl.append(paramsObjectName);
			methodImpl.append(");\n");

			methodImpl.append("}");

			String methodBody = methodImpl.toString();
//			System.out.println(methodBody);

			cls.addMethod(CtMethod.make(methodBody, cls));
			idx++;
		}

		return cls.toClass();
	}

	@MetadataModifier
	public static void applyMetadata(ComponentMetadataManagerBuilder metadataManagerBuilder) {
		metadataManagerBuilder.loadComponent(SequentialAsyncTaskManager.class);
		metadataManagerBuilder.loadComponent(PeriodicAsyncTaskManager.class);
		metadataManagerBuilder.loadComponent(ConcurrentAsyncTaskManager.class);

		for (ComponentMetadata componentMetadata : metadataManagerBuilder.getLoadedComponentMetadatas()) {
			List<Method> asyncMethods = componentMetadata.getMethodsWithAnnotation(Async.class);

			if (asyncMethods != null) {
				try {
					String newClassName = componentMetadata.getComponentClass().getName() + "$AsyncImpl";
					Class<?> createdClass;

					try {
						createdClass = Class.forName(newClassName);
					} catch (ClassNotFoundException e) {
						createdClass = makeAsyncType(componentMetadata.getComponentClass(), newClassName, asyncMethods);
					}

					int idx = 0;
					final List<Field> asyncRunnersFields = new ArrayList<>();
					final List<AsyncRunner> asyncRunners = new ArrayList<>();
					for (Method method : asyncMethods) {
						Async async = method.getAnnotation(Async.class);

						Method underlyingMethod = null;

						String methodName = null;
						if (async.underlyingMethodName().isEmpty()) {
							methodName = method.getName();
							if (methodName.endsWith("Async")) {
								methodName = methodName.substring(0, methodName.length() - 5);
							} else {
								throw new CindyException("Async methods MUST have the same name of their underlying method that does the actual work with \"Async\" at the end." +
										"\nExample: a doWork() method can have an async implementation by declaring a doWorkAsync() method and adding the @Async annotation to it");
							}
						} else {
							methodName = async.underlyingMethodName();
						}

						try {
							underlyingMethod = componentMetadata.getComponentClass().getDeclaredMethod(methodName, method.getParameterTypes());
						} catch (Throwable t) {
							throw new CindyException("Unable to find underlying synchronous method " + methodName, t);
						}
						underlyingMethod.setAccessible(true);

						asyncRunnersFields.add(createdClass.getField("__asyncRunner_" + idx));
						asyncRunners.add(new AsyncRunner(underlyingMethod));

						Field wireField = createdClass.getDeclaredField("__asyncTaskManager_" + idx);

						SearchScope searchScope = async.context() == AsyncContext.SHARED ? SearchScope.LOCAL : SearchScope.NO_SEARCH;
						Class<?> asyncTaskManagerClass = null;
						switch (async.mode()) {
							case CONCURRENT:
								asyncTaskManagerClass = ConcurrentAsyncTaskManager.class;
								break;
							case SEQUENTIAL:
								asyncTaskManagerClass = SequentialAsyncTaskManager.class;
								break;
							case PERIODIC:
								asyncTaskManagerClass = PeriodicAsyncTaskManager.class;
								break;
						}
						ComponentDependency dependency = componentMetadata.addDependency(asyncTaskManagerClass, true, false, searchScope, CreationBox.CURRENT_BOX);

						dependency.setWire(new Wire(wireField, async.context() == AsyncContext.SHARED ? null : new BoxOptions(new Aspect[0], new Aspect[0], true)));

						dependency.onInjected((componentMetadata1, componentDependency, dependencyInstance) -> {
							if (dependencyInstance instanceof ConcurrentAsyncTaskManager) {
								ConcurrentAsyncTaskManager tm = (ConcurrentAsyncTaskManager)dependencyInstance;

								if (async.minNumberOfThreads() > tm.getThreadCount()) {
									if (!tm.isInitialized()) {
										tm.setThreadCount(async.minNumberOfThreads());
									} else {
										throw new CindyException("Unable to satisfy ASync parameters: An already existing ConcurrentAsyncTaskManager has been initialized" +
												" and has less threads that the min requested. You can fix this issue by lowering the number of threads requested, " +
												"increasing the number of threads on the other ASync method that created this AsyncTaskManager, or " +
												"isolating the AsyncTaskManager by using a SINGLE AsyncContext. ");
									}
								}
							} else if (dependencyInstance instanceof PeriodicAsyncTaskManager) {
								PeriodicAsyncTaskManager tm = (PeriodicAsyncTaskManager)dependencyInstance;
								if (async.timeMsBetweenFlush() < tm.getTimeMsBetweenFlushs()) {
									if (!tm.isInitialized()) {
										tm.setTimeMsBetweenFlushs(async.timeMsBetweenFlush());
									} else {
										throw new CindyException("Unable to satisfy ASync parameters: An already existing PeriodicAsyncTaskManager has been initialized" +
												" and has a slower time between flush that the max requested. You can fix this issue by increasing the time between flush requested, " +
												"reducing the time between flush on the other ASync method that created this AsyncTaskManager, or " +
												"isolating the AsyncTaskManager by using a SINGLE AsyncContext. ");
									}
								}

							}
						});
						idx++;
					}

					final Class<?> fCreatedCls = createdClass;
					componentMetadata.setFactory(() -> {
						try {
							Object obj = fCreatedCls.newInstance();

							for (int i = 0; i < asyncRunnersFields.size(); i++) {
								asyncRunnersFields.get(i).set(obj, asyncRunners.get(i));
							}

							return obj;
						} catch (Exception e) {
							throw new CindyException("Unable to create component", e);
						}
					});

				} catch (Exception e) {
					throw new CindyException("Unable to make async type from " + componentMetadata.getComponentClass(), e);
				}
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isInitialized() {
		return this.taskQueue != null;
	}
}
