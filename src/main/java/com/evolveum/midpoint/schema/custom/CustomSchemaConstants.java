/*
 * Copyright (C) 2022 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolveum.midpoint.schema.custom;

import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;

import javax.xml.namespace.QName;

/**
 * @author lskublik
 */
public class CustomSchemaConstants {

    public static final String NS_SCHEMA_EXTENSION = "http://example.com/xml/ns/midpoint/schema/extension-3";

    public static final QName SCHEMA_EXTENSION_TOP_LEVEL_VIEWS = new QName(NS_SCHEMA_EXTENSION, "topLevelViews");
    public static final ItemPath EXTENSION_TOP_LEVEL_VIEWS_PATH = ItemPath.create(ObjectType.F_EXTENSION, SCHEMA_EXTENSION_TOP_LEVEL_VIEWS);
}
