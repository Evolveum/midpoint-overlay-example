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
import com.evolveum.midpoint.web.component.prism.ObjectWrapper;
import com.evolveum.midpoint.web.model.ContainerWrapperFromObjectWrapperModel;
import com.evolveum.midpoint.web.page.admin.users.dto.FocusSubwrapperDto;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;
import com.example.midpoint.schema.ExampleSchemaConstants;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

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
    private static final String ID_ROLES_DOMAIN = "rolesDomain";

    private static final Trace LOGGER = TraceManager.getTrace(ExtendedFormFocusTabPanel.class);

    public ExtendedFormFocusTabPanel(String id, Form mainForm,
                                     LoadableModel<ObjectWrapper<F>> focusWrapperModel,
                                     LoadableModel<List<FocusSubwrapperDto<ShadowType>>> projectionModel,
                                     PageBase pageBase) {
        super(id, mainForm, focusWrapperModel, projectionModel, pageBase);
        initLayout(focusWrapperModel, pageBase);
    }

    private void initLayout(final LoadableModel<ObjectWrapper<F>> focusModel, PageBase pageBase) {
        add(new Label(ID_HEADER, "Object details"));
        WebMarkupContainer body = new WebMarkupContainer("body");
        add(body);

        addPrismPropertyPanel(body, ID_PROP_NAME, FocusType.F_NAME);
        addPrismPropertyPanel(body, ID_PROP_FULL_NAME, UserType.F_FULL_NAME);
        addPrismPropertyPanel(body, ID_PROP_SSN, new ItemPath(ObjectType.F_EXTENSION, ExampleSchemaConstants.SCHEMA_EXTENSION_SSN));

        // TODO: create proxy for these operations
        Task task = pageBase.createSimpleTask(OPERATION_SEARCH_ROLES);
        List<PrismObject<RoleType>> availableSimpleRoles;
        try {
            ObjectQuery simpleRoleQuery = QueryBuilder.queryFor(RoleType.class, getPrismContext()).item(RoleType.F_ROLE_TYPE).eq(ExampleSchemaConstants.ROLE_TYPE_SIMPLE).build();
//			ObjectQuery simpleRoleQuery = ObjectQuery.createObjectQuery(
//					EqualFilter.createEqual(RoleType.F_ROLE_TYPE, RoleType.class, getPrismContext(), ExampleSchemaConstants.ROLE_TYPE_SIMPLE));
            availableSimpleRoles = pageBase.getModelService().searchObjects(RoleType.class, simpleRoleQuery, null, task, task.getResult());
        } catch (Throwable e) {
            task.getResult().recordFatalError(e);
            LoggingUtils.logException(LOGGER, "Couldn't load roles", e);
            availableSimpleRoles = new ArrayList<>();
            // TODO: better errror reporting
        }

        ContainerWrapperFromObjectWrapperModel<AssignmentType, F> assignmentsModel = new ContainerWrapperFromObjectWrapperModel<>(focusModel, new ItemPath(FocusType.F_ASSIGNMENT));
		add(new SimpleRoleSelector<F, RoleType>(ID_ROLES_SIMPLE, assignmentsModel, availableSimpleRoles));
    }
}
