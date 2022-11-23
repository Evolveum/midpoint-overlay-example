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
package com.evolveum.midpoint.gui.impl.component.menu;

import com.evolveum.midpoint.cases.api.util.QueryUtils;
import com.evolveum.midpoint.gui.api.component.BasePanel;
import com.evolveum.midpoint.gui.api.model.LoadableModel;
import com.evolveum.midpoint.gui.api.page.PageBase;
import com.evolveum.midpoint.gui.api.util.GuiDisplayTypeUtil;
import com.evolveum.midpoint.gui.api.util.WebComponentUtil;
import com.evolveum.midpoint.gui.api.util.WebModelServiceUtils;
import com.evolveum.midpoint.gui.impl.page.admin.AbstractPageObjectDetails;
import com.evolveum.midpoint.gui.impl.page.admin.cases.PageCase;
import com.evolveum.midpoint.gui.impl.page.admin.systemconfiguration.PageSystemConfiguration;
import com.evolveum.midpoint.gui.impl.page.admin.systemconfiguration.page.PageBaseSystemConfiguration;
import com.evolveum.midpoint.gui.impl.page.self.PageRequestAccess;
import com.evolveum.midpoint.gui.impl.page.self.credentials.PageSelfCredentials;
import com.evolveum.midpoint.gui.impl.page.self.dashboard.PageSelfDashboard;
import com.evolveum.midpoint.model.api.AccessCertificationService;
import com.evolveum.midpoint.model.api.authentication.CompiledDashboardType;
import com.evolveum.midpoint.model.api.authentication.CompiledGuiProfile;
import com.evolveum.midpoint.model.api.authentication.CompiledObjectCollectionView;
import com.evolveum.midpoint.prism.query.ObjectQuery;
import com.evolveum.midpoint.prism.query.builder.S_FilterEntryOrEmpty;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.util.logging.LoggingUtils;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.web.application.PageMounter;
import com.evolveum.midpoint.web.component.menu.*;
import com.evolveum.midpoint.web.component.util.VisibleBehaviour;
import com.evolveum.midpoint.web.component.util.VisibleEnableBehaviour;
import com.evolveum.midpoint.web.page.admin.cases.PageCaseWorkItem;
import com.evolveum.midpoint.web.page.admin.cases.PageCaseWorkItemsAll;
import com.evolveum.midpoint.web.page.admin.cases.PageCaseWorkItemsAllocatedToMe;
import com.evolveum.midpoint.web.page.admin.cases.PageWorkItemsClaimable;
import com.evolveum.midpoint.web.page.admin.certification.PageCertCampaigns;
import com.evolveum.midpoint.web.page.admin.certification.PageCertDecisions;
import com.evolveum.midpoint.web.page.admin.certification.PageCertDecisionsAll;
import com.evolveum.midpoint.web.page.admin.certification.PageCertDefinition;
import com.evolveum.midpoint.web.page.admin.certification.PageCertDefinitions;
import com.evolveum.midpoint.web.page.admin.configuration.PageAbout;
import com.evolveum.midpoint.web.page.admin.configuration.PageBulkAction;
import com.evolveum.midpoint.web.page.admin.configuration.PageDebugList;
import com.evolveum.midpoint.web.page.admin.configuration.PageDebugView;
import com.evolveum.midpoint.web.page.admin.configuration.PageEvaluateMapping;
import com.evolveum.midpoint.web.page.admin.configuration.PageImportObject;
import com.evolveum.midpoint.web.page.admin.configuration.PageInternals;
import com.evolveum.midpoint.web.page.admin.configuration.PageRepositoryQuery;
import com.evolveum.midpoint.web.page.admin.home.PageDashboardConfigurable;
import com.evolveum.midpoint.web.page.admin.home.PageDashboardInfo;
import com.evolveum.midpoint.web.page.admin.orgs.PageOrgTree;
import com.evolveum.midpoint.web.page.admin.reports.PageAuditLogViewer;
import com.evolveum.midpoint.web.page.admin.reports.PageCreatedReports;
import com.evolveum.midpoint.web.page.admin.resources.PageConnectorHosts;
import com.evolveum.midpoint.web.page.admin.resources.PageImportResource;
import com.evolveum.midpoint.web.page.admin.resources.PageResourceWizard;
import com.evolveum.midpoint.web.page.admin.server.PageNodes;
import com.evolveum.midpoint.web.page.admin.server.PageTasksCertScheduling;
import com.evolveum.midpoint.web.page.admin.workflow.PageAttorneySelection;
import com.evolveum.midpoint.web.page.admin.workflow.PageWorkItemsAttorney;
import com.evolveum.midpoint.web.page.self.PageSelfConsents;
import com.evolveum.midpoint.web.security.MidPointApplication;
import com.evolveum.midpoint.xml.ns._public.common.common_3.CaseWorkItemType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.DeploymentInformationType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.DisplayType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.IconType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationTypeType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OtherPrivilegesLimitationType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.RichHyperlinkType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import com.evolveum.prism.xml.ns._public.types_3.PolyStringType;

