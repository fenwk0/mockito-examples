package basic;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

public class Basic {

	// Creates a mock iterator and makes it return 
	// "Hello" the first time method next() is called. 
	// Calls after that return "World". 
	// Then we can run normal assertions. 
	@Test
	public void testIteratorWillReturnHelloWorld() {
		// Initialise
		@SuppressWarnings("unchecked")
		Iterator<String> i = mock(Iterator.class);
		// Set Expectations
		when(i.next()).thenReturn("Hello").thenReturn("World");
		// Exercise
		String result = i.next() + " " + i.next();
		// Assert/Verify
		assertEquals("Hello World", result);
	}
	
	// Stubs can also return different values depending on arguments 
	// passed into the method.
	//
	// This creates a stub Comparable object and returns 1 if it is 
	// compared to a particular String value (“Test” in this case).
	@Test
	public void testWithArguments() {
		@SuppressWarnings("unchecked")
		Comparable<String> c = mock(Comparable.class);
		when(c.compareTo("Test")).thenReturn(1);
		assertEquals(1,c.compareTo("Test"));
	}
	
	// Don't care what is passed or cannot predict.
	// Use anyInt() (and other type equivalents).
	@Test
	public void testWithUnspecifiedArguments() {
		@SuppressWarnings("unchecked")
		Comparable<Integer> c = mock(Comparable.class);
		when(c.compareTo(anyInt())).thenReturn(-1);
		assertEquals(-1,c.compareTo(5));
	}
	
	@Test(expected = IOException.class)
	public void testOutputStreamWriterRethrowsExceptionFromOutputStream() throws IOException {
		OutputStream mock = mock(OutputStream.class);
		OutputStreamWriter osw = new OutputStreamWriter(mock);
		doThrow(new IOException()).when(mock).close();
		osw.close();
	}
	
	// Verify actual calls to underlying objects.
	// This verifies that OutputStreamWriter propagates the 
	// close method call to the wrapped output stream.
	@Test
	public void testOutputStreamWriterClosesOutputStreamOnClose() throws IOException {
		OutputStream mock = mock(OutputStream.class);
		OutputStreamWriter osw = new OutputStreamWriter(mock);
		osw.close();
		verify(mock).close();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOutputStreamWriterBuffersAndForwardsToOutputStream() throws IOException {		
		OutputStream mock = mock(OutputStream.class);
		OutputStreamWriter osw = new OutputStreamWriter(mock);
		osw.write('a');
		osw.flush();
		// Can't do this as don't know how long the array is going to be.
		// verify(mock).write(new byte[]{'a'},0,1);

		@SuppressWarnings("rawtypes")
		BaseMatcher arrayStartingWithA = new BaseMatcher(){
			@Override
			public void describeTo(Description description) {
				// Nothing
			}
			// Check first character is A
			@Override
			public boolean matches(Object item) {
				byte[] actual=(byte[]) item;
				return actual[0]=='a';
			}
		};
		// Check that first char of array is A, and other two args are 0 and 1
		verify(mock).write((byte[]) argThat(arrayStartingWithA), eq(0), eq(1));	
	}
}
