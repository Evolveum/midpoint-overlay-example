/**
 * Copyright (c) 2016 Evolveum
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
package com.example.midpoint.schema;

import javax.xml.namespace.QName;

/**
 * @author Radovan Semancik
 *
 */
public class ExampleSchemaConstants {

	public static final String NS_SCHEMA_EXTENSION = "http://example.com/xml/ns/midpoint/schema/extension-3";
	
	public static final QName SCHEMA_EXTENSION_SSN = new QName(NS_SCHEMA_EXTENSION, "ssn");
	public static final QName SCHEMA_EXTENSION_DOMAIN = new QName(NS_SCHEMA_EXTENSION, "domain");
	
	public static final String ROLE_TYPE_SIMPLE = "simple";
	public static final String ROLE_TYPE_DOMAIN = "domain";
}
