package org.kaaproject.kaa.server.admin.client.mvp.activity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collections;
import java.util.List;
import org.kaaproject.avro.ui.gwt.client.util.BusyAsyncCallback;
import org.kaaproject.kaa.common.dto.EndpointUserConfigurationDto;
import org.kaaproject.kaa.server.admin.client.KaaAdmin;
import org.kaaproject.kaa.server.admin.client.mvp.ClientFactory;
import org.kaaproject.kaa.server.admin.client.mvp.place.GetUserConfigPlace;
import org.kaaproject.kaa.server.admin.client.mvp.view.GetUserConfigView;
import org.kaaproject.kaa.server.admin.client.servlet.ServletHelper;
import org.kaaproject.kaa.server.admin.client.util.Utils;
import org.kaaproject.kaa.server.admin.shared.schema.SchemaInfoDto;

/**
 * Created by pyshankov on 07.09.16.
 */
public class GetUserConfigActivity extends AbstractDetailsActivity<EndpointUserConfigurationDto, GetUserConfigView, GetUserConfigPlace> {

    private String applicationId;

    public GetUserConfigActivity(GetUserConfigPlace place, ClientFactory clientFactory) {
        super(place, clientFactory);
        this.applicationId = place.getApplicationId();
    }

    @Override
    protected String getEntityId(GetUserConfigPlace place) {
        return null;
    }

    @Override
    protected GetUserConfigView getView(boolean create) {
        return clientFactory.getUserConfigView();
    }

    @Override
    protected EndpointUserConfigurationDto newEntity() {
        return new EndpointUserConfigurationDto();
    }

    @Override
    protected void onEntityRetrieved() {
        KaaAdmin.getDataSource().getUserConfigurationSchemaInfosByApplicationId(applicationId,
                new BusyAsyncCallback<List<SchemaInfoDto>>() {
                    @Override
                    public void onSuccessImpl(List<SchemaInfoDto> result) {
                        Collections.sort(result);
                        SchemaInfoDto schemaInfo = result.get(result.size()-1);
                        detailsView.getConfigurationSchemaInfo().setValue(schemaInfo);
                        detailsView.getConfigurationSchemaInfo().setAcceptableValues(result);

                    }
                    @Override
                    public void onFailureImpl(Throwable caught) {
                        Utils.handleException(caught, detailsView);
                    }
                });
        registrations.add(detailsView.getDownloadUserCongigurationButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                KaaAdmin.getDataSource().findUserConfigurationByExternalUIdAndAppIdAndSchemaVersion(
                        detailsView.getExternalUserId().getValue(),
                        applicationId,
                        detailsView.getConfigurationSchemaInfo().getValue().getVersion(),
                        new BusyAsyncCallback<EndpointUserConfigurationDto>() {
                            @Override
                            public void onSuccessImpl(EndpointUserConfigurationDto result) {
                                ServletHelper.downloadUserConfiguration(
                                        detailsView.getExternalUserId().getValue(),
                                        String.valueOf(detailsView.getConfigurationSchemaInfo().getValue().getVersion()),
                                        applicationId
                                );
                            }
                            @Override
                            public void onFailureImpl(Throwable caught) {
                                Utils.handleException(caught, detailsView);
                            }
                        });

            }
        }));
    }

    @Override
    protected void onSave() {
    }

    @Override
    protected void getEntity(String id, AsyncCallback<EndpointUserConfigurationDto> callback) {
    }

    @Override
    protected void editEntity(EndpointUserConfigurationDto entity, AsyncCallback<EndpointUserConfigurationDto> callback) {
    }

    @Override
    protected void bind(EventBus eventBus) {
        super.bind(eventBus);

    }
}