import java.util.*;
import javax.xml.namespace.QName;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public class LeftMenuPanel extends BasePanel<Void> {
    private static final Trace LOGGER = TraceManager.getTrace(LeftMenuPanel.class);
    private static final String DOT_CLASS = LeftMenuPanel.class.getName() + ".";
    private static final String OPERATION_LOAD_WORK_ITEM_COUNT;
    private static final String OPERATION_LOAD_CERT_WORK_ITEM_COUNT;
    private final LoadableModel<String> workItemCountModel = new LoadableModel<String>(false) {
        private static final long serialVersionUID = 1L;

        protected String load() {
            try {
                Task task = LeftMenuPanel.this.getPageBase().createSimpleTask(LeftMenuPanel.OPERATION_LOAD_WORK_ITEM_COUNT);
                S_FilterEntryOrEmpty q = LeftMenuPanel.this.getPrismContext().queryFor(CaseWorkItemType.class);
                ObjectQuery query = QueryUtils.filterForAssignees(q, LeftMenuPanel.this.getPageBase().getPrincipal(), OtherPrivilegesLimitationType.F_APPROVAL_WORK_ITEMS, LeftMenuPanel.this.getPageBase().getRelationRegistry()).and().item(CaseWorkItemType.F_CLOSE_TIMESTAMP).isNull().build();
                Integer workItemCount = LeftMenuPanel.this.getPageBase().getModelService().countContainers(CaseWorkItemType.class, query, (Collection)null, task, task.getResult());
                return workItemCount != null && workItemCount != 0 ? workItemCount.toString() : null;
            } catch (Exception var5) {
                LoggingUtils.logExceptionAsWarning(LeftMenuPanel.LOGGER, "Couldn't load work item count", var5, new Object[0]);
                return null;
            }
        }
    };
    private final LoadableModel<String> certWorkItemCountModel = new LoadableModel<String>(false) {
        private static final long serialVersionUID = 1L;

        protected String load() {
            try {
                AccessCertificationService acs = LeftMenuPanel.this.getPageBase().getCertificationService();
                Task task = LeftMenuPanel.this.getPageBase().createSimpleTask(LeftMenuPanel.OPERATION_LOAD_CERT_WORK_ITEM_COUNT);
                OperationResult result = task.getResult();
                int openCertWorkItems = acs.countOpenWorkItems(LeftMenuPanel.this.getPrismContext().queryFactory().createQuery(), true, (Collection)null, task, result);
                return openCertWorkItems == 0 ? null : Integer.toString(openCertWorkItems);
            } catch (Exception var5) {
                LoggingUtils.logExceptionAsWarning(LeftMenuPanel.LOGGER, "Couldn't load certification work item count", var5, new Object[0]);
                return null;
            }
        }
    };
    private final LoadableModel<List<SideBarMenuItem>> sideBarMenuModel = new LoadableModel<List<SideBarMenuItem>>(false) {
        private static final long serialVersionUID = 1L;

        protected List<SideBarMenuItem> load() {
            return LeftMenuPanel.this.createMenuItems();
        }
    };

    public LeftMenuPanel(String id) {
        super(id);
    }

    protected void onInitialize() {
        super.onInitialize();
        this.initLayout();
    }

    private void initLayout() {
        AjaxLink<String> logo = new AjaxLink<String>("logo") {
            private static final long serialVersionUID = 1L;

            public void onClick(AjaxRequestTarget target) {
                Class<? extends Page> page = MidPointApplication.get().getHomePage();
                this.setResponsePage(page);
            }
        };
        logo.add(new Behavior[]{new VisibleEnableBehaviour(() -> {
            return !this.isCustomLogoVisible();
        }, () -> {
            return this.getPageBase().isLogoLinkEnabled();
        })});
        logo.add(new Behavior[]{AttributeAppender.append("class", () -> {
            return WebComponentUtil.getMidPointSkin().getNavbarCss();
        })});
        this.add(new Component[]{logo});
        AjaxLink<String> customLogo = new AjaxLink<String>("customLogo") {
            private static final long serialVersionUID = 1L;

            public void onClick(AjaxRequestTarget target) {
                Class<? extends Page> page = MidPointApplication.get().getHomePage();
                this.setResponsePage(page);
            }
        };
        customLogo.add(new Behavior[]{AttributeAppender.append("class", () -> {
            return WebComponentUtil.getMidPointSkin().getNavbarCss();
        })});
        customLogo.add(new Behavior[]{new VisibleBehaviour(() -> {
            return this.isCustomLogoVisible();
        })});
        this.add(new Component[]{customLogo});
        final IModel<IconType> logoModel = new IModel<IconType>() {
            private static final long serialVersionUID = 1L;

            public IconType getObject() {
                DeploymentInformationType info = MidPointApplication.get().getDeploymentInfo();
                return info != null ? info.getLogo() : null;
            }
        };
        ExternalImage customLogoImgSrc = new ExternalImage("customLogoImgSrc") {
            protected void buildSrcAttribute(ComponentTag tag, IModel<?> srcModel) {
                tag.put("src", (CharSequence)WebComponentUtil.getIconUrlModel((IconType)logoModel.getObject()).getObject());
            }
        };
        customLogoImgSrc.add(new Behavior[]{new VisibleBehaviour(() -> {
            return logoModel.getObject() != null && StringUtils.isEmpty(((IconType)logoModel.getObject()).getCssClass());
        })});
        customLogo.add(new Component[]{customLogoImgSrc});
        WebMarkupContainer customLogoImgCss = new WebMarkupContainer("customLogoImgCss");
        customLogoImgCss.add(new Behavior[]{new VisibleBehaviour(() -> {
            return logoModel.getObject() != null && StringUtils.isNotEmpty(((IconType)logoModel.getObject()).getCssClass());
        })});
        customLogoImgCss.add(new Behavior[]{new AttributeAppender("class", new IModel<String>() {
            private static final long serialVersionUID = 1L;

            public String getObject() {
                return logoModel.getObject() != null ? ((IconType)logoModel.getObject()).getCssClass() : null;
            }
        })});
        customLogo.add(new Component[]{customLogoImgCss});
        logo.add(new Behavior[]{PageBase.createHeaderColorStyleModel(false)});
        customLogo.add(new Behavior[]{PageBase.createHeaderColorStyleModel(false)});
        SideBarMenuPanel sidebarMenu = new SideBarMenuPanel("menu", this.sideBarMenuModel);
        this.add(new Component[]{sidebarMenu});
    }

    private boolean isCustomLogoVisible() {
        DeploymentInformationType info = MidPointApplication.get().getDeploymentInfo();
        if (info != null && info.getLogo() != null) {
            IconType logo = info.getLogo();
            return StringUtils.isNotEmpty(logo.getImageUrl()) || StringUtils.isNotEmpty(logo.getCssClass());
        } else {
            return false;
        }
    }

    protected List<SideBarMenuItem> createMenuItems() {
        List<SideBarMenuItem> menus = new ArrayList();
        boolean experimentalFeaturesEnabled = WebModelServiceUtils.isEnableExperimentalFeature(this.getPageBase());
        SideBarMenuItem menu = this.createSelfServiceMenu(experimentalFeaturesEnabled);
        this.addSidebarMenuItem(menus, menu);
        menu = this.createMainNavigationMenu(experimentalFeaturesEnabled);
        this.addSidebarMenuItem(menus, menu);
        menu = this.createConfigurationMenu(experimentalFeaturesEnabled);
        this.addSidebarMenuItem(menus, menu);
        menu = this.createAdditionalMenu(experimentalFeaturesEnabled);
        this.addSidebarMenuItem(menus, menu);
        return menus;
    }

    private void addSidebarMenuItem(List<SideBarMenuItem> menus, SideBarMenuItem menu) {
        if (!menu.isEmpty()) {
            menus.add(menu);
        }
    }

    private SideBarMenuItem createSelfServiceMenu(boolean experimentalFeaturesEnabled) {
        SideBarMenuItem menu = new SideBarMenuItem("PageAdmin.menu.selfService", experimentalFeaturesEnabled);
        menu.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.selfDashboard", "fa fa-tachometer-alt", PageSelfDashboard.class));
        PageParameters pageParameters = new PageParameters();
        pageParameters.add("pathParameter", WebModelServiceUtils.getLoggedInFocusOid());
        menu.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.profile", "fa fa-user", WebComponentUtil.resolveSelfPage(), pageParameters));
        menu.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.credentials", "fa fa-shield-alt", PageSelfCredentials.class));
        if (WebModelServiceUtils.getLoggedInFocus() instanceof UserType) {
            menu.addMainMenuItem(this.createMainMenuItem("PageRequestAccess.title", "fas fa-plus-circle", PageRequestAccess.class));
        }

        menu.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.consent", "fa fa-check-square", PageSelfConsents.class));
        return menu;
    }

    private SideBarMenuItem createMainNavigationMenu(boolean experimentalFeaturesEnabled) {
        SideBarMenuItem menu = new SideBarMenuItem("PageAdmin.menu.mainNavigation", experimentalFeaturesEnabled);
        menu.addMainMenuItem(this.createHomeItems());
        addCollectionsMainMenuItems(menu, PageTypes.USER);
        menu.addMainMenuItem(this.createUsersItems());
        addCollectionsMainMenuItems(menu, PageTypes.ORG);
        menu.addMainMenuItem(this.createOrganizationsMenu());
        addCollectionsMainMenuItems(menu, PageTypes.ROLE);
        menu.addMainMenuItem(this.createRolesMenu());
        addCollectionsMainMenuItems(menu, PageTypes.SERVICE);
        menu.addMainMenuItem(this.createServicesItems());
        addCollectionsMainMenuItems(menu, PageTypes.RESOURCE);
        menu.addMainMenuItem(this.createResourcesItems());
        if (this.getPageBase().getCaseManager().isEnabled()) {
            menu.addMainMenuItem(this.createWorkItemsItems());
        }

        menu.addMainMenuItem(this.createCertificationItems());
        menu.addMainMenuItem(this.createServerTasksItems());
        menu.addMainMenuItem(this.createNodesItems());
        menu.addMainMenuItem(this.createReportsItems());
        return menu;
    }

    private MainMenuItem createHomeItems() {
        MainMenuItem homeMenu = this.createMainMenuItem("PageAdmin.menu.dashboard", "fa fa-tachometer-alt");
        homeMenu.addMenuItem(new MenuItem("PageAdmin.menu.dashboard.info", PageDashboardInfo.class));
        List<CompiledDashboardType> dashboards = this.getPageBase().getCompiledGuiProfile().getConfigurableDashboards();
        Iterator var3 = dashboards.iterator();

        while(var3.hasNext()) {
            CompiledDashboardType prismObject = (CompiledDashboardType)var3.next();
            MenuItem dashboardMenu = this.createDashboardMenuItem(prismObject);
            homeMenu.addMenuItem(dashboardMenu);
        }

        return homeMenu;
    }

    private MenuItem createDashboardMenuItem(CompiledDashboardType dashboard) {
        Validate.notNull(dashboard, "Dashboard object is null", new Object[0]);
        if (!WebComponentUtil.getElementVisibility(dashboard.getVisibility())) {
            return null;
        } else {
            String label = this.getDashboardLabel(dashboard);
            StringValue dashboardOidParam = this.getPageBase().getPageParameters().get("pathParameter");
            boolean active = false;
            if (dashboardOidParam != null) {
                active = dashboard.getOid().equals(dashboardOidParam.toString());
            }

            return new MenuItem(label, PageDashboardConfigurable.class, this.createDashboardPageParameters(dashboard), active);
        }
    }

    private String getDashboardLabel(CompiledDashboardType dashboard) {
        String label = null;
        PolyStringType displayType = WebComponentUtil.getCollectionLabel(dashboard.getDisplay());
        if (displayType != null) {
            label = WebComponentUtil.getTranslatedPolyString(displayType);
        }

        if (StringUtils.isBlank(label)) {
            label = WebComponentUtil.getTranslatedPolyString(dashboard.getName());
        }

        return label;
    }

    private PageParameters createDashboardPageParameters(CompiledDashboardType dashboard) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.add("pathParameter", dashboard.getOid());
        return pageParameters;
    }

    private MainMenuItem createUsersItems() {
        MainMenuItem userMenu = this.createMainMenuItem("PageAdmin.menu.top.users", "fa fa-user object-user-color");
        this.createBasicAssignmentHolderMenuItems(userMenu, PageTypes.USER);
        return userMenu;
    }

    private MainMenuItem createOrganizationsMenu() {
        MainMenuItem organizationMenu = this.createMainMenuItem("PageAdmin.menu.top.orgs", "fa fa-building object-org-color");
        MenuItem orgTree = new MenuItem("PageAdmin.menu.top.orgs.tree", "fa fa-building", PageOrgTree.class);
        organizationMenu.addMenuItem(orgTree);
        this.createBasicAssignmentHolderMenuItems(organizationMenu, PageTypes.ORG);
        return organizationMenu;
    }

    private MainMenuItem createRolesMenu() {
        MainMenuItem roleMenu = this.createMainMenuItem("PageAdmin.menu.top.roles", "fe fe-role object-role-color");
        this.createBasicAssignmentHolderMenuItems(roleMenu, PageTypes.ROLE);
        return roleMenu;
    }

    private MainMenuItem createServicesItems() {
        MainMenuItem serviceMenu = this.createMainMenuItem("PageAdmin.menu.top.services", "fa fa-cloud object-service-color");
        this.createBasicAssignmentHolderMenuItems(serviceMenu, PageTypes.SERVICE);
        return serviceMenu;
    }

    private MainMenuItem createResourcesItems() {
        MainMenuItem resourceMenu = this.createMainMenuItem("PageAdmin.menu.top.resources", "fa fa-database object-resource-color");
        this.createBasicAssignmentHolderMenuItems(resourceMenu, PageTypes.RESOURCE);
        resourceMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.resources.import", PageImportResource.class));
        resourceMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.connectorHosts.list", PageConnectorHosts.class));
        return resourceMenu;
    }

    private MainMenuItem createWorkItemsItems() {
        MainMenuItem casesMenu = new MainMenuItem("PageAdmin.menu.top.cases", "fe fe-case_thick") {
            public String getBubbleLabel() {
                return (String)LeftMenuPanel.this.workItemCountModel.getObject();
            }
        };
        this.createBasicAssignmentHolderMenuItems(casesMenu, PageTypes.CASE);
        casesMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.caseWorkItems.listAll", "fa fa-inbox", PageCaseWorkItemsAll.class));
        casesMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.caseWorkItems.list", PageCaseWorkItemsAllocatedToMe.class));
        casesMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.workItems.selectAttorney", PageAttorneySelection.class));
        this.createFocusPageViewMenu(casesMenu, "PageAdmin.menu.top.workItems.listAttorney", PageWorkItemsAttorney.class);
        casesMenu.addMenuItem(new MenuItem("PageWorkItemsClaimable.title", PageWorkItemsClaimable.class));
        this.createFocusPageViewMenu(casesMenu, "PageAdmin.menu.top.case.view", PageCase.class);
        this.createFocusPageViewMenu(casesMenu, "PageAdmin.menu.top.caseWorkItems.view", PageCaseWorkItem.class);
        return casesMenu;
    }

    private MainMenuItem createCertificationItems() {
        MainMenuItem certificationMenu = new MainMenuItem("PageAdmin.menu.top.certification", "fa fa-certificate") {
            private static final long serialVersionUID = 1L;

            public String getBubbleLabel() {
                return (String)LeftMenuPanel.this.certWorkItemCountModel.getObject();
            }
        };
        certificationMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.certification.definitions", PageCertDefinitions.class));
        certificationMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.certification.campaigns", PageCertCampaigns.class));
        PageParameters params = new PageParameters();
        params.add("category", "AccessCertification");
        MenuItem menu = new MenuItem("PageAdmin.menu.top.certification.scheduling", PageTasksCertScheduling.class, params, new Class[0]);
        certificationMenu.addMenuItem(menu);
        certificationMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.certification.allDecisions", PageCertDecisionsAll.class));
        certificationMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.certification.decisions", PageCertDecisions.class));
        MenuItem newCertificationMenu = new MenuItem("PageAdmin.menu.top.certification.newDefinition", "fa fa-plus-circle", PageCertDefinition.class);
        certificationMenu.addMenuItem(newCertificationMenu);
        return certificationMenu;
    }

    private MainMenuItem createServerTasksItems() {
        MainMenuItem tasksMenu = this.createMainMenuItem("PageAdmin.menu.top.serverTasks", "fa fa-tasks object-task-color");
        this.createBasicAssignmentHolderMenuItems(tasksMenu, PageTypes.TASK);
        return tasksMenu;
    }

    private MainMenuItem createNodesItems() {
        MainMenuItem nodesMenu = this.createMainMenuItem("PageAdmin.menu.top.nodes", "fa fa-server object-node-color");
        nodesMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.nodes.list", PageNodes.class));
        return nodesMenu;
    }

    private MainMenuItem createReportsItems() {
        MainMenuItem reportMenu = this.createMainMenuItem("PageAdmin.menu.top.reports", "fa fa-chart-pie");
        this.createBasicAssignmentHolderMenuItems(reportMenu, PageTypes.REPORT);
        reportMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.reports.created", PageCreatedReports.class));
        reportMenu.addMenuItem(new MenuItem("PageAuditLogViewer.menuName", PageAuditLogViewer.class));
        return reportMenu;
    }

    private SideBarMenuItem createConfigurationMenu(boolean experimentalFeaturesEnabled) {
        SideBarMenuItem item = new SideBarMenuItem("PageAdmin.menu.top.configuration", experimentalFeaturesEnabled);
        item.addMainMenuItem(this.createArchetypesItems());
        item.addMainMenuItem(this.createMessageTemplatesItems());
        item.addMainMenuItem(this.createObjectsCollectionItems());
        item.addMainMenuItem(this.createObjectTemplatesItems());
        item.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.top.configuration.bulkActions", "fa fa-bullseye", PageBulkAction.class));
        item.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.top.configuration.importObject", "fa fa-upload", PageImportObject.class));
        item.addMainMenuItem(this.createRepositoryObjectsMenu());
        this.createSystemConfigurationMenu(item);
        item.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.top.configuration.internals", "fa fa-archive", PageInternals.class));
        item.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.top.configuration.repoQuery", "fa fa-search flip-icon flip-icon-margin", PageRepositoryQuery.class));
        item.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.top.configuration.evaluateMapping", "fa fa-cog", PageEvaluateMapping.class));
        item.addMainMenuItem(this.createMainMenuItem("PageAdmin.menu.top.configuration.about", "fa fa-info-circle", PageAbout.class));
        return item;
    }

    private SideBarMenuItem createAdditionalMenu(boolean experimentalFeaturesEnabled) {
        SideBarMenuItem menu = new SideBarMenuItem("PageAdmin.menu.additional", experimentalFeaturesEnabled);
        CompiledGuiProfile userProfile = this.getPageBase().getCompiledGuiProfile();
        List<RichHyperlinkType> menuList = userProfile.getAdditionalMenuLink();
        if (CollectionUtils.isEmpty(menuList)) {
            return menu;
        } else {
            Map<String, Class<? extends WebPage>> urlClassMap = PageMounter.getUrlClassMap();
            if (MapUtils.isEmpty(urlClassMap)) {
                return menu;
            } else {
                Iterator var6 = menuList.iterator();

                while(var6.hasNext()) {
                    RichHyperlinkType link = (RichHyperlinkType)var6.next();
                    if (!StringUtils.isBlank(link.getTargetUrl())) {
                        AdditionalMenuItem item = new AdditionalMenuItem(link, (Class)urlClassMap.get(link.getTargetUrl()));
                        menu.addMainMenuItem(item);
                    }
                }

                return menu;
            }
        }
    }

    private void createBasicAssignmentHolderMenuItems(MainMenuItem mainMenuItem, PageTypes pageDesc) {
        String label = "PageAdmin.menu.top." + pageDesc.getIdentifier() + ".list";
        String icon = pageDesc.getIcon();
        Class<? extends PageBase> page = pageDesc.getListClass();
        boolean isDefaultViewVisible = true;

        Optional<CompiledObjectCollectionView> defaultViewOptional = getPageBase().getCompiledGuiProfile().findAllApplicableObjectCollectionViews(pageDesc.getTypeName()).stream()
                .filter(view -> view.isDefaultView()).findFirst();

        if (defaultViewOptional.isPresent()) {
            CompiledObjectCollectionView defaultView = defaultViewOptional.get();
            isDefaultViewVisible = WebComponentUtil.getElementVisibility(defaultView.getVisibility());
            if (isDefaultViewVisible) {
                DisplayType viewDisplayType = defaultView.getDisplay();

                PolyStringType display = WebComponentUtil.getCollectionLabel(viewDisplayType);
                if (display != null) {
                    label = WebComponentUtil.getTranslatedPolyString(display);
                }

                String iconClass = GuiDisplayTypeUtil.getIconCssClass(viewDisplayType);
                if (StringUtils.isNotEmpty(iconClass)) {
                    icon = iconClass;
                }
            }
        }

        if (isDefaultViewVisible) {
            mainMenuItem.addMenuItem(createObjectListPageMenuItem(label, icon, page));
        }
        this.addCollectionsMenuItems(mainMenuItem, pageDesc.getTypeName(), pageDesc.getListClass());
        if (PageTypes.CASE != pageDesc) {
            this.createFocusPageNewEditMenu(mainMenuItem, "PageAdmin.menu.top." + pageDesc.getIdentifier() + ".new", "PageAdmin.menu.top." + pageDesc.getIdentifier() + ".edit", this.getDetailsPage(pageDesc));
        }

    }

    private Class<? extends PageBase> getDetailsPage(PageTypes pageDesc) {
        return pageDesc.getDetailsPage();
    }

    private boolean isEditForAdminObjectDetails() {
        PageBase pageBase = this.getPageBase();
        if (pageBase instanceof AbstractPageObjectDetails) {
            AbstractPageObjectDetails<?, ?> page = (AbstractPageObjectDetails)pageBase;
            return page.isEditObject();
        } else {
            return false;
        }
    }

    private boolean isEditForResourceWizzard() {
        PageBase pageBase = this.getPageBase();
        if (pageBase instanceof PageResourceWizard) {
            return !((PageResourceWizard)pageBase).isNewResource();
        } else {
            return false;
        }
    }

    private void createFocusPageNewEditMenu(MainMenuItem mainMenuItem, String newKey, String editKey, Class<? extends PageBase> newPageClass) {
        boolean addActive = this.classMatches(newPageClass) && !this.isEditForAdminObjectDetails();
        MenuItem newMenu = new MenuItem(newKey, "fa fa-plus-circle", newPageClass, (PageParameters)null, addActive);
        mainMenuItem.addMenuItem(newMenu);
        boolean editActive = this.classMatches(newPageClass) && this.isEditForAdminObjectDetails();
        if (editActive) {
            MenuItem edit = new MenuItem(editKey, newPageClass);
            edit.setDynamic(true);
            mainMenuItem.addMenuItem(edit);
        }

    }

    private boolean classMatches(Class<? extends PageBase> page) {
        return this.getPageBase().getClass().equals(page);
    }

    private void createFocusPageViewMenu(MainMenuItem mainMenuItem, String viewKey, Class<? extends PageBase> newPageType) {
        boolean editActive = this.classMatches(newPageType);
        if (editActive) {
            MenuItem editMenuItem = new MenuItem(viewKey, newPageType);
            editMenuItem.setDynamic(true);
            mainMenuItem.addMenuItem(editMenuItem);
        }

    }

    private MainMenuItem createMessageTemplatesItems() {
        MainMenuItem item = new MainMenuItem("PageAdmin.menu.top.messageTemplates", "fa fa-book");
        this.createBasicAssignmentHolderMenuItems(item, PageTypes.MESSAGE_TEMPLATES);
        return item;
    }

    private MainMenuItem createArchetypesItems() {
        MainMenuItem item = new MainMenuItem("PageAdmin.menu.top.archetypes", "fe fe-archetype_smooth");
        this.createBasicAssignmentHolderMenuItems(item, PageTypes.ARCHETYPE);
        return item;
    }

    private MainMenuItem createObjectsCollectionItems() {
        MainMenuItem item = new MainMenuItem("PageAdmin.menu.top.objectCollections", "fa fa-filter");
        this.createBasicAssignmentHolderMenuItems(item, PageTypes.OBJECT_COLLECTION);
        return item;
    }

    private MainMenuItem createObjectTemplatesItems() {
        MainMenuItem item = new MainMenuItem("PageAdmin.menu.top.objectTemplates", "fa fa-file-alt");
        this.createBasicAssignmentHolderMenuItems(item, PageTypes.OBJECT_TEMPLATE);
        return item;
    }

    private MainMenuItem createRepositoryObjectsMenu() {
        MainMenuItem repositoryObjectsMenu = this.createMainMenuItem("PageAdmin.menu.top.configuration.repositoryObjects", "fa fa-file-alt");
        repositoryObjectsMenu.addMenuItem(new MenuItem("PageAdmin.menu.top.configuration.repositoryObjectsList", PageDebugList.class));
        boolean editActive = this.classMatches(PageDebugView.class);
        if (editActive) {
            MenuItem editMenuItem = new MenuItem("PageAdmin.menu.top.configuration.repositoryObjectView", PageDebugView.class);
            editMenuItem.setDynamic(true);
            repositoryObjectsMenu.addMenuItem(editMenuItem);
        }

        return repositoryObjectsMenu;
    }

    private MenuItem createObjectListPageMenuItem(String key, String iconClass, Class<? extends PageBase> menuItemPage) {
        return new MenuItem(key, iconClass, menuItemPage) {
            public boolean isMenuActive(WebPage page) {
                PageParameters pageParameters = LeftMenuPanel.this.getPageBase().getPageParameters();
                return page.getClass().equals(this.getPageClass()) && (pageParameters == null || pageParameters.get("collectionName") == null || !StringUtils.isNotEmpty(pageParameters.get("collectionName").toString()) || pageParameters.get("collectionName").toString().equals("null")) ? super.isMenuActive(page) : false;
            }
        };
    }

    private void addCollectionsMainMenuItems(SideBarMenuItem menu, PageTypes pageDesc) {
        List<CompiledObjectCollectionView> objectViews =
                this.getPageBase().getCompiledGuiProfile().findAllApplicableObjectCollectionViews(pageDesc.getTypeName());
        objectViews.forEach((objectView) -> {
            if (!objectView.isDefaultView() && WebComponentUtil.getElementVisibility(objectView.getVisibility())) {
                OperationTypeType operationTypeType = objectView.getApplicableForOperation();
                if (operationTypeType == null || operationTypeType == OperationTypeType.MODIFY) {
                    if (!objectView.isDefaultView() && objectView.isTopLevelView()) {
                        menu.addMainMenuItem(createMenuItemForCollection(objectView, pageDesc.getListClass(), MainMenuItem.class));
                    }
                }
            }
        });
    }

    private void addCollectionsMenuItems(MainMenuItem mainMenuItem, QName type, Class<? extends PageBase> redirectToPage) {
        List<CompiledObjectCollectionView> objectViews =
                this.getPageBase().getCompiledGuiProfile().findAllApplicableObjectCollectionViews(type);
        objectViews.forEach((objectView) -> {
            if (!objectView.isDefaultView() && WebComponentUtil.getElementVisibility(objectView.getVisibility())) {
                OperationTypeType operationTypeType = objectView.getApplicableForOperation();
                if (operationTypeType == null || operationTypeType == OperationTypeType.MODIFY) {
                    if (!objectView.isDefaultView() && !objectView.isTopLevelView()) {
                        mainMenuItem.addCollectionMenuItem(createMenuItemForCollection(objectView, redirectToPage, MenuItem.class));
                    }
                }
            }
        });
    }

    private <M extends BaseMenuItem> M createMenuItemForCollection(
            CompiledObjectCollectionView objectView,
            Class<? extends PageBase> redirectToPage,
            Class<M> returnType) {
        DisplayType viewDisplayType = objectView.getDisplay();
        PageParameters pageParameters = new PageParameters();
        pageParameters.add("collectionName", objectView.getViewIdentifier());
        String label = "MenuItem.noName";
        PolyStringType display = WebComponentUtil.getCollectionLabel(viewDisplayType);
        if (display != null) {
            label = WebComponentUtil.getTranslatedPolyString(display);
        }

        String iconClass = GuiDisplayTypeUtil.getIconCssClass(viewDisplayType);
        M viewMenu = null;
        if (returnType.equals(MenuItem.class)) {
            viewMenu = (M) new MenuItem(
                    label,
                    StringUtils.isEmpty(iconClass) ? "far fa-circle" : iconClass,
                    redirectToPage,
                    pageParameters,
                    this.isObjectCollectionMenuActive(objectView));
            viewMenu.setDisplayOrder(objectView.getDisplayOrder());
        } else if (returnType.equals(MainMenuItem.class)) {
            viewMenu = (M) new MainMenuItem(
                    label,
                    StringUtils.isEmpty(iconClass) ? "far fa-circle" : iconClass,
                    redirectToPage,
                    pageParameters) {
                @Override
                public boolean isMenuActive(WebPage page) {
                    return isObjectCollectionMenuActive(objectView);
                }
            };
        }
        return viewMenu;
    }

    private boolean isObjectCollectionMenuActive(CompiledObjectCollectionView objectView) {
        PageParameters params = this.getPageBase().getPageParameters();
        if (params == null) {
            return false;
        } else {
            StringValue collectionNameParam = params.get("collectionName");
            return collectionNameParam.isEmpty() ? false : collectionNameParam.toString().equals(objectView.getViewIdentifier());
        }
    }

    private void createSystemConfigurationMenu(SideBarMenuItem item) {
        MainMenuItem system = this.createMainMenuItem("PageAdmin.menu.top.configuration.basic", "fa fa-cog", PageSystemConfiguration.class);
        PageBase page = this.getPageBase();
        if (page != null && PageBaseSystemConfiguration.class.isAssignableFrom(page.getClass())) {
            MenuItem menuItem = new MenuItem(page.getClass().getSimpleName() + ".title", page.getClass(), new PageParameters(), new Class[0]);
            system.addMenuItem(menuItem);
        }

        item.addMainMenuItem(system);
    }

    private MainMenuItem createMainMenuItem(String key, String icon) {
        return new MainMenuItem(key, icon);
    }

    private MainMenuItem createMainMenuItem(String key, String icon, Class<? extends PageBase> page) {
        return new MainMenuItem(key, icon, page);
    }

    private MainMenuItem createMainMenuItem(String key, String icon, Class<? extends PageBase> page, PageParameters params) {
        return new MainMenuItem(key, icon, page, params);
    }

    public List<SideBarMenuItem> getItems() {
        SideBarMenuPanel sideBarMenuPanel = (SideBarMenuPanel)this.get("menu");
        return (List)sideBarMenuPanel.getModelObject();
    }

    static {
        OPERATION_LOAD_WORK_ITEM_COUNT = DOT_CLASS + "loadWorkItemCount";
        OPERATION_LOAD_CERT_WORK_ITEM_COUNT = DOT_CLASS + "loadCertificationWorkItemCount";
    }
}
