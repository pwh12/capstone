package com.example.animalDev

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.animalDev.R
import com.example.animalDev.databinding.ActivityDetailBinding
import com.example.animalDev.databinding.DialogCustomBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var databaseHelper: DatabaseHelper
    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(this).get(SharedViewModel::class.java)
    }

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mapView = binding.map
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            googleMap = map

            // 예시 주소
            val address: String = intent.getStringExtra("shelterAddr").toString()
            getAddressFromLocationName(this, address)
        }

        // 보호소 이름 설정
        binding.shelterName.setText(intent.getStringExtra("shelterNm"))

        val imageUrl = intent.getStringExtra("image")?.replace("http://", "https://")
        Glide.with(this)
            .load(imageUrl) // 불러올 이미지 url
            .into(binding.animalImage) // 이미지를 넣을 뷰

        binding.kind.text = intent.getStringExtra("type")
        binding.detailAge.text = intent.getStringExtra("age")
        binding.sex.text = intent.getStringExtra("sex")
        binding.color.text = intent.getStringExtra("color")
        binding.weight.text = intent.getStringExtra("weight")
        binding.detailAge.text = intent.getStringExtra("age")
        binding.neuterYn.text = intent.getStringExtra("neuter")
        binding.happend.text = intent.getStringExtra("happendAt")
        binding.place.text = intent.getStringExtra("place")
        binding.start.text = intent.getStringExtra("startDate")
        binding.end.text = intent.getStringExtra("endDate")
        binding.feat.text = intent.getStringExtra("specialMark")
        binding.shelter.text = intent.getStringExtra("shelterNm")
        binding.addr.text = intent.getStringExtra("shelterAddr")
        binding.tel.text = intent.getStringExtra("shelterTel")

        // 전화하기
        binding.call.setOnClickListener{
            val telNumber = intent.getStringExtra("shelterTel").toString()
            Log.d("telephone :", "${telNumber}")
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)  != PackageManager.PERMISSION_GRANTED){
                // 사용자에게 권한을 요청
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CALL_PHONE), 0)

                return@setOnClickListener
                // return이 없으면
                // 승인을 하고나서 다시 어플리케이션 화면으로 돌아갈 수 없다.
            }
            else {
                // 커스텀 다이얼로그 레이아웃을 인플레이트합니다.
                val dialogBinding = DialogCustomBinding.inflate(layoutInflater)
                val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setView(dialogBinding.root)
                    .create()

                dialogBinding.telNum.text = telNumber
                dialogBinding.yes.setOnClickListener{  // 예를 누르면 전화걸기
                    var intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:" + telNumber)
                    startActivity(intent)

                    alertDialog.dismiss()  // 다이얼로그 닫기
                }

                dialogBinding.no.setOnClickListener {   // 아니요 누르면 다이얼로그 닫기
                    alertDialog.dismiss()
                }

                alertDialog.show()
            }
        }

        // 뒤로가기 클릭
        binding.btnBack.setOnClickListener {
            finish()
        }

        // DatabaseHelper 인스턴스 초기화
        databaseHelper = DatabaseHelper(this)
        val animalId = intent.getStringExtra("noticeNo") ?: return

        // 현재 동물의 좋아요 상태를 조회하여 UI 업데이트
        val isLiked = databaseHelper.isLiked(animalId)
        binding.detailHeart.setImageResource(if (isLiked) R.drawable.full_heart else R.drawable.heart)

        binding.detailHeart.setOnClickListener {
            // 좋아요 상태 토글 및 데이터베이스 업데이트
            val newLikeStatus = !isLiked
            databaseHelper.addOrUpdateLike(animalId, newLikeStatus)

            // UI 업데이트
            binding.detailHeart.setImageResource(if (newLikeStatus) R.drawable.full_heart else R.drawable.heart)
        }

//        // 좋아요 상태를 관찰합니다.
//        sharedViewModel.getLikedAnimals().observe(this, { likedSet ->
//            val isLiked = likedSet.contains(animalId)
//            binding.detailHeart.setImageResource(if (isLiked) R.drawable.full_heart else R.drawable.heart)
//        })
//
//        // 좋아요 버튼 클릭 리스너를 설정합니다.
//        binding.detailHeart.setOnClickListener {
//            sharedViewModel.toggleLike(animalId)
//        }
    }

    fun getAddressFromLocationName(context: Context, name: String, maxResults: Int = 5) {
        val geocoder = Geocoder(context)
        try {
            Log.d("Address", "input address : ${name}")

            val addressList = geocoder.getFromLocationName(name, maxResults) ?: listOf()
            if (addressList.isNotEmpty()) {
                val address = addressList[0]
                // 직접 위도와 경도를 변수에 할당
                val latitude : Double = address.latitude
                val longitude : Double = address.longitude

                // onMapReady 내에서 마커와 카메라 위치를 업데이트 하도록 합니다.
                updateMap(latitude, longitude)
            } else {
                Log.d("Geocoding", "No Address Found")
                // 주소가 없다면 사용자에게 알림
                Toast.makeText(context, "No Address Found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Log.e("Geocoding", "Geocoder IOException: ${e.message}")
        }
    }

    fun updateMap(latitude: Double, longitude: Double) {
        val location = LatLng(latitude, longitude)
        googleMap?.addMarker(MarkerOptions().position(location).title("보호소 위치"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    // MapView의 생명주기를 액티비티의 생명주기에 맞추기 위한 메서드들
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0

        val location = LatLng(37.5562,126.9724)
        googleMap?.addMarker(MarkerOptions().position(location).title("보호소 위치"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}
