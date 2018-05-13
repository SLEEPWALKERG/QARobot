package com.example.adam.qarobot;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationBar bottomNavigationBar;
    private QAFragment qaFragment;
    private AcountFragment acountFragment;
    private AdminFragment adminFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (position){
                    case 0:
                        if (qaFragment == null)
                            qaFragment = QAFragment.newInstance("chatroom");
                        fragmentTransaction.replace(R.id.qa_content,qaFragment);
                        break;
                    case 1:
                        if (adminFragment == null)
                            adminFragment = AdminFragment.newInstance("admin");
                        fragmentTransaction.replace(R.id.qa_content,adminFragment);
                        break;
                    case 2:
                        if (acountFragment == null)
                            acountFragment = AcountFragment.newInstance("acount");
                        fragmentTransaction.replace(R.id.qa_content,acountFragment);
                        break;
                }
                fragmentTransaction.commit();
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_launcher_background,"ChatRoom"));
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_launcher_background,"Admin"));
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_launcher_background,"MyAcount"));
        bottomNavigationBar.setFirstSelectedPosition(0);
        bottomNavigationBar.initialise();

        setDefaultFragment();
    }

    private void setDefaultFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //qaFragment = QAFragment.newInstance("qa");
        adminFragment = AdminFragment.newInstance("admin");
        //fragmentTransaction.replace(R.id.qa_content,qaFragment);
        fragmentTransaction.replace(R.id.qa_content,adminFragment);
        fragmentTransaction.commit();
    }
}
