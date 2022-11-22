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
package com.evolveum.midpoint.model.api.authentication;

import com.evolveum.midpoint.prism.Containerable;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.polystring.PolyString;
import com.evolveum.midpoint.prism.query.ObjectFilter;
import com.evolveum.midpoint.schema.GetOperationOptions;
import com.evolveum.midpoint.schema.SelectorOptions;
import com.evolveum.midpoint.util.DebugDumpable;
import com.evolveum.midpoint.util.DebugUtil;
import com.evolveum.midpoint.util.QNameUtil;
import com.evolveum.midpoint.util.annotation.Experimental;
import com.evolveum.midpoint.xml.ns._public.common.common_3.CollectionRefSpecificationType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.DisplayType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.DistinctSearchOptionType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.GuiActionType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.GuiObjectColumnType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.GuiObjectListViewAdditionalPanelsType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.GuiObjectListViewType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationTypeType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.SearchBoxConfigurationType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.UserInterfaceElementVisibilityType;
import com.evolveum.prism.xml.ns._public.query_3.PagingType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;

@Experimental
public class CompiledObjectCollectionView implements DebugDumpable, Serializable {
    private static final long serialVersionUID = 1L;
    private QName containerType;
    private String viewIdentifier;
    private List<GuiActionType> actions = new ArrayList();
    private CollectionRefSpecificationType collection;
    private List<GuiObjectColumnType> columns = new ArrayList();
    private DisplayType display;
    private GuiObjectListViewAdditionalPanelsType additionalPanels;
    private DistinctSearchOptionType distinct;
    private Boolean disableSorting;
    private Boolean disableCounting;
    private SearchBoxConfigurationType searchBoxConfiguration;
    private ObjectFilter filter;
    private ObjectFilter domainFilter;
    private Integer displayOrder;
    private Integer refreshInterval;
    private Collection<SelectorOptions<GetOperationOptions>> options;
    private Collection<SelectorOptions<GetOperationOptions>> domainOptions;
    private PagingType paging;
    private PolyString name;
    private UserInterfaceElementVisibilityType visibility;
    private OperationTypeType applicableForOperation;
    private Boolean includeDefaultColumns;
    private String objectCollectionDescription;
    private boolean defaultView;

    private Boolean topLevelView;

    public CompiledObjectCollectionView() {
        this.containerType = null;
        this.viewIdentifier = null;
    }

    public CompiledObjectCollectionView(QName objectType, String viewIdentifier) {
        this.containerType = objectType;
        this.viewIdentifier = viewIdentifier;
    }

    public QName getContainerType() {
        return this.containerType;
    }

    public void setContainerType(QName containerType) {
        this.containerType = containerType;
    }

    public <C extends Containerable> Class<C> getTargetClass(PrismContext prismContext) {
        return this.containerType == null ? null : prismContext.getSchemaRegistry().determineClassForType(this.containerType);
    }

    public String getViewIdentifier() {
        return this.viewIdentifier;
    }

    public void setViewIdentifier(String viewIdentifier) {
        this.viewIdentifier = viewIdentifier;
    }

    public @NotNull List<GuiActionType> getActions() {
        return this.actions;
    }

    public CollectionRefSpecificationType getCollection() {
        return this.collection;
    }

    public void setCollection(CollectionRefSpecificationType collection) {
        this.collection = collection;
    }

    public List<GuiObjectColumnType> getColumns() {
        return this.columns;
    }

    public DisplayType getDisplay() {
        return this.display;
    }

    public void setDisplay(DisplayType display) {
        this.display = display;
    }

    public GuiObjectListViewAdditionalPanelsType getAdditionalPanels() {
        return this.additionalPanels;
    }

    public void setAdditionalPanels(GuiObjectListViewAdditionalPanelsType additionalPanels) {
        this.additionalPanels = additionalPanels;
    }

    public DistinctSearchOptionType getDistinct() {
        return this.distinct;
    }

    public void setDistinct(DistinctSearchOptionType distinct) {
        this.distinct = distinct;
    }

