/*
 * Copyright (C) 2016-2021 Evolveum
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
package com.example.midpoint.gui.forms;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.gui.impl.page.admin.focus.FocusDetailsModels;

import com.example.midpoint.schema.ExampleSchemaConstants;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import com.evolveum.midpoint.gui.api.factory.GuiComponentFactory;
import com.evolveum.midpoint.gui.api.prism.wrapper.PrismContainerValueWrapper;
import com.evolveum.midpoint.gui.api.prism.wrapper.PrismContainerWrapper;
import com.evolveum.midpoint.gui.api.prism.wrapper.PrismObjectWrapper;
import com.evolveum.midpoint.gui.api.util.WebPrismUtil;
import com.evolveum.midpoint.gui.impl.factory.panel.ItemRealValueModel;
import com.evolveum.midpoint.gui.impl.factory.panel.PrismPropertyPanelContext;
import com.evolveum.midpoint.gui.impl.page.admin.AbstractObjectMainPanel;
import com.evolveum.midpoint.gui.impl.prism.wrapper.PrismPropertyValueWrapper;
import com.evolveum.midpoint.prism.Containerable;
import com.evolveum.midpoint.prism.path.ItemName;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.util.DOMUtil;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.logging.LoggingUtils;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.web.application.PanelType;
import com.evolveum.midpoint.web.component.message.FeedbackAlerts;
import com.evolveum.midpoint.web.model.PrismContainerWrapperModel;
import com.evolveum.midpoint.web.model.PrismPropertyWrapperModel;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ContainerPanelConfigurationType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.FocusType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;

/**
 * Sample showing a custom focus form that displays configuration parameters formatted in a table.
 *
 * @author Radovan Semancik
 */
@PanelType(name = "configurableOptionsTable")
public class ConfigurationTableTabPanel<F extends FocusType>
        extends AbstractObjectMainPanel<F, FocusDetailsModels<F>> {

    private static final long serialVersionUID = 1L;

    private static final String ID_TRANSFORM_DESCRIPTION = "transformDescription";
    private static final String ID_TRANSFORMATION_ENABLED = "transformationEnabled";
    private static final String ID_TRANSFORM_TABLE_ROW = "transformTableRow";
    private static final String ID_TRANSFORM_TABLE_PATTERN = "transformPattern";
    private static final String ID_TRANSFORM_TABLE_REPLACEMENT = "transformReplacement";
    private static final String ID_ADD_TRANSFORM = "addTransform";

    private static final String ID_FEEDBACK = "feedback";

    private static final transient Trace LOGGER = TraceManager.getTrace(ConfigurationTableTabPanel.class);

    public ConfigurationTableTabPanel(String id, FocusDetailsModels<F> model, ContainerPanelConfigurationType config) {
        super(id, model, config);
    }

    protected void initLayout() {
        setOutputMarkupId(true);

        // This is absolutely ordinary field. MidPoint GUI code will choose appropriate input for the field.
        addPrismPropertyPanel(this, ID_TRANSFORM_DESCRIPTION, DOMUtil.XSD_STRING,
                ItemPath.create(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM_DESCRIPTION));

        // This is an example of using custom Wicket component to handle a property.
        PrismPropertyWrapperModel<F, Boolean> transformationEnabledPropertyWrapperModel =
                PrismPropertyWrapperModel.fromContainerWrapper(getObjectWrapperModel(),
                        ItemPath.create(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORMATION_ENABLED));

        IModel<PrismPropertyValueWrapper<Boolean>> transformationEnabledPropertyValueWrapperModel =
                new PropertyModel<>(transformationEnabledPropertyWrapperModel, "value");
        ItemRealValueModel<Boolean> checkboxModel = new ItemRealValueModel<>(transformationEnabledPropertyValueWrapperModel);
        CheckBox checkbox = new CheckBox(ID_TRANSFORMATION_ENABLED, checkboxModel);
        this.add(checkbox);

        // Following code sets up a table of pattern/replacement transforms
        PrismContainerWrapperModel<?, ?> transformWrapperModel =
                PrismContainerWrapperModel.fromContainerWrapper(
                        getObjectWrapperModel(), ExampleSchemaConstants.PATH_EXTENSION_TRANSFORM);

        ListView<PrismContainerValueWrapper<Containerable>> table = new ListView<>(
                ID_TRANSFORM_TABLE_ROW, new PropertyModel<>(transformWrapperModel, "values")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<PrismContainerValueWrapper<Containerable>> item) {
                // feedback
                FeedbackAlerts feedback = new FeedbackAlerts(ID_FEEDBACK);
                feedback.setOutputMarkupId(true);
                item.add(feedback);

                item.add(createTransformTableItem(ID_TRANSFORM_TABLE_PATTERN, item.getModel(),
                        ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM_PATTERN));
                item.add(createTransformTableItem(ID_TRANSFORM_TABLE_REPLACEMENT, item.getModel(),
                        ExampleSchemaConstants.SCHEMA_EXTENSION_TRANSFORM_REPLACEMENT));
            }

        };
        add(table);

        AjaxLink<PrismObjectWrapper<F>> addButton = new AjaxLink<>(ID_ADD_TRANSFORM, getObjectWrapperModel()) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                PrismObjectWrapper<F> objectWrapper = getObjectWrapperModel().getObject();

                try {
                    PrismContainerWrapper<Containerable> transformWrapper =
                            objectWrapper.findContainer(ExampleSchemaConstants.PATH_EXTENSION_TRANSFORM);
                    WebPrismUtil.createNewValueWrapper(
                            transformWrapper, transformWrapper.getItem().createNewValue(), getPageBase(), target);
                } catch (SchemaException e) {
                    LoggingUtils.logException(LOGGER,
                            "Cannot find container " + ExampleSchemaConstants.PATH_EXTENSION_TRANSFORM + " in " + getObjectWrapper(), e);
                    target.add(ConfigurationTableTabPanel.this);
                    return;
                }
                target.add(ConfigurationTableTabPanel.this);
            }
        };
        add(addButton);

    }

    private Component createTransformTableItem(String id,
            IModel<PrismContainerValueWrapper<Containerable>> itemModel, QName tableElementQName) {

        PrismPropertyWrapperModel<Containerable, ?> propertyModel =
                PrismPropertyWrapperModel.fromContainerValueWrapper(
                        itemModel, ItemName.fromQName(tableElementQName));
        GuiComponentFactory<PrismPropertyPanelContext<?>> valuePanelFactory =
                getPageBase().getRegistry().findValuePanelFactory(propertyModel.getObject(), propertyModel.getObject().getParent());
        if (valuePanelFactory == null) {
            return new Label(id, createStringResource("Cannot create component for " + tableElementQName));
        }

        PrismPropertyPanelContext<?> ctx = new PrismPropertyPanelContext<>(propertyModel);
        ctx.setComponentId(id);
        ctx.setParentComponent(this);
        ctx.setRealValueModel(new PropertyModel<>(propertyModel, "value"));
        return valuePanelFactory.createPanel(ctx);
    }
}
