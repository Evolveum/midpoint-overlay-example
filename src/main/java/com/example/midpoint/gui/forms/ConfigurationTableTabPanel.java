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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.evolveum.midpoint.gui.api.model.LoadableModel;
import com.evolveum.midpoint.gui.api.page.PageBase;
import com.evolveum.midpoint.prism.Containerable;
import com.evolveum.midpoint.prism.PrismPropertyValue;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.web.component.form.Form;
import com.evolveum.midpoint.web.component.objectdetails.AbstractFocusTabPanel;
import com.evolveum.midpoint.web.component.prism.ContainerValueWrapper;
import com.evolveum.midpoint.web.component.prism.ContainerWrapper;
import com.evolveum.midpoint.web.component.prism.ContainerWrapperFactory;
import com.evolveum.midpoint.web.component.prism.ObjectWrapper;
import com.evolveum.midpoint.web.component.prism.PrismValuePanel;
import com.evolveum.midpoint.web.component.prism.PropertyOrReferenceWrapper;
import com.evolveum.midpoint.web.component.prism.PropertyWrapper;
import com.evolveum.midpoint.web.component.prism.ValueStatus;
import com.evolveum.midpoint.web.component.prism.ValueWrapper;
import com.evolveum.midpoint.web.model.ContainerWrapperFromObjectWrapperModel;
import com.evolveum.midpoint.web.model.PropertyWrapperFromObjectWrapperModel;
import com.evolveum.midpoint.web.page.admin.users.dto.FocusSubwrapperDto;
import com.evolveum.midpoint.xml.ns._public.common.common_3.FocusType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ShadowType;
import com.example.midpoint.schema.ExampleSchemaConstants;

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
    }

    @Override
	protected void onInitialize() {
    	super.onInitialize();
    	initLayout();
    }
    
    private void initLayout() {
    	setOutputMarkupId(true);
    	
    	// This is absolutely ordinary field. MidPoint GUI code will choose appropriate input for the field.
        addPrismPropertyPanel(this, ID_TRANSFORM_DESCRIPTION, 
        		new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM_DESCRIPTION));
        
        // This is is an example of using custom Wicket component to handle a property.
        final PropertyWrapperFromObjectWrapperModel<Boolean, F> transformationEnabledPropertyWrapperModel = 
        		new PropertyWrapperFromObjectWrapperModel<Boolean,F>(getObjectWrapperModel(), 
        				new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORMATION_ENABLED));
        IModel<Boolean> checkboxModel = new IModel<Boolean>() {

			@Override
			public void detach() {
			}

			@Override
			public Boolean getObject() {
				PropertyWrapper<Boolean> propertyWrapper = transformationEnabledPropertyWrapperModel.getObject();
				List<ValueWrapper> values = propertyWrapper.getValues();
				if (values.isEmpty()) {
					return false;
				}
				ValueWrapper valueWrapper = values.get(0);
				PrismPropertyValue<Boolean> pval = (PrismPropertyValue<Boolean>) valueWrapper.getValue();
				Boolean value = pval.getValue();
				if (value == null) {
					return false;
				}
				return value;
			}

			@Override
			public void setObject(Boolean object) {
				PropertyWrapper<Boolean> propertyWrapper = transformationEnabledPropertyWrapperModel.getObject();
				List<ValueWrapper> values = propertyWrapper.getValues();
				if (values.isEmpty()) {
					ValueWrapper valueWrapper = new ValueWrapper<>(propertyWrapper, new PrismPropertyValue<Boolean>(object));
					values.add(valueWrapper);
				} else {
					ValueWrapper valueWrapper = values.get(0);
					PrismPropertyValue<Boolean> pval = (PrismPropertyValue<Boolean>) valueWrapper.getValue();
					pval.setValue(object);
				}
			}
        };
        CheckBox checkbox = new CheckBox(ID_TRANSFORMATION_ENABLED, checkboxModel);
		this.add(checkbox);
        
        // Following code sets up a table of pattern/replacement transforms
		
        ContainerWrapperFromObjectWrapperModel transformWrapperModel = new ContainerWrapperFromObjectWrapperModel<>(getObjectWrapperModel(), 
    			new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM));

        final ListView<ContainerValueWrapper<Containerable>> table = new ListView<ContainerValueWrapper<Containerable>>(ID_TRANSFORM_TABLE_ROW, transformWrapperModel.getValuesModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ContainerValueWrapper<Containerable>> item) {
				item.add(createTransformTableItem(ID_TRANSFORM_TABLE_PATTERN, item.getModel(), ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM_PATTERN));
				item.add(createTransformTableItem(ID_TRANSFORM_TABLE_REPLACEMENT, item.getModel(), ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM_REPLACEMENT));
			}
        	
        };
        add(table);
        
        AjaxLink<ObjectWrapper<F>> addButton = new AjaxLink<ObjectWrapper<F>>(ID_ADD_TRANSFORM, getObjectWrapperModel()) {

			@Override
			public void onClick(AjaxRequestTarget target) {
				ObjectWrapper<F> objectWrapper = getObjectWrapperModel().getObject();
		    	ContainerWrapper<Containerable> transformWrapper = objectWrapper.findContainerWrapper(new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM));
				ContainerWrapperFactory cwf = new ContainerWrapperFactory(getPageBase());
				Task task = getPageBase().createSimpleTask("Creating new container");
				ContainerValueWrapper<Containerable> newContainerValue = cwf.createContainerValueWrapper(transformWrapper,
						transformWrapper.getItem().createNewValue(), transformWrapper.getObjectStatus(), ValueStatus.ADDED,
						transformWrapper.getPath(), task);
				newContainerValue.setShowEmpty(true, false);
				transformWrapper.addValue(newContainerValue);
				target.add(ConfigurationTableTabPanel.this);
			}
		};
		add(addButton);
        
    }
    
    private Component createTransformTableItem(String id, IModel<ContainerValueWrapper<Containerable>> itemModel, QName tableElementQName) {
    	PropertyOrReferenceWrapper propertyWrapper = itemModel.getObject().findPropertyWrapper(tableElementQName);
    	List<ValueWrapper> valueWrappers = propertyWrapper.getValues();
    	ValueWrapper valueWrapper = valueWrappers.get(0);
    	
    	PrismValuePanel valuePanel = new PrismValuePanel(id, Model.of(valueWrapper), Model.of("Label"), getMainForm(), null, null);
    	return valuePanel;
	}
    
}
