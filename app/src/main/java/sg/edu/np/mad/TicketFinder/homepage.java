package sg.edu.np.mad.TicketFinder;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

public class homepage extends AppCompatActivity {

    private RecyclerView eventRecyclerView;
    private EventAdapter eventAdapter;
    private RecyclerView verticalRecyclerView;
    private EventAdapter verticalItemAdapter;
    private dbHandler handler = new dbHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Horizontal RecyclerView
        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        eventAdapter = new EventAdapter(homepage.this, getEventList());
        eventRecyclerView.setAdapter(eventAdapter);

        // Vertical RecyclerView
        verticalRecyclerView = findViewById(R.id.verticalRecyclerView);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        verticalItemAdapter = new EventAdapter(homepage.this, getRecoList());
        verticalRecyclerView.setAdapter(verticalItemAdapter);

        // for navbar
        Footer.setUpFooter(this);

        // Load the featured image
        ImageView featuredImage = findViewById(R.id.featuredImage);
        loadFeaturedImage(featuredImage);
    }

    private void loadFeaturedImage(ImageView imageView) {
        handler.getData(new FirestoreCallback<Event>() {
            @Override
            public void onCallback(ArrayList<Event> eventList) {
                if (!eventList.isEmpty()) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(eventList.size());
                    String imageUrl = eventList.get(randomIndex).getImgUrl();
                    Glide.with(homepage.this)
                            .load(imageUrl)
                            .into(imageView);
                }
            }
        });
    }

    private ArrayList<Event> getRecoList() {
        ArrayList<Event> recoList = new ArrayList<>();
        handler.getData(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList eventList) {

                // add the first 5 events from list
                if (eventList.size() > 5) {
                    for (int i = 0; i < 5; i++) {
                        recoList.add((Event) eventList.get(i));
                    }
                } else {
                    recoList.addAll(eventList);
                }
                verticalItemAdapter.notifyDataSetChanged();
            }
        });
        return recoList;
    }
    private ArrayList<Event> getEventList() {
        ArrayList<Event> eventList = new ArrayList<>();
        handler.getData(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList retrievedEventList) {
                eventList.addAll(retrievedEventList);

                eventAdapter.notifyDataSetChanged();
            }
        });
        return eventList;
    }
}