/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.thevis.groovyhadoop;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;

/**
 * @author Thomas Thevis
 *
 */
class MainTest extends GroovyTestCase {

	private File testOutput
	
	void setUp() {
		this.testOutput = new File('build/test-main')
	}
	
	void tearDown() {
		if (this.testOutput?.exists()) {
			this.testOutput.deleteDir()
		}
	}
	
	void testWordcount() {
		Main.main((String[])[
			'-D', 'mapred.output.key.class=org.apache.hadoop.io.Text', 
			'-D', 'mapred.output.value.class=org.apache.hadoop.io.LongWritable', 
			'-map', 'value.toString().tokenize().each { context.write(new Text(it), new LongWritable(1)) }',
			'-reduce', 'def sum = 0; values.each { sum += it.get() }; context.write(key, new LongWritable(sum))', 
			'-input', './LICENSE-2.0', '-output', this.testOutput.path])
		
		def result = new File(this.testOutput, "part-r-00000").text
		
		def expectedResult = getClass().getClassLoader()
			.getResourceAsStream('wordcount.result').text
		assert result == expectedResult
	}
}