package co.mindie.cindy.task;

import co.mindie.cindy.async.annotation.Async;
import co.mindie.cindy.async.manager.AsyncContext;
import co.mindie.cindy.async.manager.AsyncMode;
import co.mindie.cindy.async.manager.AsyncTaskManager;
import co.mindie.cindy.async.task.AsyncResult;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManagerBuilder;
import co.mindie.cindy.core.component.box.ComponentBox;
import co.mindie.cindy.core.tools.Activator;
import me.corsin.javatools.misc.Action;
import me.corsin.javatools.misc.ValueHolder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(JUnit4.class)
public class AsyncManagerTest {

	public abstract static class AsyncTest {

		@Async
		public abstract AsyncResult<String> doStuffAsync();

		public String doStuff() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			return "a result";
		}
	}


	@Test
	public void test_async_with_get_result_blocks_until_done() {
		try (ComponentBox box = ComponentBox.create(true)) {
			ComponentMetadataManagerBuilder builder = new ComponentMetadataManagerBuilder();
			builder.loadComponent(AsyncTest.class);
			builder.loadComponent(AsyncTaskManager.class);
			AsyncTest test = builder.build().createComponent(AsyncTest.class, box).getInstance();

			String result = test.doStuffAsync().getResult();

			Assert.assertEquals("a result", result);
		}
	}

	public abstract static class AsyncParametersTest {

		@Async(mode = AsyncMode.CONCURRENT)
		public abstract AsyncResult<Double> maxiPowAsync(Double number, Double pow, Integer iterations);

		public Double maxiPow(Double number, Double pow, Integer iterations) {
			double result = 0;
			for (int i = 0; i < iterations; i++) {
				result *= Math.pow(number, pow);
			}

			return result;
		}
	}

	private <T> void useComponent(Class<T> cls, Action<T> onReady) {
		try (ComponentBox box = ComponentBox.create(true)) {
			ComponentMetadataManagerBuilder builder = new ComponentMetadataManagerBuilder();
			builder.loadComponent(cls);
			builder.loadComponent(AsyncTaskManager.class);
			T test = builder.build().createComponent(cls, box).getInstance();
			onReady.run(test);
		}
	}

	@Test
	public void test_async_with_parameters() {
		this.useComponent(AsyncParametersTest.class, test -> {
			Object reachedZero = new Object();
			AtomicInteger count = new AtomicInteger();
			for (int i = 0; i < 1000; i++) {
				count.getAndIncrement();
				test.maxiPowAsync(new Double(7), new Double(21), new Integer(10000)).onComplete(f -> {
					if (count.decrementAndGet() == 0) {
						synchronized (reachedZero) {
							reachedZero.notifyAll();
						}
					}
				});

				synchronized (reachedZero) {
					if (count.get() != 0) {
						try {
							reachedZero.wait(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					Assert.assertEquals(0, count.get());
				}
			}
		});
	}

	public abstract static class AsyncDoubleTest {

		@Async
		public abstract AsyncResult<Double> multiplyAsync(double n, double n2);

		public double multiply(double n, double n2) {
			return n * n2;
		}
	}

	@Test
	public void test_async_with_double_primitive() {
		this.useComponent(AsyncDoubleTest.class, t -> {
			double result = t.multiplyAsync(4242, 1337).getResult();
			Assert.assertEquals(4242 * 1337, result, 0);
		});
	}

	public abstract static class AsyncIntTest {

		@Async
		public abstract AsyncResult<Integer> multiplyAsync(int n, int n2);

		public int multiply(int n, int n2) {
			return n * n2;
		}
	}

	@Test
	public void test_async_with_int_primitive() {
		this.useComponent(AsyncIntTest.class, t -> {
			int result = t.multiplyAsync(4242, 1337).getResult();
			Assert.assertEquals(4242 * 1337, result);
		});
	}

	public abstract static class AsyncFloatTest {

		@Async
		public abstract AsyncResult<Float> multiplyAsync(float n, float n2);

		public float multiply(float n, float n2) {
			return n * n2;
		}
	}

	@Test
	public void test_async_with_float_primitive() {
		this.useComponent(AsyncFloatTest.class, t -> {
			float result = t.multiplyAsync(4242, 1337).getResult();
			Assert.assertEquals(4242 * 1337, result, 0);
		});
	}

	public abstract static class AsyncLongTest {

		@Async
		public abstract AsyncResult<Long> multiplyAsync(long n, long n2);

		public long multiply(long n, long n2) {
			return n * n2;
		}
	}

	@Test
	public void test_async_with_long_primitive() {
		this.useComponent(AsyncLongTest.class, t -> {
			long result = t.multiplyAsync(4242, 1337).getResult();
			Assert.assertEquals(4242 * 1337, result);
		});
	}

	public abstract static class AsyncShortTest {

		@Async
		public abstract AsyncResult<Short> multiplyAsync(short n, short n2);

		public short multiply(short n, short n2) {
			return (short)(n * n2);
		}
	}

	@Test
	public void test_async_with_short_primitive() {
		this.useComponent(AsyncShortTest.class, t -> {
			short result = t.multiplyAsync((short)20, (short)37).getResult();
			Assert.assertEquals((short)20 * (short)37, result);
		});
	}

	public abstract static class AsyncBooleanTest {

		@Async
		public abstract AsyncResult<Boolean> isNegativeAsync(boolean n);

		public boolean isNegative(boolean n) {
			return !n;
		}
	}

	@Test
	public void test_async_with_boolean_primitive() {
		this.useComponent(AsyncBooleanTest.class, t -> {
			boolean result = t.isNegativeAsync(true).getResult();
			Assert.assertEquals(false, result);
		});
	}

	public abstract static class AsyncNameDispatchTest {

		@Async(underlyingMethodName = "returnWololoAfter50ms")
		public abstract AsyncResult<String> getMyName();

		private String returnWololoAfter50ms() throws Throwable {
			Thread.sleep(50);

			return "wololo";
		}
	}

	@Test
	public void test_async_uses_forced_underlying_name() {
		this.useComponent(AsyncNameDispatchTest.class, t -> {
			String result = t.getMyName().getResult();
			Assert.assertEquals("wololo", result);
		});
	}

	public abstract static class SequentialTest {

		@Async
		public abstract AsyncResult<Void> increaseAsync(ValueHolder.IntegerHolder integerHolder);

		private void increase(ValueHolder.IntegerHolder integerHolder) {
			integerHolder.setValue(integerHolder.value() + 1);
		}
	}

	@Test
	public void test_async_sequential() {
		this.useComponent(SequentialTest.class, t -> {
			ValueHolder.IntegerHolder value = new ValueHolder.IntegerHolder();
			value.setValue(0);

			Activator activator = new Activator();
			int max = 1000000;
			for (int i = 0; i < max; i++) {
				AsyncResult<Void> result = t.increaseAsync(value);

				if (i + 1 == max) {
					result.onComplete(c -> {
						activator.activate();
					});
				}
			}

			Assert.assertEquals(true, activator.waitUntilActivated(1000));
			Assert.assertEquals(max, value.value().intValue());
		});
	}


	public abstract static class ConcurrentTest {

		@Async(mode = AsyncMode.CONCURRENT, minNumberOfThreads = 8)
		public abstract AsyncResult<Void> increaseAsync(ValueHolder.IntegerHolder integerHolder);

		private void increase(ValueHolder.IntegerHolder integerHolder) {
			integerHolder.setValue(integerHolder.value() + 1);
		}
	}

	@Test
	public void test_async_concurrent() {
		this.useComponent(ConcurrentTest.class, t -> {
			ValueHolder.IntegerHolder value = new ValueHolder.IntegerHolder();
			value.setValue(0);

			Activator activator = new Activator();
			int max = 1000000;
			for (int i = 0; i < max; i++) {
				AsyncResult<Void> result = t.increaseAsync(value);

				if (i + 1 == max) {
					result.onComplete(c -> {
						activator.activate();
					});
				}
			}

			Assert.assertEquals(true, activator.waitUntilActivated(2000));
			Assert.assertNotEquals(max, value.value().intValue());
		});
	}

	public abstract static class PeriodicTest {

		@Async(mode = AsyncMode.PERIODIC, timeMsBetweenFlush = 200)
		public abstract AsyncResult<Void> activateAsync(Activator activator);

		private void activate(Activator activator) {
			activator.activate();
		}
	}

	@Test
	public void test_async_periodic() {
		this.useComponent(PeriodicTest.class, t -> {
			Activator activator = new Activator();

			t.activateAsync(activator);

			Assert.assertEquals(false, activator.waitUntilActivated(100));
			Assert.assertEquals(true, activator.waitUntilActivated(500));
		});
	}

	public abstract static class VoidReturnTest {

		@Async
		public abstract void doNothingAsync();

		private void doNothing() {

		}
	}

	@Test
	public void test_async_void_return() {
		this.useComponent(VoidReturnTest.class, t -> {
			t.doNothingAsync();
		});
	}

	public abstract static class SequentialTwoMethods {

		@Async
		public abstract AsyncResult<Void> addOneAsync(ValueHolder.IntegerHolder integerHolder);

		@Async
		public abstract AsyncResult<Void> removeOneAsync(ValueHolder.IntegerHolder integerHolder);

		private void addOne(ValueHolder.IntegerHolder integerHolder) {
			integerHolder.setValue(integerHolder.value() + 1);
		}

		private void removeOne(ValueHolder.IntegerHolder integerHolder) {
			integerHolder.setValue(integerHolder.value() - 1);
		}
	}

	@Test
	public void test_async_sequential_with_two_methods() {
		this.useComponent(SequentialTwoMethods.class, t -> {
			ValueHolder.IntegerHolder value = new ValueHolder.IntegerHolder();
			value.setValue(0);

			Activator activator = new Activator();
			int max = 1000000;
			for (int i = 0; i < max; i++) {
				t.removeOneAsync(value);
				AsyncResult<Void> result = t.addOneAsync(value);

				if (i + 1 == max) {
					result.onComplete(c -> {
						activator.activate();
					});
				}
			}

			Assert.assertEquals(true, activator.waitUntilActivated(1000));
			Assert.assertEquals(0, value.value().intValue());
		});
	}

	public abstract static class SequentialTwoMethodsAndOneIsolated {

		@Async
		public abstract AsyncResult<Void> addOneAsync(ValueHolder.IntegerHolder integerHolder);

		@Async(context = AsyncContext.SINGLE)
		public abstract AsyncResult<Void> removeOneAsync(ValueHolder.IntegerHolder integerHolder);

		private void addOne(ValueHolder.IntegerHolder integerHolder) {
			integerHolder.setValue(integerHolder.value() + 1);
		}

		private void removeOne(ValueHolder.IntegerHolder integerHolder) {
			integerHolder.setValue(integerHolder.value() - 1);
		}
	}

	@Test
	public void test_async_sequential_with_two_methods_one_isolated_task_manager() {
		this.useComponent(SequentialTwoMethodsAndOneIsolated.class, t -> {
			ValueHolder.IntegerHolder value = new ValueHolder.IntegerHolder();
			value.setValue(0);

			Activator activator = new Activator();
			int max = 1000000;
			for (int i = 0; i < max; i++) {
				t.removeOneAsync(value);
				AsyncResult<Void> result = t.addOneAsync(value);

				if (i + 1 == max) {
					result.onComplete(c -> {
						activator.activate();
					});
				}
			}

			Assert.assertEquals(true, activator.waitUntilActivated(1000));
			Assert.assertNotEquals(0, value.value().intValue());
		});
	}

	public abstract static class ConcurrentWithDifferentThreadCount {

		@Async(minNumberOfThreads = 2, mode = AsyncMode.CONCURRENT)
		public abstract AsyncResult<Thread> getCurrentThreadAsync();

		@Async(minNumberOfThreads = 16, mode = AsyncMode.CONCURRENT, underlyingMethodName = "getCurrentThread")
		public abstract AsyncResult<Thread> getCurrentThread2Async();

		private Thread getCurrentThread() {
			return Thread.currentThread();
		}
	}

	@Test
	public void test_two_methods_use_the_largest_thread_count() {
		this.useComponent(ConcurrentWithDifferentThreadCount.class, t -> {
			int max = 100000;
			Set<Thread> threadUseds = new HashSet<>();
			for (int i = 0; i < max; i++) {
				t.getCurrentThreadAsync().onComplete(f -> {
					synchronized (threadUseds) {
						threadUseds.add(f);
					}
				});
				t.getCurrentThread2Async().onComplete(f -> {
					synchronized (threadUseds) {
						threadUseds.add(f);
					}
				});
			}

			Assert.assertEquals(16, threadUseds.size());
		});
	}

}
