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
* Source code 에서 사용
```java
// 메써드 추적 시작 ------
Debug.startMethodTracing("결과파일");

// 메써드 추적 종료
Debug.stopMethodTracing();
```
* 결과파일 에뮬레이터에서 가져오기

adb -s 디바이스명 pull sdcard/결과파일.trace 최종파일.trace

* 결과파일 보기

traceview 결과파일.trace

## MediaStore.Images.ImageColumns
이미지 데이터를 불러오면서 query의 마지막 인자를 사용해서 정렬처리합니다
```java
ContentResolver resolver = getContext().getContentResolver();
// 1. 데이터 Uri 정의
Uri target = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
// 2. projection 정의
String projection[] = { MediaStore.Images.ImageColumns.DATA }; // 이미지 경로
// 정렬 추가 - 날짜순 역순 정렬
// 컬럼명 ASC : 정방향 정렬
// 컬럼명 DESC : 역방향 정렬
String order = MediaStore.Images.ImageColumns.DATE_ADDED +" DESC";

// 3. 데이터 가져오기
Cursor cursor = resolver.query(target, projection, null, null, order);
```