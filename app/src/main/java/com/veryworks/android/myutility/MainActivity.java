package com.veryworks.android.myutility;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.veryworks.android.myutility.dummy.DummyContent;

import java.util.Stack;

/*
 * GPS 사용 순서
 * 1. manifest 에 FINE, COARSE 권한추가
 * 2. runtime permission 소스코드에 추가
 * 3. GPS Location Manager 정의
 * 4. GPS 가 켜져있는지 확인. 꺼져있다면 GPS 화면으로 이동
 *
 * 5. Listener 등록
 * 6. Listener 실행
 * 7. Listener 해제
 */
public class MainActivity extends AppCompatActivity implements FiveFragment.OnListFragmentInteractionListener {

    // 탭 및 페이저 속성 정의
    final int TAB_COUNT = 5;
    // 현재 페이지
    private int page_position = 0;

    ViewPager viewPager;
    OneFragment one;
    TwoFragment two;
    ThreeFragment three;
    FourFragment four;
    FiveFragment five;

    // 페이지 이동경로를 저장하는 history stack 변수
    Stack<Integer> pageStack = new Stack<>();

    boolean backPress = false;

    // 위치정보 관리자
    private LocationManager manager;
    public LocationManager getLocationManager(){
        return manager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Method 추적 시작 ------
        // 마시멜로 이상버전에서 사용하기 위해서는 외부저장소 런타임권한 필요
        Debug.startMethodTracing("trace_result");

        // 프래그먼트 init
        one = new OneFragment();
        two = new TwoFragment();
        three = new ThreeFragment();
        four = new FourFragment();
        five = FiveFragment.newInstance(3); // 미리 정해진 그리드 가로축 개수

        // - 탭 Layout 정의
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        // 탭 생성 및 타이틀 입력
        tabLayout.addTab( tabLayout.newTab().setText("계산기") );
        tabLayout.addTab( tabLayout.newTab().setText("단위변환") );
        tabLayout.addTab( tabLayout.newTab().setText("검색") );
        tabLayout.addTab( tabLayout.newTab().setText("현재위치") );
        tabLayout.addTab( tabLayout.newTab().setText("갤러리") );

        // - 프래그먼트 페이저 작성
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        // 아답터 생성
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // 1. 페이저 리스너 : 페이저가 변경되었을때 탭을 바꿔주는 리스너
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // 2. 페이지의 변경사항을 체크한다.
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 뒤로가기를 누르지 않았을때만 stack 에 포지션을 더한다
                if(!backPress){
                    pageStack.push(page_position);
                // 뒤로가기를 눌렀으면 false로 다시 세팅해준다.
                }else{
                    backPress = false;
                }
                page_position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 3. 탭 리스너 : 탭이 변경되었을 때 페이지를 바꿔저는 리스너
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        // - GPS 사용권한 체크
        // 버전체크해서 마시말로우 보다 낮으면 런타임권한 체크를 하지 않는다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            init();
        }

        // Method 추적 종료
        Debug.stopMethodTracing();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }


    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0: fragment = one; break;
                case 1: fragment = two; break;
                case 2: fragment = three; break;
                case 3: fragment = four; break;
                case 4: fragment = five; break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }

    private final int REQ_CODE = 100;

    // 1. 권한체크
    @TargetApi(Build.VERSION_CODES.M) // Target 지정 애너테이션
    private void checkPermission(){
        // 1.1 런타임 권한체크
        if( checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ){
            // 1.2 요청할 권한 목록 작성
            String permArr[] = {android.Manifest.permission.ACCESS_FINE_LOCATION
                    , android.Manifest.permission.ACCESS_COARSE_LOCATION};
            // 1.3 시스템에 권한요청
            requestPermissions(permArr, REQ_CODE);
        }else{
            init();
        }
    }

    // 2. 권한체크 후 콜백 < 사용자가 확인후 시스템이 호출하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_CODE){
            // 2.1 배열에 넘긴 런타임권한을 체크해서 승인이 됬으면
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                // 2.2 프로그램 실행
                init();
            }else{
                Toast.makeText(this, "권한을 허용하지 않으시면 프로그램을 실행할 수 없습니다.", Toast.LENGTH_LONG).show();
                // 선택 : 1 종료, 2 권한체크 다시 물어보기
                finish();
            }
        }
    }

    private void init(){
        // LocationManager 객체를 얻어온다
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // GPS_센서가 켜져있는지 확인
        // 꺼져있다면 GPS 를 켜는 페이지로 이동
        if(!gpsCheck()){
            // - 팝업창 만들기
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            // 1. 팝업창 제목
            alertDialog.setTitle("GPS 켜기");
            // 2. 팝업창 메시지
            alertDialog.setMessage("GPS 꺼져있습니다.\n 설정창으로 이동하시겠습니까?");
            // 3. Yes 버튼 생성
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            // 4. No 버튼 생성
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            // 5. show 함수로 팝업창을 화면에 띄운다.
            alertDialog.show();
        }
    }

    // GPS 가 꺼져있는지 체크 롤리팝 이하버전...
    private boolean gpsCheck(){
        // 롤리팝 이상버전에서는 LocationManager로 GPS 꺼짐 여부 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 롤리팝 이하버전에서는 LOCATION_PROVIDERS_ALLOWED 로 체크
        } else {
            String gps = android.provider.Settings.Secure.getString(getContentResolver()
                    , Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(gps.matches(".*gps.*")){
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        switch (page_position) {
            // webview 페이지에서
            case 2:
                // 뒤로가기가 가능하면 아무런 동작을 하지 않는다
                if (three.goBack()) {
                    // 뒤로가기가 안되면 앱을 닫는다
                } else {
                    goBackStack();
                }
                break;
            // 위의 조건에 해당되지 않는 모든 케이스는 아래 로직을 처리한다.
            default:
                goBackStack();
                break;
        }
    }

    // stack 뒤로가기
    private void goBackStack(){
        // stack 의 사이즈가 0이면 앱을 종료
        if(pageStack.size() < 1){
            super.onBackPressed();
        // stack 에 position 값이 있으면
        }else {
            // View Pager 리스너에서 stack에 더해지는 것을 방지하기 위해 backpress 상태값을 미리 세팅
            backPress = true;
            // 페이지를 stack의 가장 마지막에 있는 위치값으로 이동
            viewPager.setCurrentItem(pageStack.pop());
        }
    }
}
