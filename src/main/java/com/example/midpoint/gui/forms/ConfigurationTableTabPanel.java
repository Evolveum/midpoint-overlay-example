/**
 * Copyright (c) 2016-2018 Evolveum
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.midpoint.gui.forms;

import com.evolveum.midpoint.gui.api.model.LoadableModel;
import com.evolveum.midpoint.gui.api.page.PageBase;
import com.evolveum.midpoint.prism.Containerable;
import com.evolveum.midpoint.prism.PrismContainerValue;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.prism.query.ObjectQuery;
import com.evolveum.midpoint.prism.query.builder.QueryBuilder;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.util.logging.LoggingUtils;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.web.component.assignment.SimpleRoleSelector;
import com.evolveum.midpoint.web.component.form.Form;
import com.evolveum.midpoint.web.component.objectdetails.AbstractFocusTabPanel;
import com.evolveum.midpoint.web.component.prism.ContainerValueWrapper;
import com.evolveum.midpoint.web.component.prism.ContainerWrapper;
import com.evolveum.midpoint.web.component.prism.ContainerWrapperFactory;
import com.evolveum.midpoint.web.component.prism.ObjectWrapper;
import com.evolveum.midpoint.web.component.prism.PrismValuePanel;
import com.evolveum.midpoint.web.component.prism.PropertyOrReferenceWrapper;
import com.evolveum.midpoint.web.component.prism.ValueStatus;
import com.evolveum.midpoint.web.component.prism.ValueWrapper;
import com.evolveum.midpoint.web.model.ContainerWrapperFromObjectWrapperModel;
import com.evolveum.midpoint.web.page.admin.users.dto.FocusSubwrapperDto;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;
import com.example.midpoint.schema.ExampleSchemaConstants;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Sample showing a custom focus form that displays configuration parameters formatted in a table.
 *
 * @author Radovan Semancik
 */
public class ConfigurationTableTabPanel<F extends FocusType> extends AbstractFocusTabPanel<F> {
    private static final long serialVersionUID = 1L;

    private static final String ID_TRANSFORM_DESCRIPTION = "transformDescription";
    private static final String ID_TRANSFORMATION_ENABLED = "transformationEnabled";
    private static final String ID_TRANSFORM_TABLE_ROW = "transformTableRow";
    private static final String ID_TRANSFORM_TABLE_PATTERN = "transformPattern";
    private static final String ID_TRANSFORM_TABLE_REPLACEMENT = "transformReplacement";
    private static final String ID_ADD_TRANSFORM = "addTransform";

    private static final Trace LOGGER = TraceManager.getTrace(ConfigurationTableTabPanel.class);

    public ConfigurationTableTabPanel(String id, Form mainForm,
                                     LoadableModel<ObjectWrapper<F>> focusWrapperModel,
                                     LoadableModel<List<FocusSubwrapperDto<ShadowType>>> projectionModel,
                                     PageBase pageBase) {
        super(id, mainForm, focusWrapperModel, projectionModel, pageBase);
        initLayout(focusWrapperModel, pageBase);
    }

    private void initLayout(final LoadableModel<ObjectWrapper<F>> focusModel, PageBase pageBase) {
    	setOutputMarkupId(true);
    	
    	ContainerWrapperFromObjectWrapperModel transformWrapperModel = new ContainerWrapperFromObjectWrapperModel<>(focusModel, 
    			new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM));
    	
        addPrismPropertyPanel(this, ID_TRANSFORM_DESCRIPTION, new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM_DESCRIPTION));
        addPrismPropertyPanel(this, ID_TRANSFORMATION_ENABLED, new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORMATION_ENABLED));
        
//        ContainerWrapperFromObjectWrapperModel transformModel = new ContainerWrapperFromObjectWrapperModel(focusModel, new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM));
        final ListView<ContainerValueWrapper> table = new ListView<ContainerValueWrapper>(ID_TRANSFORM_TABLE_ROW, transformWrapperModel.getValuesModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ContainerValueWrapper> item) {
				LOGGER.info("POPULATING: {}", item);
				item.add(createTransformTableItem(ID_TRANSFORM_TABLE_PATTERN, item.getModel(), ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM_PATTERN));
				item.add(createTransformTableItem(ID_TRANSFORM_TABLE_REPLACEMENT, item.getModel(), ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM_REPLACEMENT));
			}
        	
        };
        add(table);
        
        AjaxLink<ObjectWrapper<F>> addButton = new AjaxLink<ObjectWrapper<F>>(ID_ADD_TRANSFORM, focusModel) {

			@Override
			public void onClick(AjaxRequestTarget target) {
				ObjectWrapper<F> objectWrapper = focusModel.getObject();
		    	ContainerWrapper transformWrapper = objectWrapper.findContainerWrapper(new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM));
				ContainerWrapperFactory cwf = new ContainerWrapperFactory(getPageBase());
				Task task = getPageBase().createSimpleTask("Creating new container");
				ContainerValueWrapper newContainerValue = cwf.createContainerValueWrapper(transformWrapper,
						transformWrapper.getItem().createNewValue(), transformWrapper.getObjectStatus(), ValueStatus.ADDED,
						transformWrapper.getPath(), task);
				newContainerValue.setShowEmpty(true, false);
				transformWrapper.addValue(newContainerValue);
				LOGGER.info("TRANSFORM WRAPPER (ADD)\n:{}", transformWrapper.debugDump(1));
				target.add(ConfigurationTableTabPanel.this);
			}
		};
		add(addButton);
        
    }
    
    private Component createTransformTableItem(String id, IModel<ContainerValueWrapper> itemModel, QName tableElementQName) {
    	PropertyOrReferenceWrapper propertyWrapper = itemModel.getObject().findPropertyWrapper(tableElementQName);
    	List<ValueWrapper> valueWrappers = propertyWrapper.getValues();
    	ValueWrapper valueWrapper = valueWrappers.get(0);
    	
    	PrismValuePanel valuePanel = new PrismValuePanel(id, Model.of(valueWrapper), Model.of("LLllaabel"), getMainForm(), null, null);
    	return valuePanel;
	}
    
}
