package techkids.mad3.servicexmlparse;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private List<VnExpressXmlParser.Item> items = null;
    private RecyclerView recyclerView;
    private Intent intent;
    private Bundle bundle;
    private String urlRSS = "http://vnexpress.net/rss/tin-moi-nhat.rss";
    private String urlResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        initComponent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadXMLfromDownloadService();
        setupRecyclerView();
    }

    //khoi tao cac thanh phan cua giao dien
    private void initComponent()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
    }


    //doc RSS XML dung Download Service
    private void loadXMLfromDownloadService()
    {
        //gui link URL RSS cho Service DownloadService xu ly
        intent = new Intent(ItemListActivity.this, DownloadXmlService.class);
        bundle = new Bundle();
        bundle.putString("URL_RSS", urlRSS);
        intent.putExtras(bundle);
        startService(intent);
    }

    //Service xu ly load chuoi URL va download XML ve xu ly (dung Intent va Bundle de day sang cho ItemListActivity)
    private void setupRecyclerView() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                bundle = intent.getBundleExtra("INTENT_ITEMS");
                items = (List<VnExpressXmlParser.Item>) bundle.getSerializable("GET_ITEMS");
                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(items));
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("FILTER_DOWNLOAD_XML_PARSE"));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<VnExpressXmlParser.Item> mValues;

        public SimpleItemRecyclerViewAdapter(List<VnExpressXmlParser.Item> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            if (null == view) {
                Log.d("XMLL", "view = null");
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            if (holder.title == null) {
                Log.d("XMLL", "" + mValues.get(position).title);
                Log.d("XMLL", "" + mValues.get(position).description);
                Log.d("XMLL", "" + mValues.get(position).pubDate);

            }

            String description = mValues.get(position).description;

            holder.title.setText(mValues.get(position).title);
            holder.description.setText(description);
            holder.pubDate.setText(mValues.get(position).pubDate);

            //tim link anh jpg va cat chuoi, sau do load anh jpg voi thu vien Picasso
            int startDescription = description.indexOf("src=\"http://");
            int endDescription = description.indexOf(".jpg");
            final String urlImage = description.substring(startDescription + 5, endDescription + 4);
            Picasso.with(holder.image.getContext()).load(urlImage).into(holder.image);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra("urlDescription", mValues.get(position).link);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView image;
            public final TextView title;
            public final TextView description;
            public final TextView pubDate;
            public VnExpressXmlParser.Item mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                image = (ImageView) view.findViewById(R.id.image);
                title = (TextView) view.findViewById(R.id.title);
                description = (TextView) view.findViewById(R.id.description);
                pubDate = (TextView) view.findViewById(R.id.pubDate);
            }
        }
    }

}
