package com.kopirealm.peasyrecyclerview.sample.demo1;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

// TODO Define view holder to PeasyRVInbox Content, find its views
class ModelInbox {

    public enum InboxState {
        Read, Unread, Header, Footer
    }

    String title;
    String message;
    String sender;
    InboxState state;

    private ModelInbox(String title, String message, String sender, InboxState state) {
        this.title = title;
        this.message = message;
        this.sender = sender + "@scmp.com";
        this.state = state;
    }

    private static ModelInbox buildInboxMessage(String title, String message, String sender, boolean read) {
        return new ModelInbox(title, message, sender, (read) ? InboxState.Read : InboxState.Unread);
    }

    static ModelInbox buildInboxHeader() {
        return new ModelInbox("", "", "", InboxState.Header);
    }

    static ModelInbox buildInboxFooter() {
        return new ModelInbox("", "", "", InboxState.Footer);
    }

    static ArrayList<ModelInbox> forgingInboxMessage(JSONObject rootObj) {
        final ArrayList<ModelInbox> inboxMessages = new ArrayList<>();
        if (rootObj != null) {
            final JSONArray contents = rootObj.optJSONArray("contents");
            if (contents != null) {
                for (int i = 0; i < contents.length(); i++) {
                    JSONObject contentObj = contents.optJSONObject(i);
                    if (contentObj != null) {
                        inboxMessages.add(ModelInbox.buildInboxMessage(
                                contentObj.optString("title", ""),
                                contentObj.optString("message", ""),
                                contentObj.optString("sender", ""),
                                contentObj.optBoolean("read", false)
                        ));
                    }
                }
            }
        }
        return inboxMessages;
    }


}