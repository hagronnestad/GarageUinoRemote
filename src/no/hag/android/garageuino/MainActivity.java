package no.hag.android.garageuino;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
//        final ActionBar actionBar = getActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
//        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//            // Create a tab with text corresponding to the page title defined by the adapter.
//            // Also specify this Activity object, which implements the TabListener interface, as the
//            // listener for when this tab is selected.
//            actionBar.addTab(
//                    actionBar.newTab()
//                            .setText(mSectionsPagerAdapter.getPageTitle(i))
//                            .setTabListener(this));
//        }
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
        	switch (i) {
			case 0:
	        	ControlFragment controlFragment = new ControlFragment();
	            return controlFragment;

			case 1:
	        	AboutFragment aboutFragment = new AboutFragment();
	            return aboutFragment;
				
			default:
				return null;
			}

        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Control";
                case 1: return "About";
            }
            return null;
        }
    }


    
    
    
    public static class ControlFragment extends Fragment {
    	updateLCD updateLCDTask;
    	updateLED updateLEDTask;
    	
    	public TextView txtLCD = null;
    	public Button btnButton = null;
    	public CheckBox chkReady = null;
    	public CheckBox chkError = null;
    	public CheckBox chkClosed = null;
    	public CheckBox chkOpen = null;
    	
    	// Change "127.0.0.1" with the Arduinos IP address.
    	public GarageUinoClient garageUino = new GarageUinoClient("127.0.0.1", 15443);
    	
    	
    	
        public ControlFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	Log.i("HAG", "Fragment: onCreateView");
        	
    		View view = inflater.inflate(R.layout.fragment_control, null);
    		txtLCD = (TextView) view.findViewById(R.id.txtLCD);
    		btnButton = (Button) view.findViewById(R.id.btnButton);
    		
    		chkReady = (CheckBox) view.findViewById(R.id.chkReady);
    		chkError = (CheckBox) view.findViewById(R.id.chkError);
    		chkClosed = (CheckBox) view.findViewById(R.id.chkClosed);
    		chkOpen = (CheckBox) view.findViewById(R.id.chkOpen);
    		
    		
    		btnButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					garageUino.pushButton();
				}
			});
    		

    		return view;
        }
        
        @Override
        public void onPause() {
        	super.onPause();
        	
        	updateLCDTask.cancel(true);
        	updateLEDTask.cancel(true);
        }
        
        @Override
        public void onResume() {
        	super.onResume();
        	
        	updateLCDTask = new updateLCD();
        	updateLCDTask.execute();
        	
        	updateLEDTask = new updateLED();
        	updateLEDTask.execute();
        }
        
        
        public class updateLCD extends AsyncTask<Object, Integer, String> {
        	@Override
        	protected void onPreExecute() {
        		Log.i("HAG", "Updating LCD!");
        	}
        	@Override
            protected String doInBackground(Object... na) {
        		try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
				return garageUino.getLCD();
            }
            @Override
            protected void onProgressUpdate(Integer... progress) {
                
            }
            @Override
            protected void onPostExecute(String result) {           	
            	txtLCD.setText(result);
            	
            	updateLCDTask = new updateLCD();
            	updateLCDTask.execute();
            }
        }
        
        public class updateLED extends AsyncTask<Object, Integer, boolean[]> {
        	@Override
        	protected void onPreExecute() {
        		Log.i("HAG", "Updating LED!");
        	}
        	@Override
            protected boolean[] doInBackground(Object... na) {
        		try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
				return garageUino.getLED();
            }
            @Override
            protected void onProgressUpdate(Integer... progress) {
                
            }
            @Override
            protected void onPostExecute(boolean[] result) {           	
            	chkReady.setChecked(result[0] == true);
            	chkError.setChecked(result[1] == true);
            	chkClosed.setChecked(result[2] == true);
            	chkOpen.setChecked(result[3] == true);
            	
            	updateLEDTask = new updateLED();
            	updateLEDTask.execute();
            }
        }
    }
    
    
    
    public static class AboutFragment extends Fragment {
        public AboutFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		View view = inflater.inflate(R.layout.fragment_about, null);
    		return view;
        }
        
        @Override
        public void onPause() {
        	super.onPause();
        }
        
        @Override
        public void onResume() {
        	super.onResume();
        }
    }
}
