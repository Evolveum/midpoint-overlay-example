/*
 * Copyright (c) 2016-2018 Evolveum
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

import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;

/**
 * @author Radovan Semancik
 */
public class ExampleSchemaConstants {

    public static final String NS_SCHEMA_EXTENSION = "http://example.com/xml/ns/midpoint/schema/extension-3";

    public static final QName SCHEMA_EXTENSION_SSN = new QName(NS_SCHEMA_EXTENSION, "ssn");
    public static final QName SCHEMA_EXTENSION_DOMAIN = new QName(NS_SCHEMA_EXTENSION, "domain");

    public static final QName SCHEMA_EXTENSION_TRANSFORM_DESCRIPTION = new QName(NS_SCHEMA_EXTENSION, "transformDescription");
    public static final QName SCHEMA_EXTENSION_TRANSFORMATION_ENABLED = new QName(NS_SCHEMA_EXTENSION, "transformationEnabled");
    public static final QName SCHEMA_EXTENSION_TRANSFORM = new QName(NS_SCHEMA_EXTENSION, "transform");
    public static final QName SCHEMA_EXTENSION_TRANSFORM_PATTERN = new QName(NS_SCHEMA_EXTENSION, "pattern");
    public static final QName SCHEMA_EXTENSION_TRANSFORM_REPLACEMENT = new QName(NS_SCHEMA_EXTENSION, "replacement");

    public static final ItemPath PATH_EXTENSION_TRANSFORM = ItemPath.create(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM);

    public static final String ROLE_TYPE_SIMPLE = "simple";
    public static final String ROLE_TYPE_DOMAIN = "domain";
}
