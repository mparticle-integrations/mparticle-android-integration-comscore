package com.mparticle.kits;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.comscore.Analytics;
import com.comscore.PartnerConfiguration;
import com.comscore.PublisherConfiguration;
import com.comscore.util.log.LogLevel;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ComscoreKit extends KitIntegration implements KitIntegration.EventListener, KitIntegration.AttributeListener, KitIntegration.ActivityListener {

    private static final String CLIENT_ID = "CustomerC2Value";
    private static final String PRODUCT = "product";
    private static final String PARTNER_ID = "partnerId";
    private static final String COMSCORE_DEFAULT_LABEL_KEY = "name";
    private boolean isEnterprise;

    @Override
    public List<ReportingMessage> leaveBreadcrumb(String breadcrumb) {
        return null;
    }

    @Override
    public List<ReportingMessage> logError(String message, Map<String, String> errorAttributes) {
        return null;
    }

    @Override
    public List<ReportingMessage> logException(Exception exception, Map<String, String> exceptionAttributes, String message) {
        return null;
    }

    @Override
    public List<ReportingMessage> logEvent(MPEvent event) {
        if (!isEnterprise) {
            return null;
        }
        List<ReportingMessage> messages = new LinkedList<ReportingMessage>();
        HashMap<String, String> comscoreLabels;
        Map<String, String> attributes = event.getCustomAttributeStrings();
        if (attributes == null) {
            comscoreLabels = new HashMap<String, String>();
        }else if (!(attributes instanceof HashMap)){
            comscoreLabels = new HashMap<String, String>();
            for (Map.Entry<String, String> entry : attributes.entrySet())
            {
                comscoreLabels.put(entry.getKey(), entry.getValue());
            }
        }else {
            comscoreLabels = (HashMap<String, String>) attributes;
        }
        comscoreLabels.put(COMSCORE_DEFAULT_LABEL_KEY, event.getEventName());
        if (MParticle.EventType.Navigation.equals(event.getEventType())){
            Analytics.notifyViewEvent(comscoreLabels);
        }else{
            Analytics.notifyHiddenEvent(comscoreLabels);
        }
        messages.add(
                ReportingMessage.fromEvent(this,
                        new MPEvent.Builder(event).customAttributes(comscoreLabels).build()
                )
        );
        return messages;
    }

    @Override
    public List<ReportingMessage>  logScreen(String screenName, Map<String, String> eventAttributes) {
        return logEvent(
                new MPEvent.Builder(screenName, MParticle.EventType.Navigation)
                        .customAttributes(eventAttributes)
                        .build()
        );
    }

    @Override
    public void setUserAttribute(String key, String value) {
        if (isEnterprise){
            Analytics.getConfiguration().setPersistentLabel(KitUtils.sanitizeAttributeKey(key), value);
        }
    }

    @Override
    public void setUserAttributeList(String key, List<String> list) {

    }

    @Override
    public boolean supportsAttributeLists() {
        return !isEnterprise;
    }

    @Override
    public void setAllUserAttributes(Map<String, String> attributes, Map<String, List<String>> attributeLists) {
        if (isEnterprise) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                setUserAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void removeUserAttribute(String key) {
        if (isEnterprise){
            Analytics.getConfiguration().removePersistentLabel(KitUtils.sanitizeAttributeKey(key));
        }
    }

    @Override
    public void removeUserIdentity(MParticle.IdentityType identityType) {
        if (isEnterprise){
            Analytics.getConfiguration().removePersistentLabel(identityType.toString());
        }
    }

    @Override
    public List<ReportingMessage> logout() {
        return null;
    }

    @Override
    public void setUserIdentity(MParticle.IdentityType identityType, String id) {
        if (isEnterprise){
            Analytics.getConfiguration().setPersistentLabel(identityType.toString(), id);
        }
    }

    @Override
    public String getName() {
        return "Comscore";
    }

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        PartnerConfiguration partnerConfiguration = new PartnerConfiguration.Builder()
                .partnerId(settings.get(PARTNER_ID))
                .build();
        Analytics.getConfiguration().addClient(partnerConfiguration);

        PublisherConfiguration.Builder builder = new PublisherConfiguration.Builder();
        builder.publisherId(getSettings().get(CLIENT_ID));
        builder.secureTransmission(true);

        if (MParticle.getInstance().getEnvironment() == MParticle.Environment.Development) {
            Analytics.setLogLevel(LogLevel.VERBOSE);
        }
        isEnterprise = "enterprise".equals(getSettings().get(PRODUCT));
        PublisherConfiguration publisherConfiguration = builder.build();
        Analytics.getConfiguration().addClient(publisherConfiguration);
        Analytics.start(context);
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityPaused(Activity activity) {
        Analytics.notifyExitForeground();
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        messageList.add(
                new ReportingMessage(this, ReportingMessage.MessageType.APP_STATE_TRANSITION, System.currentTimeMillis(), null)
        );
        return messageList;
    }

    @Override
    public List<ReportingMessage> onActivitySaveInstanceState(Activity activity, Bundle outState) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityDestroyed(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityCreated(Activity activity, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityResumed(Activity activity) {
        Analytics.notifyEnterForeground();
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        messageList.add(
                new ReportingMessage(this, ReportingMessage.MessageType.APP_STATE_TRANSITION, System.currentTimeMillis(), null)
        );
        return messageList;
    }

    @Override
    public List<ReportingMessage> onActivityStarted(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityStopped(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optOutStatus) {
        if (!optOutStatus) {
            Analytics.getConfiguration().disable();
        }
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        messageList.add(
                new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null)
                        .setOptOut(optOutStatus)
        );
        return messageList;
    }
}