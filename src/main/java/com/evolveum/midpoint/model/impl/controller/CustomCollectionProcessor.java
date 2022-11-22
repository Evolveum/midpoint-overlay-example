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
package com.evolveum.midpoint.model.impl.controller;

import com.evolveum.midpoint.model.api.authentication.CompiledObjectCollectionView;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.PrismProperty;
import com.evolveum.midpoint.repo.common.SystemObjectCache;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.schema.custom.CustomSchemaConstants;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.common.common_3.GuiObjectListViewType;

import com.evolveum.midpoint.xml.ns._public.common.common_3.SystemConfigurationType;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Primary
@Component
public class CustomCollectionProcessor extends CollectionProcessor{

    private static final Trace LOGGER = TraceManager.getTrace(CustomCollectionProcessor.class);

    private static final String LOAD_SYSTEM_CONFIGURATION_OPERATION = "loadSystemConfigurationOperation";

    @Autowired SystemObjectCache systemObjectCache;

    @Override
    public void compileView(CompiledObjectCollectionView existingView, GuiObjectListViewType objectListViewType) {
        super.compileView(existingView, objectListViewType);
        compileTopLevelView(existingView);
    }

    private void compileTopLevelView(CompiledObjectCollectionView existingView) {
        if (existingView.getTopLevelView() == null) {
            boolean isTopLevel = false;
            if (getTopLevelViews().contains(existingView.getViewIdentifier())) {
                isTopLevel = true;
            }
            existingView.setTopLevelView(isTopLevel);
        }
    }

    private Collection<String> getTopLevelViews() {
        @Nullable PrismObject<SystemConfigurationType> system = null;
        try {
            system = systemObjectCache.getSystemConfiguration(new OperationResult(LOAD_SYSTEM_CONFIGURATION_OPERATION));
        } catch (SchemaException e) {
            LOGGER.error("Couldn't load system configuration", e);
        }
        if (system != null) {
            PrismProperty<String> property = system.findProperty(CustomSchemaConstants.EXTENSION_TOP_LEVEL_VIEWS_PATH);
            if (property != null && !property.isEmpty()) {
                return property.getRealValues();
            }
        }
        return Collections.EMPTY_LIST;
    }
}
