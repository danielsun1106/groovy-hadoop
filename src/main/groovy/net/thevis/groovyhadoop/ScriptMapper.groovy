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

package net.thevis.groovyhadoop

import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Mapper.Context

/**
 * Reads map text from configuration and parses and prepares script once
 * during {@link #setup(Context)}.  
 * 
 * @author Thomas Thevis
 * @since 0.1.0
 */
class ScriptMapper<KEY_IN, VALUE_IN, KEY_OUT, VALUE_OUT> 
		extends Mapper<KEY_IN, VALUE_IN, KEY_OUT, VALUE_OUT> {

	static final String CONF_MAP_SCRIPT = 'groovyhadoop.map.script'
	
	private Script script
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		def scriptText = context.getConfiguration().get(CONF_MAP_SCRIPT)
		def scriptProvider = new ScriptProvider()
		this.script = scriptProvider.getParsedScript(scriptText)
	}
	
	@Override
	protected void map(KEY_IN key, VALUE_IN value, Context context) 
			throws IOException, InterruptedException {
		
		script.binding.key = key
		script.binding.value = value
		script.binding.context = context
		
		script.run()		
	}
}
