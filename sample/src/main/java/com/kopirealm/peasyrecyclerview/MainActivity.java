package com.kopirealm.peasyrecyclerview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final ArrayList<PeasyRVInbox.ModelInbox> inboxMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FloatingActionButton fab = findViewById(R.id.fab);
        final PeasyRVInbox prvInbox = new PeasyRVInbox(this, (RecyclerView) findViewById(R.id.rvSample), fab, inboxMessages);
        forgingInboxMessage();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Reset Sample Inbox", Snackbar.LENGTH_LONG)
                        .setAction("RESET", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                forgingInboxMessage();
                                prvInbox.setContent(inboxMessages);
                            }
                        }).show();
            }
        });


    }

    private void forgingInboxMessage() {
        inboxMessages.clear();
        inboxMessages.add(new PeasyRVInbox.ModelInbox(
                "Xi holding up trade deal for ‘mafioso’ local officials: top Trump adviser",
                "Trump’s top economic adviser blamed Xi for stalling talks that could end the US-China trade war, and referred to local Chinese government officials as “mafioso dons”, in an interview on Wednesday.\n",
                "scmp_international",
                false)
        );

        inboxMessages.add(new PeasyRVInbox.ModelInbox(
                "‘Our image is at stake’: France fan zone organiser sorry for World Cup mess",
                "As the French community rallies round to pay for HK$40,000 worth of damage to the Kerry Hotel and their reputation, Overseas French Association of Hong Kong (UFE) president Marc Guyon has apologised for the actions of his fellow football fans in the aftermath of Sunday’s",
                "scmp_sport",
                false)
        );

        inboxMessages.add(new PeasyRVInbox.ModelInbox(
                "Google investors largely unfazed by US$5 billion EU fine",
                "Europe’s record US$5 billion fine against Alphabet’s Google, levied on Wednesday, marks the biggest regulatory attack yet on technology giants. But investors and analysts largely shrugged off the ruling’s potential to immediately dent Google’s business.",
                "scmp_tech",
                true)
        );

        inboxMessages.add(new PeasyRVInbox.ModelInbox(
                "China’s yuppies think US$462,300 needed to raise, educate children",
                "China’s emerging affluent are pinched by financial stress as they anticipate setting aside 3.1 million yuan (US$462,300) for their children, including funding their education, marriage and even a first home, according to a survey.",
                "scmp_business",
                false)
        );

        inboxMessages.add(new PeasyRVInbox.ModelInbox(
                "And world’s best airline in 2018 is … bye-bye Qatar, hello Singapore",
                "Win for Singapore Airlines in annual Skytrax survey of 24 million passengers is its first since 2008, and topples Qatar Airways from top spot; Cathay Pacific falls to sixth, Hainan Airlines rises to eighth, and Hong Kong Airlines is in top 20.",
                "scmp_lifestyle",
                true)
        );

        inboxMessages.add(new PeasyRVInbox.ModelInbox(
                "The naughty child monks of Lumbini: documentary tells their story",
                "Swearing, fighting and addicted to cell phones and Bollywood – Chinese filmmaker Kang Yuqi’s new feature, A Little Wisdom, shows kids will be kids, even in a monastery in the birthplace of Buddha",
                "scmp_culture",
                false)
        );
    }
}
