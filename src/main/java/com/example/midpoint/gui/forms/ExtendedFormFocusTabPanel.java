/*
 * Copyright (C) 2016-2020 Evolveum
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

import java.util.ArrayList;
import java.util.List;

import com.example.midpoint.schema.ExampleSchemaConstants;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import com.evolveum.midpoint.gui.api.model.LoadableModel;
import com.evolveum.midpoint.gui.api.prism.wrapper.PrismObjectWrapper;
import com.evolveum.midpoint.gui.api.prism.wrapper.ShadowWrapper;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.prism.query.ObjectQuery;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.util.DOMUtil;
import com.evolveum.midpoint.util.logging.LoggingUtils;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.web.component.assignment.SimpleRoleSelector;
import com.evolveum.midpoint.web.component.form.MidpointForm;
import com.evolveum.midpoint.web.component.objectdetails.AbstractFocusTabPanel;
import com.evolveum.midpoint.web.model.PrismContainerWrapperModel;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;
import com.evolveum.prism.xml.ns._public.types_3.PolyStringType;

/**
 * Sample showing a custom focus form that displays semi-static form.
 * This form is using extended attributes and role parameters. It needs extension-samples.xsd.
 *
 * @author Radovan Semancik
 */
public class ExtendedFormFocusTabPanel<F extends FocusType> extends AbstractFocusTabPanel<F> {
    private static final long serialVersionUID = 1L;

    private static final String DOT_CLASS = ExtendedFormFocusTabPanel.class.getName() + ".";
    private static final String OPERATION_SEARCH_ROLES = DOT_CLASS + "searchRoles";

    private static final String ID_HEADER = "header";

    private static final String ID_PROP_NAME = "propName";
    private static final String ID_PROP_FULL_NAME = "propFullName";
    private static final String ID_PROP_SSN = "propSsn";

    private static final String ID_ROLES_SIMPLE = "rolesSimple";

    private static final Trace LOGGER = TraceManager.getTrace(ExtendedFormFocusTabPanel.class);

    public ExtendedFormFocusTabPanel(String id, MidpointForm<PrismObjectWrapper<F>> mainForm,
            LoadableModel<PrismObjectWrapper<F>> focusWrapperModel,
            LoadableModel<List<ShadowWrapper>> projectionModel) {
        super(id, mainForm, focusWrapperModel, projectionModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initLayout();
    }

    private void initLayout() {
        add(new Label(ID_HEADER, "Object details"));
        WebMarkupContainer body = new WebMarkupContainer("body");
        add(body);

        addPrismPropertyPanel(body, ID_PROP_NAME, PolyStringType.COMPLEX_TYPE, FocusType.F_NAME);
        addPrismPropertyPanel(body, ID_PROP_FULL_NAME, PolyStringType.COMPLEX_TYPE, UserType.F_FULL_NAME);
        addPrismPropertyPanel(body, ID_PROP_SSN, DOMUtil.XSD_STRING, ItemPath.create(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_SSN));

        // TODO: create proxy for these operations
        Task task = getPageBase().createSimpleTask(OPERATION_SEARCH_ROLES);
        List<PrismObject<RoleType>> availableSimpleRoles;
        try {
            ObjectQuery simpleRoleQuery = getPageBase().getPrismContext().queryFor(RoleType.class).item(RoleType.F_ROLE_TYPE).eq(ExampleSchemaConstants.ROLE_TYPE_SIMPLE).build();

            availableSimpleRoles = getPageBase().getModelService().searchObjects(RoleType.class, simpleRoleQuery, null, task, task.getResult());
        } catch (Throwable e) {
            task.getResult().recordFatalError(e);
            LoggingUtils.logException(LOGGER, "Couldn't load roles", e);
            availableSimpleRoles = new ArrayList<>();
            // TODO: better errror reporting
        }

        PrismContainerWrapperModel<F, AssignmentType> assignmentsModel =
                PrismContainerWrapperModel.fromContainerWrapper(getObjectWrapperModel(), FocusType.F_ASSIGNMENT);

        add(new SimpleRoleSelector<>(ID_ROLES_SIMPLE, assignmentsModel, availableSimpleRoles));
    }
}
