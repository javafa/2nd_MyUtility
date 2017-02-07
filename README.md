# My Utility
탭 레이아웃으로 앱을 제작합니다

## Google Map in Fragment
* fragment_layout.xml

레이아웃에서 SupportMapFragment 로 정의합니다.
```xml
<fragment
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:name="com.google.android.gms.maps.SupportMapFragment"
    android:id="@+id/mapView" />
```

* Fragment.java

소스코드에서 OnMapReadyCallBack 인터페이스를 구현합니다
```java
public class SubFragment extends Fragment implements OnMapReadyCallback {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_four, container, false);
        // Fragment 에서 mapview 호출하기
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        // Async 함수에서 Fragment 에서 구현한 OnMapReadyCallBack 이 호출된다
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap map = googleMap;
        // 신사역 좌표
        LatLng seoul = new LatLng(37.516066, 127.019361);
        map.addMarker(new MarkerOptions().position(seoul).title("Sinsa in Seoul"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,17));
    }

}
```

## ADB shell
adb shell 사용법을 익힙니다

* adb devices - device 목록보기
* adb -s 디바이스명 shell - device shell 연결
* adb -s 디바이스면 pull /디바이스디렉토리/원본파일명 최종파일명
* adb -s 디바이스명 push 원본파일명 /디바이스디렉토리/최종파일명

## Method Trace
* WRITE_EXTERNAL_STORAGE 권한설정
```java
// 메써드 추적 시작 ------
Debug.startMethodTracing("trace_result");

// 메써드 추적 종료
Debug.stopMethodTracing();
```