    public Boolean isDisableSorting() {
        return this.disableSorting;
    }

    public Boolean getDisableSorting() {
        return this.disableSorting;
    }

    public void setDisableSorting(Boolean disableSorting) {
        this.disableSorting = disableSorting;
    }

    public Boolean isDisableCounting() {
        return this.disableCounting;
    }

    public void setDisableCounting(Boolean disableCounting) {
        this.disableCounting = disableCounting;
    }

    public SearchBoxConfigurationType getSearchBoxConfiguration() {
        return this.searchBoxConfiguration;
    }

    public void setSearchBoxConfiguration(SearchBoxConfigurationType searchBoxConfiguration) {
        this.searchBoxConfiguration = searchBoxConfiguration;
    }

    public ObjectFilter getFilter() {
        return this.filter;
    }

    public void setFilter(ObjectFilter filter) {
        this.filter = filter;
    }

    public ObjectFilter getDomainFilter() {
        return this.domainFilter;
    }

    public void setDomainFilter(ObjectFilter domainFilter) {
        this.domainFilter = domainFilter;
    }

    public boolean hasDomain() {
        return this.domainFilter != null;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean match(QName expectedObjectType, String expectedViewIdentifier) {
        if (!QNameUtil.match(this.containerType, expectedObjectType)) {
            return false;
        } else {
            return expectedViewIdentifier == null ? this.isDefaultView() : expectedViewIdentifier.equals(this.viewIdentifier);
        }
    }

    public boolean match(QName expectedObjectType) {
        return QNameUtil.match(this.containerType, expectedObjectType);
    }

    private boolean isAllObjectsView() {
        return this.collection == null;
    }

    public void setRefreshInterval(Integer refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public Integer getRefreshInterval() {
        return this.refreshInterval;
    }

    public void setOptions(Collection<SelectorOptions<GetOperationOptions>> options) {
        this.options = options;
    }

    public Collection<SelectorOptions<GetOperationOptions>> getOptions() {
        return this.options;
    }

    public void setDomainOptions(Collection<SelectorOptions<GetOperationOptions>> domainOptions) {
        this.domainOptions = domainOptions;
    }

    public Collection<SelectorOptions<GetOperationOptions>> getDomainOptions() {
        return this.domainOptions;
    }

    public String getObjectCollectionDescription() {
        return this.objectCollectionDescription;
    }

    public void setObjectCollectionDescription(String objectCollectionDescription) {
        this.objectCollectionDescription = objectCollectionDescription;
    }

    public void setPaging(PagingType paging) {
        this.paging = paging;
    }

    public PagingType getPaging() {
        return this.paging;
    }

    public void setVisibility(UserInterfaceElementVisibilityType visibility) {
        this.visibility = visibility;
    }

    public UserInterfaceElementVisibilityType getVisibility() {
        return this.visibility;
    }

    public void setApplicableForOperation(OperationTypeType applicableForOperation) {
        this.applicableForOperation = applicableForOperation;
    }

    public OperationTypeType getApplicableForOperation() {
        return this.applicableForOperation;
    }

    public void setIncludeDefaultColumns(Boolean includeDefaultColumns) {
        this.includeDefaultColumns = includeDefaultColumns;
    }

    public Boolean getIncludeDefaultColumns() {
        return this.includeDefaultColumns;
    }

    public String debugDump(int indent) {
        StringBuilder sb = DebugUtil.createTitleStringBuilderLn(CompiledObjectCollectionView.class, indent);
        DebugUtil.debugDumpWithLabelLn(sb, "containerType", this.containerType, indent + 1);
        DebugUtil.debugDumpWithLabelLn(sb, "viewIdentifier", this.viewIdentifier, indent + 1);
        DebugUtil.debugDumpWithLabelLn(sb, "actions", this.actions, indent + 1);
        DebugUtil.debugDumpWithLabelLn(sb, "columns", this.columns, indent + 1);
        DebugUtil.debugDumpWithLabelLn(sb, "includeDefaultColumns", this.includeDefaultColumns, indent + 1);
        DebugUtil.debugDumpWithLabelToStringLn(sb, "display", this.display, indent + 1);
        DebugUtil.debugDumpWithLabelToStringLn(sb, "additionalPanels", this.additionalPanels, indent + 1);
        DebugUtil.debugDumpWithLabelToStringLn(sb, "distinct", this.distinct, indent + 1);
        DebugUtil.debugDumpWithLabelLn(sb, "disableSorting", this.disableSorting, indent + 1);
        DebugUtil.debugDumpWithLabelLn(sb, "disableCounting", this.disableCounting, indent + 1);
        DebugUtil.debugDumpWithLabelToStringLn(sb, "searchBoxConfiguration", this.searchBoxConfiguration, indent + 1);
        DebugUtil.debugDumpWithLabelLn(sb, "filter", this.filter, indent + 1);
        DebugUtil.debugDumpWithLabel(sb, "domainFilter", this.domainFilter, indent + 1);
        DebugUtil.debugDumpWithLabel(sb, "displayOrder", this.displayOrder, indent + 1);
        DebugUtil.debugDumpWithLabel(sb, "refreshInterval", this.refreshInterval, indent + 1);
        DebugUtil.debugDumpWithLabel(sb, "visibility", this.visibility, indent + 1);
        DebugUtil.debugDumpWithLabel(sb, "applicableForOperation", this.applicableForOperation, indent + 1);
        DebugUtil.debugDumpWithLabel(sb, "objectCollectionDescription", this.objectCollectionDescription, indent + 1);
        DebugUtil.debugDumpWithLabelToStringLn(sb, "paging", this.paging, indent + 1);
        return sb.toString();
    }

    public GuiObjectListViewType toGuiObjectListViewType() {
        GuiObjectListViewType viewType = new GuiObjectListViewType();
        viewType.setIdentifier(this.getViewIdentifier());
        viewType.setType(this.getContainerType());
        viewType.setAdditionalPanels(this.getAdditionalPanels() != null ? this.getAdditionalPanels().clone() : null);
        viewType.setDisplay(this.getDisplay() != null ? this.getDisplay().clone() : null);
        Iterator var2 = this.getColumns().iterator();

        while(var2.hasNext()) {
            GuiObjectColumnType column = (GuiObjectColumnType)var2.next();
            viewType.column(column.clone());
        }

        var2 = this.getActions().iterator();

        while(var2.hasNext()) {
            GuiActionType action = (GuiActionType)var2.next();
            viewType.action(action.clone());
        }

        viewType.setDistinct(this.getDistinct());
        viewType.setDisableSorting(this.isDisableSorting());
        viewType.setDisableCounting(this.isDisableCounting());
        viewType.setSearchBoxConfiguration(this.getSearchBoxConfiguration() != null ? this.getSearchBoxConfiguration().clone() : null);
        viewType.setDisplayOrder(this.getDisplayOrder());
        viewType.setRefreshInterval(this.getRefreshInterval());
        viewType.setPaging(this.getPaging() != null ? this.getPaging().clone() : null);
        viewType.setVisibility(this.getVisibility());
        viewType.setApplicableForOperation(this.getApplicableForOperation());
        viewType.setIncludeDefaultColumns(this.getIncludeDefaultColumns());
        return viewType;
    }

    public boolean isApplicableForOperation(OperationTypeType operationTypeType) {
        if (this.applicableForOperation == null) {
            return true;
        } else {
            return operationTypeType == this.applicableForOperation;
        }
    }

    public boolean isIncludeDefaultColumns() {
        return BooleanUtils.isTrue(this.includeDefaultColumns);
    }

    public boolean isDefaultView() {
        return this.defaultView;
    }

    public void setDefaultView(boolean defaultView) {
        this.defaultView = defaultView;
    }

    public boolean isTopLevelView() {
        return Boolean.TRUE.equals(topLevelView);
    }

    public Boolean getTopLevelView() {
        return topLevelView;
    }

    public void setTopLevelView(boolean topLevelView) {
        this.topLevelView = topLevelView;
    }
}
