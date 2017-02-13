package com.veryworks.android.myutility;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FiveFragment extends Fragment {

    private static final int REQ_CAMERA = 101;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2; // 그리드 가로개수 초기화

    // 사진정보 데이터 저장소
    List<String> datas = new ArrayList<>();

    RecyclerView recyclerView;
    MyItemRecyclerViewAdapter adapter;

    private static View view = null;

    // 사진촬영후 임시로 저장할 파일 공간
    private Uri fileUri = null;

    public FiveFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FiveFragment newInstance(int columnCount) {
        FiveFragment fragment = new FiveFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view != null)
            return view;

        Context context = getContext();
        view = inflater.inflate(R.layout.fragment_item_list, container, false);
        // 카메라 처리
        Button btnCamera = (Button) view.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(clickListener);

        // 이미지 데이터 불러오기
        datas = loadData();

        // 리스트 처리
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        adapter = new MyItemRecyclerViewAdapter(getContext(), datas);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<String> loadData(){

        List<String> datas = new ArrayList<>();

        // 폰에서 이미지를 가져온후 datas 에 세팅한다
        ContentResolver resolver = getContext().getContentResolver();
        // 1. 데이터 Uri 정의
        Uri target = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 2. projection 정의
        String projection[] = { MediaStore.Images.ImageColumns.DATA }; // 이미지 경로
        // 정렬 추가 - 날짜순 역순 정렬
        String order = MediaStore.Images.ImageColumns.DATE_ADDED +" DESC";

        // 3. 데이터 가져오기
        Cursor cursor = resolver.query(target, projection, null, null, order);
        if(cursor != null) {
            while (cursor.moveToNext()) {
                String uriString = cursor.getString(0);
                datas.add(uriString);
            }
            cursor.close();
        }
        return datas;
    }


    // 리스너 정의
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent intent = null;
        switch(v.getId()){
            case R.id.btnCamera: //카메라 버튼 동작
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 롤리팝 이상 버전에서는 아래 코드를 반영해야 한다.
                // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                    // 저장할 미디어 속성을 정의하는 클래스
                    ContentValues values = new ContentValues(1);
                    // 속성중에 파일의 종류를 정의
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    // 전역변수로 정의한 fileUri에 외부저장소 컨텐츠가 있는 Uri 를 임시로 생성해서 넣어준다.
                    fileUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    // 위에서 생성한 fileUri를 사진저장공간으로 사용하겠다고 설정
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    // Uri에 읽기와 쓰기 권한을 시스템에 요청
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                // --- 여기 까지 컨텐트 uri 강제세팅 ---
                startActivityForResult(intent, REQ_CAMERA);
                break;
        }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQ_CAMERA :
                if (resultCode == RESULT_OK) { // 사진 확인처리됨 RESULT_OK = -1
                    datas = loadData();
                    // 아답터에 변경된 데이터 반영하기
                    adapter = new MyItemRecyclerViewAdapter(getContext(), datas);
                    recyclerView.setAdapter(adapter);
                    // 얘만 호출하면 되야됨...
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
