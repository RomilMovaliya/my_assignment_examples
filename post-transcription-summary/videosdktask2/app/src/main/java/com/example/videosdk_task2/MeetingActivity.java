package com.example.videosdk_task2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.Participant;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.lib.JsonUtils;
import live.videosdk.rtc.android.lib.transcription.PostTranscriptionConfig;
import live.videosdk.rtc.android.lib.transcription.SummaryConfig;
import live.videosdk.rtc.android.listeners.MeetingEventListener;

public class MeetingActivity extends AppCompatActivity {

    private Meeting meeting;
    private boolean micEnabled = true;
    private boolean webcamEnabled = true;

    boolean recording = false;

    // Webhook URL where, webhooks are received
    final String webhookUrl = "https://webhook.site/558bd4d3-e649-467a-ab2f-60ecc47608bf";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);


        findViewById(R.id.btnRecording).setOnClickListener(view -> {
            if (!recording) {
                JSONObject config = new JSONObject();
                JSONObject layout = new JSONObject();
                JsonUtils.jsonPut(layout, "type", "GRID");
                JsonUtils.jsonPut(layout, "priority", "SPEAKER");
                JsonUtils.jsonPut(layout, "gridSize", 4);
                JsonUtils.jsonPut(config, "layout", layout);
                JsonUtils.jsonPut(config, "theme", "DARK");
                JsonUtils.jsonPut(config, "mode", "video-and-audio");
                JsonUtils.jsonPut(config, "quality", "high");
                JsonUtils.jsonPut(config, "orientation", "portrait");

                // Post Transcription Configuration
                String prompt = "Write summary in sections like Title, Agenda, Speakers, Action Items, Outlines, Notes and Summary";
                SummaryConfig summaryConfig = new SummaryConfig(true, prompt);
                String modelId = "raman_v1";
                PostTranscriptionConfig transcription = new PostTranscriptionConfig(true, summaryConfig, modelId);

                // Start Recording
                // If you don't have a `webhookUrl` or `awsDirPath`, you should pass null.
                // If you don't want to enable transcription, you can pass null as parameter.
                meeting.startRecording(webhookUrl, null , config, transcription);
            } else {
                // Stop Recording
                meeting.stopRecording();
            }
        });








        final RecyclerView rvParticipants = findViewById(R.id.rvParticipants);
        rvParticipants.setLayoutManager(new GridLayoutManager(this, 2));

        final String token = getIntent().getStringExtra("token");
        final String meetingId = getIntent().getStringExtra("meetingId");
        final String participantName = "John Doe";

        // 1. Configuration VideoSDK with Token
        VideoSDK.config(token);

        // 2. Initialize VideoSDK Meeting
        meeting = VideoSDK.initMeeting(
                MeetingActivity.this, meetingId, participantName,
                micEnabled, webcamEnabled, null, null, false, null, null);

        // **Important**:  Initialize the ParticipantAdapter here after meeting Initializing.
        rvParticipants.setAdapter(new ParticipantAdapter(meeting));

        // 3. Add event listener for listening upcoming events
        meeting.addEventListener(meetingEventListener);

        //4. Join VideoSDK Meeting
        meeting.join();

        ((TextView)findViewById(R.id.tvMeetingId)).setText(meetingId);

        setActionListeners();
    }

    // creating the MeetingEventListener
    private final MeetingEventListener meetingEventListener = new MeetingEventListener() {
        @Override
        public void onMeetingJoined() {
            Log.d("#meeting", "onMeetingJoined()");
        }

        @Override
        public void onMeetingLeft() {
            Log.d("#meeting", "onMeetingLeft()");
            meeting = null;
            if (!isDestroyed()) finish();
        }

        @Override
        public void onParticipantJoined(Participant participant) {
            Toast.makeText(MeetingActivity.this, participant.getDisplayName() + " joined", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onParticipantLeft(Participant participant) {
            Toast.makeText(MeetingActivity.this, participant.getDisplayName() + " left", Toast.LENGTH_SHORT).show();
        }
    };

    private void setActionListeners() {
        // toggle mic
        findViewById(R.id.btnMic).setOnClickListener(view -> {
            if (micEnabled) {
                // this will mute the local participant's mic
                meeting.muteMic();
                Toast.makeText(MeetingActivity.this, "Mic Disabled", Toast.LENGTH_SHORT).show();
            } else {
                // this will unmute the local participant's mic
                meeting.unmuteMic();
                Toast.makeText(MeetingActivity.this, "Mic Enabled", Toast.LENGTH_SHORT).show();
            }
            micEnabled = !micEnabled;
        });

        // toggle webcam
        findViewById(R.id.btnWebcam).setOnClickListener(view -> {
            if (webcamEnabled) {
                // this will disable the local participant webcam
                meeting.disableWebcam();
                Toast.makeText(MeetingActivity.this, "Webcam Disabled", Toast.LENGTH_SHORT).show();
            } else {
                // this will enable the local participant webcam
                meeting.enableWebcam();
                Toast.makeText(MeetingActivity.this, "Webcam Enabled", Toast.LENGTH_SHORT).show();
            }
            webcamEnabled = !webcamEnabled;
        });

        // leave meeting
        findViewById(R.id.btnLeave).setOnClickListener(view -> {
            // this will make the local participant leave the meeting
            meeting.leave();
        });
    }
}
