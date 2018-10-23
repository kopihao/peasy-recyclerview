package com.kopirealm.peasyrecyclerview.sample.demo1;

import java.util.ArrayList;

// TODO Define view holder to PeasyRVInbox Content, find its views
public class ModelInbox {

    public enum InboxState {
        Read, Unread, Header, Footer
    }

    String title = "";
    String message = "";
    String sender = "";
    InboxState state = InboxState.Unread;

    private ModelInbox(String title, String message, String sender, InboxState state) {
        this.title = title;
        this.message = message;
        this.sender = sender + "@scmp.com";
        this.state = state;
    }

    static ModelInbox buildInboxMessage(String title, String message, String sender, boolean read) {
        return new ModelInbox(title, message, sender, (read) ? InboxState.Read : InboxState.Unread);
    }

    static ModelInbox buildInboxHeader() {
        return new ModelInbox("", "", "", InboxState.Header);
    }

    static ModelInbox buildInboxFooter() {
        return new ModelInbox("", "", "", InboxState.Footer);
    }

    static ArrayList<ModelInbox> forgingInboxMessage() {
        final ArrayList<ModelInbox> inboxMessages = new ArrayList<>();
        inboxMessages.clear();
        inboxMessages.add(ModelInbox.buildInboxMessage(
                "Xi holding up trade deal for ‘mafioso’ local officials: top Trump adviser",
                "Trump’s top economic adviser blamed Xi for stalling talks that could end the US-China trade war, and referred to local Chinese government officials as “mafioso dons”, in an interview on Wednesday.\n",
                "international",
                false)
        );

        inboxMessages.add(ModelInbox.buildInboxMessage(
                "‘Our image is at stake’: France fan zone organiser sorry for World Cup mess",
                "As the French community rallies round to pay for HK$40,000 worth of damage to the Kerry Hotel and their reputation, Overseas French Association of Hong Kong (UFE) president Marc Guyon has apologised for the actions of his fellow football fans in the aftermath of Sunday’s",
                "sport",
                false)
        );

        inboxMessages.add(ModelInbox.buildInboxMessage(
                "Google investors largely unfazed by US$5 billion EU fine",
                "Europe’s record US$5 billion fine against Alphabet’s Google, levied on Wednesday, marks the biggest regulatory attack yet on technology giants. But investors and analysts largely shrugged off the ruling’s potential to immediately dent Google’s business.",
                "tech",
                true)
        );

        inboxMessages.add(ModelInbox.buildInboxMessage(
                "China’s yuppies think US$462,300 needed to raise, educate children",
                "China’s emerging affluent are pinched by financial stress as they anticipate setting aside 3.1 million yuan (US$462,300) for their children, including funding their education, marriage and even a first home, according to a survey.",
                "business",
                false)
        );

        inboxMessages.add(ModelInbox.buildInboxMessage(
                "And world’s best airline in 2018 is … bye-bye Qatar, hello Singapore",
                "Win for Singapore Airlines in annual Skytrax survey of 24 million passengers is its first since 2008, and topples Qatar Airways from top spot; Cathay Pacific falls to sixth, Hainan Airlines rises to eighth, and Hong Kong Airlines is in top 20.",
                "lifestyle",
                true)
        );

        inboxMessages.add(ModelInbox.buildInboxMessage(
                "The naughty child monks of Lumbini: documentary tells their story",
                "Swearing, fighting and addicted to cell phones and Bollywood – Chinese filmmaker Kang Yuqi’s new feature, A Little Wisdom, shows kids will be kids, even in a monastery in the birthplace of Buddha",
                "culture",
                false)
        );
        return inboxMessages;
    }


}